package com.prime.rushhour.activity;

import com.prime.rushhour.entity.Activity;
import com.prime.rushhour.exception.ActivityConflictException;
import com.prime.rushhour.exception.ActivityNotFoundException;
import com.prime.rushhour.provider.ActivityProvider;
import com.prime.rushhour.repository.ActivityRepository;
import com.prime.rushhour.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

  @InjectMocks private ActivityService activityService;

  @Mock private ActivityRepository activityRepository;

  @Test
  void testActivityCreation() {
    Activity activity = ActivityProvider.getInstance();

    when(activityRepository.findByName(any(String.class))).thenReturn(Optional.empty());
    when(activityRepository.save(activity)).thenReturn(activity);

    Optional<Activity> createdActivity = activityService.create(activity);

    assertEquals(Optional.of(activity), createdActivity);
  }

  @Test
  void testActivityCreationDuplicateNameShouldFail() {
    Activity activity = ActivityProvider.getInstance();

    when(activityRepository.findByName(any(String.class))).thenReturn(Optional.of(activity));

    assertThrows(ActivityConflictException.class, () -> activityService.create(activity));
  }

  @Test
  void testFindActivityById() {
    Activity activity = ActivityProvider.getInstance();

    when(activityRepository.findById(any(Integer.class))).thenReturn(Optional.of(activity));
    Optional<Activity> foundActivity = activityService.findById(activity.getId());

    assertEquals(Optional.of(activity), foundActivity);
  }

  @Test
  void testFindActivityByNonExistingIdShouldFail() {
    Activity activity = ActivityProvider.getInstance();

    when(activityRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

    assertThrows(ActivityNotFoundException.class, () -> activityService.findById(activity.getId()));
  }

  @Test
  void testFindAllActivities() {
    List<Activity> activities = ActivityProvider.getActivitiesInstance();
    Page<Activity> paging = new PageImpl<>(activities);

    when(activityRepository.findAll(any(Pageable.class))).thenReturn(paging);
    List<Activity> foundActivities = activityService.findAll(0, 10, "id");

    assertEquals(activities, foundActivities);
  }

  @Test
  void testFindAllActivitiesButNoContentFound() {
    List<Activity> activities = new ArrayList<>();
    Page<Activity> paging = new PageImpl<>(activities);

    when(activityRepository.findAll(any(Pageable.class))).thenReturn(paging);
    List<Activity> foundActivities = activityService.findAll(0, 10, "id");

    assertEquals(activities, foundActivities);
  }

  @Test
  void testActivityUpdate() {
    Activity activity = ActivityProvider.getInstance();

    when(activityRepository.findById(any(Integer.class))).thenReturn(Optional.of(activity));
    when(activityRepository.save(any(Activity.class))).thenReturn(activity);

    Optional<Activity> updatedActivity = activityService.update(activity);

    assertEquals(Optional.of(activity), updatedActivity);
  }

  @Test
  void testActivityUpdateOfNonExistingActivityShouldFail() {
    Activity activity = ActivityProvider.getInstance();

    when(activityRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

    assertThrows(
        ActivityNotFoundException.class, () -> activityService.update(activity));
  }

  @Test
  void testActivityUpdateNonExistingShouldFail() {
    Activity activity = ActivityProvider.getInstance();

    when(activityRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

    assertThrows(
        ActivityNotFoundException.class, () -> activityService.update(activity));
  }

  @Test
  void testDeleteActivity() {
    Activity activity = ActivityProvider.getInstance();

    when(activityRepository.findById(any(Integer.class))).thenReturn(Optional.of(activity));
    activityService.delete(activity.getId());
    when(activityRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

    assertThrows(ActivityNotFoundException.class, () -> activityService.delete(activity.getId()));
  }

  @Test
  void testDeleteActivityByNonExistingIdShouldFail() {
    Activity activity = ActivityProvider.getInstance();

    when(activityRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

    assertThrows(ActivityNotFoundException.class, () -> activityService.delete(activity.getId()));
  }
}
