package com.prime.rushhour.controller;

import com.prime.rushhour.dto.ActivityIdDto;
import com.prime.rushhour.dto.AppointmentRequestDto;
import com.prime.rushhour.dto.AppointmentResponseDto;
import com.prime.rushhour.entity.Activity;
import com.prime.rushhour.entity.ActivityAppointment;
import com.prime.rushhour.entity.Appointment;
import com.prime.rushhour.exception.ActivityNotFoundException;
import com.prime.rushhour.mapper.Mapper;
import com.prime.rushhour.service.ActivityService;
import com.prime.rushhour.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1")
public class AppointmentController {

  private final Mapper mapper;
  private final AppointmentService appointmentService;
  private final ActivityService activityService;

  @GetMapping("/appointment")
  public ResponseEntity<AppointmentResponseDto> getAppointmentById(
      @RequestParam("id") @Min(1) int id) {
    Optional<Appointment> foundAppointment = appointmentService.findById(id);

    if (foundAppointment.isPresent()) {
      AppointmentResponseDto appointmentResponseDto =
          mapper.appointmentToAppointmentDto(foundAppointment.get());
      setActivities(foundAppointment.get(), appointmentResponseDto);

      return ResponseEntity.ok(appointmentResponseDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @GetMapping("/appointments")
  public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments(
      @RequestParam(defaultValue = "0") int pageNo,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "id") String sortBy) {

    List<Appointment> appointments = appointmentService.findAll(pageNo, pageSize, sortBy);
    List<AppointmentResponseDto> appointmentResponseDtos =
        appointments.stream()
            .map(
                appointment -> {
                  AppointmentResponseDto appointmentResponseDto =
                      mapper.appointmentToAppointmentDto(appointment);
                  appointmentResponseDto.setActivities(
                      appointment.getActivityAppointments().stream()
                          .map(
                              activityAppointment ->
                                  mapper.activityToActivityDtoWithoutAppointments(
                                      activityAppointment.getActivity()))
                          .collect(Collectors.toList()));

                  return appointmentResponseDto;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(appointmentResponseDtos);
  }

  @PostMapping("/appointment")
  public ResponseEntity<AppointmentResponseDto> createAppointment(
      @Valid @RequestBody AppointmentRequestDto appointmentRequestDto, HttpServletRequest request) {

    Appointment appointment = mapper.appointmentDtoToAppointment(appointmentRequestDto);

    prepareAppointment(appointmentRequestDto, appointment);

    Optional<Appointment> createdAppointment = appointmentService.create(appointment, request);

    if (createdAppointment.isPresent()) {
      AppointmentResponseDto appointmentResponseDto =
          mapper.appointmentToAppointmentDto(createdAppointment.get());
      setActivities(createdAppointment.get(), appointmentResponseDto);

      return ResponseEntity.status(HttpStatus.CREATED).body(appointmentResponseDto);
    }

    return new ResponseEntity<>(HttpStatus.CONFLICT);
  }

  private void setActivities(
      Appointment appointment, AppointmentResponseDto appointmentResponseDto) {
    appointmentResponseDto.setActivities(
        appointment.getActivityAppointments().stream()
            .map(
                activityAppointment ->
                    mapper.activityToActivityDtoWithoutAppointments(
                        activityAppointment.getActivity()))
            .collect(Collectors.toList()));
  }

  @PutMapping("/appointment")
  public ResponseEntity<AppointmentResponseDto> updateAppointmentById(
      @Valid @RequestBody AppointmentResponseDto appointmentResponseDto,
      HttpServletRequest request) {

    Appointment appointment = mapper.appointmentResponseDtoToAppointment(appointmentResponseDto);

    Optional<Appointment> updatedAppointment = appointmentService.update(appointment, request);

    if (updatedAppointment.isPresent()) {
      AppointmentResponseDto appointmentDto =
          mapper.appointmentToAppointmentDto(updatedAppointment.get());
      setActivities(updatedAppointment.get(), appointmentResponseDto);

      return ResponseEntity.ok(appointmentDto);
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @DeleteMapping("/appointment")
  public ResponseEntity<String> deleteAppointmentById(
      @RequestParam("id") @Min(1) int id, HttpServletRequest request) {
    appointmentService.delete(id, request);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private void prepareAppointment(
      AppointmentRequestDto appointmentRequestDto, Appointment appointment) {

    for (ActivityIdDto activityIdDto : appointmentRequestDto.getActivities()) {

      Activity foundActivity =
          activityService
              .findById(activityIdDto.getId())
              .orElseThrow(
                  () ->
                      new ActivityNotFoundException(
                          "Activity not found with id " + activityIdDto.getId()));

      ActivityAppointment activityAppointment = new ActivityAppointment();
      activityAppointment.setActivity(foundActivity);
      activityAppointment.setAppointment(appointment);
      appointment.getActivityAppointments().add(activityAppointment);
    }
  }
}
