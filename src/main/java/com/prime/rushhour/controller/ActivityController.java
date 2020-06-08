package com.prime.rushhour.controller;

import com.prime.rushhour.dto.ActivityDtoWithoutAppointments;
import com.prime.rushhour.dto.ActivityRequestDto;
import com.prime.rushhour.dto.ActivityResponseDto;
import com.prime.rushhour.entity.Activity;
import com.prime.rushhour.mapper.Mapper;
import com.prime.rushhour.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1")
public class ActivityController {

  private final ActivityService activityService;
  private final Mapper mapper;

  @GetMapping("/activity")
  public ResponseEntity<ActivityResponseDto> getActivityById(@RequestParam("id") @Min(1) int id) {
    Optional<Activity> foundActivity = activityService.findById(id);

    if (foundActivity.isPresent()) {
      ActivityResponseDto activityDto = mapper.activityToActivityDto(foundActivity.get());
      setAppointments(activityDto, foundActivity.get());

      return ResponseEntity.ok(activityDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  private void setAppointments(ActivityResponseDto activityDto, Activity activity) {
    activityDto.setAppointments(
        activity.getActivityAppointments().stream()
            .map(
                activityAppointment ->
                    mapper.appointmentToAppointmentDtoWithoutActivities(
                        activityAppointment.getAppointment()))
            .collect(Collectors.toList()));
  }

  @GetMapping("/activities")
  public ResponseEntity<List<ActivityResponseDto>> getAllActivities(
      @RequestParam(defaultValue = "0") int pageNo,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "id") String sortBy) {

    List<Activity> activities = activityService.findAll(pageNo, pageSize, sortBy);
    List<ActivityResponseDto> activityDtos =
        activities.stream()
            .map(
                activity -> {
                  ActivityResponseDto activityResponseDto = mapper.activityToActivityDto(activity);
                  setAppointments(activityResponseDto, activity);

                  return activityResponseDto;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(activityDtos);
  }

  @PostMapping("/activity")
  public ResponseEntity<ActivityResponseDto> createActivity(
      @Valid @RequestBody ActivityRequestDto activityDto) {
    Activity activity = mapper.activityDtoToActivity(activityDto);

    Optional<Activity> createdActivity = activityService.create(activity);

    if (createdActivity.isPresent()) {
      ActivityResponseDto activityResponseDto = mapper.activityToActivityDto(activity);
      activityResponseDto.setId(createdActivity.get().getId());
      setAppointments(activityResponseDto, activity);

      return ResponseEntity.status(HttpStatus.CREATED).body(activityResponseDto);
    }

    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @PutMapping("/activity")
  public ResponseEntity<ActivityDtoWithoutAppointments> updateActivityById(
      @Valid @RequestBody ActivityDtoWithoutAppointments activityDto) {

    Activity activity = mapper.ActivityDtoWithoutAppointmentsToActivity(activityDto);

    Optional<Activity> updatedActivity = activityService.update(activity);

    if (updatedActivity.isPresent()) {
      ActivityDtoWithoutAppointments updatedActivityDto =
          mapper.activityToActivityDtoWithoutAppointments(updatedActivity.get());

      return ResponseEntity.ok(updatedActivityDto);
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @DeleteMapping("/activity")
  public ResponseEntity<String> deleteActivityById(@RequestParam("id") @Min(1) int id) {
    activityService.delete(id);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
