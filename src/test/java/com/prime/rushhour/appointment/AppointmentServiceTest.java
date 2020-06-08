package com.prime.rushhour.appointment;

import com.prime.rushhour.exception.UnauthorizedActionException;
import com.prime.rushhour.provider.ActivityAppointmentProvider;
import com.prime.rushhour.entity.Appointment;
import com.prime.rushhour.entity.User;
import com.prime.rushhour.exception.AppointmentNotFoundException;
import com.prime.rushhour.exception.OverlappingAppointmentsException;
import com.prime.rushhour.provider.AppointmentProvider;
import com.prime.rushhour.repository.AppointmentRepository;
import com.prime.rushhour.service.AppointmentService;
import com.prime.rushhour.service.UserService;
import com.prime.rushhour.provider.UserDetailsProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

  @InjectMocks private AppointmentService appointmentService;

  @Mock private AppointmentRepository appointmentRepository;

  @Mock private UserService userService;

  @Mock private HttpServletRequest request;

  @Mock private UserDetailsService userDetailsService;

  @Test
  void testAppointmentCreation() {
    Appointment appointment = AppointmentProvider.getInstance();
    appointment.setActivityAppointments(
        ActivityAppointmentProvider.getActivityAppointmentsInstance());

    User user = appointment.getUser();
    user.setAppointments(Collections.singletonList(appointment));

    Principal userPrincipal = user::getEmail;

    when(this.request.getUserPrincipal()).thenReturn(userPrincipal);
    when(this.userService.findUserByEmail(any(String.class))).thenReturn(Optional.of(user));
    when(this.appointmentRepository.save(appointment)).thenReturn(appointment);

    Optional<Appointment> createdAppointment = appointmentService.create(appointment, request);

    assertEquals(Optional.of(appointment), createdAppointment);
  }

  @Test
  void testAppointmentCreationOverlappingAppointmentsShouldFail() {
    Appointment appointment = AppointmentProvider.getInstance();
    appointment.setActivityAppointments(
        ActivityAppointmentProvider.getActivityAppointmentsInstance());

    User user = appointment.getUser();
    appointment.setUser(user);
    user.setAppointments(Collections.singletonList(appointment));

    Principal userPrincipal = user::getEmail;
    List<Appointment> appointments = AppointmentProvider.getAppointmentsInstance();

    when(this.request.getUserPrincipal()).thenReturn(userPrincipal);
    when(this.userService.findUserByEmail(any(String.class))).thenReturn(Optional.of(user));
    when(this.appointmentRepository.findOverlappingAppointments(
            any(LocalDateTime.class), any(LocalDateTime.class), any(Integer.class), any()))
        .thenReturn(appointments);

    assertThrows(
        OverlappingAppointmentsException.class,
        () -> appointmentService.create(appointment, request));
  }

  @Test
  void testFindAppointmentById() {
    Appointment appointment = AppointmentProvider.getInstance();

    when(this.appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
    Optional<Appointment> foundAppointment = appointmentService.findById(appointment.getId());

    assertEquals(Optional.of(appointment), foundAppointment);
  }

  @Test
  void testFindAppointmentByNonExistingIdShouldFail() {
    Appointment appointment = AppointmentProvider.getInstance();

    when(this.appointmentRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(
        AppointmentNotFoundException.class, () -> appointmentService.findById(appointment.getId()));
  }

  @Test
  void testFindAllAppointments() {
    List<Appointment> appointments = AppointmentProvider.getAppointmentsInstance();
    Page<Appointment> paging = new PageImpl<>(appointments);

    when(this.appointmentRepository.findAll(any(Pageable.class))).thenReturn(paging);
    List<Appointment> foundAppointments = appointmentService.findAll(0, 10, "id");

    assertEquals(appointments, foundAppointments);
  }

  @Test
  void testFindAllAppointmentsButNoContentFound() {
    List<Appointment> appointments = new ArrayList<>();
    Page<Appointment> paging = new PageImpl<>(appointments);

    when(this.appointmentRepository.findAll(any(Pageable.class))).thenReturn(paging);
    List<Appointment> foundAppointments = appointmentService.findAll(0, 10, "id");

    assertEquals(appointments, foundAppointments);
  }

  @Test
  void testUpdateAppointment() {
    Appointment appointment = AppointmentProvider.getInstance();
    User user = appointment.getUser();
    user.setAppointments(Collections.singletonList(appointment));

    Principal userPrincipal = user::getEmail;
    UserDetails userDetails = new UserDetailsProvider();

    when(userService.findUserByEmail(any(String.class))).thenReturn(Optional.of(user));
    when(request.getUserPrincipal()).thenReturn(userPrincipal);
    when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
    when(appointmentRepository.findById(any(Integer.class))).thenReturn(Optional.of(appointment));
    when(appointmentRepository.save(appointment)).thenReturn(appointment);

    Optional<Appointment> updatedAppointment = appointmentService.update(appointment, request);

    assertEquals(Optional.of(appointment), updatedAppointment);
  }

  @Test
  void testUpdateAppointmentByNonExistingIdShouldFail() {
    Appointment appointment = AppointmentProvider.getInstance();
    User user = appointment.getUser();
    user.setAppointments(Collections.singletonList(appointment));

    Principal userPrincipal = user::getEmail;
    UserDetails userDetails = new UserDetailsProvider();

    when(userService.findUserByEmail(any(String.class))).thenReturn(Optional.of(user));
    when(request.getUserPrincipal()).thenReturn(userPrincipal);
    when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
    when(appointmentRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

    assertThrows(AppointmentNotFoundException.class, () -> appointmentService.update(appointment, request));
  }

  @Test
  void testDeleteAppointment() {
    Appointment appointment = AppointmentProvider.getInstance();

    User user = appointment.getUser();
    Principal userPrincipal = user::getEmail;
    UserDetails userDetails = new UserDetailsProvider();

    when(userService.findUserByEmail(any(String.class))).thenReturn(Optional.of(user));
    when(request.getUserPrincipal()).thenReturn(userPrincipal);
    when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
    when(appointmentRepository.findById(any(Integer.class))).thenReturn(Optional.of(appointment));
    appointmentService.delete(appointment.getId(), request);
    when(appointmentRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

    assertThrows(AppointmentNotFoundException.class, () -> appointmentService.delete(appointment.getId(), request));
  }

  @Test
  void testDeleteAppointmentFromUnauthorizedAccountShouldFail() {
    Appointment appointment = AppointmentProvider.getInstance();

    User user = appointment.getUser();
    user.setEmail("hacker@abv.bg");
    Principal userPrincipal = user::getEmail;
    UserDetails userDetails = new UserDetailsProvider();

    when(userService.findUserByEmail(any(String.class))).thenReturn(Optional.of(user));
    when(request.getUserPrincipal()).thenReturn(userPrincipal);
    when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
    when(appointmentRepository.findById(any(Integer.class))).thenReturn(Optional.of(appointment));

    assertThrows(UnauthorizedActionException.class, () -> appointmentService.delete(appointment.getId(), request));
  }
}
