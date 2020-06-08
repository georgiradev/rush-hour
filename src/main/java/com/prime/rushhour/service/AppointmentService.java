package com.prime.rushhour.service;

import com.prime.rushhour.entity.ActivityAppointment;
import com.prime.rushhour.entity.Appointment;
import com.prime.rushhour.entity.User;
import com.prime.rushhour.exception.AppointmentNotFoundException;
import com.prime.rushhour.exception.OverlappingAppointmentsException;
import com.prime.rushhour.exception.UnauthorizedActionException;
import com.prime.rushhour.exception.UserNotFoundException;
import com.prime.rushhour.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final UserService userService;
  private final UserDetailsService userDetailsService;

  public List<Appointment> findAll(int pageNo, int pageSize, String sortBy) {

    Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

    Page<Appointment> pagedResult = appointmentRepository.findAll(paging);

    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return Collections.emptyList();
    }
  }

  public Optional<Appointment> findById(int id) {
    return Optional.of(
        appointmentRepository
            .findById(id)
            .orElseThrow(
                () -> new AppointmentNotFoundException("Appointment not found with id " + id)));
  }

  public Optional<Appointment> create(Appointment appointment, HttpServletRequest request) {

    String userPrincipalEmail = request.getUserPrincipal().getName();
    Optional<User> userOptional = userService.findUserByEmail(userPrincipalEmail);

    userOptional.ifPresent(appointment::setUser);

    appointment.setEndDate(calculateEndDate(appointment));

    List<Integer> activityIdList =
        appointment.getActivityAppointments().stream()
            .map(p -> p.getActivity().getId())
            .collect(Collectors.toList());

    List<Appointment> overlappingAppointments =
        appointmentRepository.findOverlappingAppointments(
            appointment.getStartDate(),
            appointment.getEndDate(),
            appointment.getUser().getId(),
            activityIdList);

    if (!overlappingAppointments.isEmpty()) {
      throw new OverlappingAppointmentsException(
          "There are overlapping appointments. Please choose another date.");
    }

    return Optional.of(appointmentRepository.save(appointment));
  }

  public Optional<Appointment> update(Appointment appointment, HttpServletRequest request) {

    User user = checkUserPermission(request, "update");

    Appointment appointmentToUpdate =
        prepareAppointmentForUpdate(user.getId(), appointment)
            .orElseThrow(
                () ->
                    new AppointmentNotFoundException(
                        "Appointment not found with id " + appointment.getId()));

    appointmentToUpdate.setEndDate(calculateEndDate(appointmentToUpdate));

    List<Integer> activityIdList =
        appointmentToUpdate.getActivityAppointments().stream()
            .map(p -> p.getActivity().getId())
            .collect(Collectors.toList());

    List<Appointment> overlappingAppointments =
        appointmentRepository
            .findOverlappingAppointments(
                appointmentToUpdate.getStartDate(),
                appointmentToUpdate.getEndDate(),
                appointmentToUpdate.getUser().getId(),
                activityIdList)
            .stream()
            .filter(p -> p.getId() != appointment.getId())
            .collect(Collectors.toList());

    if (!overlappingAppointments.isEmpty()) {
      throw new OverlappingAppointmentsException(
          "There are overlapping appointments. Please choose another date.");
    }

    return Optional.of(appointmentRepository.save(appointmentToUpdate));
  }

  private Optional<Appointment> prepareAppointmentForUpdate(int id, Appointment appointment) {

    Optional<Appointment> appointmentToUpdate = appointmentRepository.findById(id);

    if (appointmentToUpdate.isEmpty()) {
      throw new AppointmentNotFoundException("Appointment not found with id " + id);
    }

    List<ActivityAppointment> toRemove = new ArrayList<>();
    for (ActivityAppointment appointmentActivityToUpdate :
        appointmentToUpdate.get().getActivityAppointments()) {
      Optional<ActivityAppointment> appointmentActivity =
          appointment.getActivityAppointments().stream()
              .filter(
                  p ->
                      p.getActivity().getId()
                          == (appointmentActivityToUpdate.getActivity().getId()))
              .findFirst();
      if (appointmentActivity.isPresent()) {
        appointment.getActivityAppointments().remove(appointmentActivity.get());
      } else {
        toRemove.add(appointmentActivityToUpdate);
      }
    }

    for (ActivityAppointment activityAppointment : toRemove) {
      appointmentToUpdate.get().removeActivityAppointment(activityAppointment);
    }

    for (ActivityAppointment activityAppointment : appointment.getActivityAppointments()) {
      activityAppointment.setAppointment(appointmentToUpdate.get());
      appointmentToUpdate.get().addActivityAppointment(activityAppointment);
    }

    appointmentToUpdate.get().setStartDate(appointment.getStartDate());

    return appointmentToUpdate;
  }

  public void delete(int id, HttpServletRequest request) {
    findById(id);
    checkUserPermission(request, "delete");
    appointmentRepository.deleteById(id);
  }

  private LocalDateTime calculateEndDate(Appointment appointment) {
    Duration totalDuration = Duration.ofSeconds(0);
    for (ActivityAppointment activityAppointment : appointment.getActivityAppointments()) {
      totalDuration = totalDuration.plus(activityAppointment.getActivity().getDuration());
    }

    return appointment.getStartDate().plus(totalDuration);
  }

  private User checkUserPermission(HttpServletRequest request, String action) {
    String loggedInUserEmail = request.getUserPrincipal().getName();
    UserDetails userDetails = userDetailsService.loadUserByUsername(loggedInUserEmail);

    User user =
        userService
            .findUserByEmail(loggedInUserEmail)
            .orElseThrow(() -> new UserNotFoundException("User not found with email " + loggedInUserEmail));

    if (!userDetails.getUsername().equals(user.getEmail())
        && !userDetails.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().contains("ROLE_ADMIN"))) {
      throw new UnauthorizedActionException(
          "You don't have permission to " + action + " account that isn't yours.");
    }
    return user;
  }
}
