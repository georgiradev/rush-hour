package com.prime.rushhour.service;

import com.prime.rushhour.entity.Activity;
import com.prime.rushhour.exception.ActivityConflictException;
import com.prime.rushhour.exception.ActivityNotFoundException;
import com.prime.rushhour.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivityService {

  private final ActivityRepository activityRepository;

  public Optional<Activity> findById(int id) {
    return Optional.of(
        activityRepository
            .findById(id)
            .orElseThrow(() -> new ActivityNotFoundException("Activity not found with id " + id)));
  }

  public Optional<Activity> findByName(String name) {
    return activityRepository.findByName(name);
  }

  public Optional<Activity> create(Activity activity) {

    if (findByName(activity.getName()).isPresent()) {
      throw new ActivityConflictException("Activity name is already in use");
    }

    return Optional.of(activityRepository.save(activity));
  }

  public List<Activity> findAll(int pageNo, int pageSize, String sortBy) {
    Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

    Page<Activity> pagedResult = activityRepository.findAll(paging);

    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return Collections.emptyList();
    }
  }

  public void delete(int id) {
    findById(id);
    activityRepository.deleteById(id);
  }

  public Optional<Activity> update(Activity activity) {
    Optional<Activity> foundActivity = findById(activity.getId());

    checkIfNameIsTaken(activity);

    Activity currentActivity = foundActivity.get();

    currentActivity.setName(activity.getName());
    currentActivity.setDuration(activity.getDuration());
    currentActivity.setPrice(activity.getPrice());

    return Optional.of(activityRepository.save(currentActivity));
  }

  private void checkIfNameIsTaken(Activity activity) {
    Optional<Activity> foundActivityByName = activityRepository.findByName(activity.getName());

    if(foundActivityByName.isPresent() && activity.getId() != foundActivityByName.get().getId()) {
      throw new ActivityConflictException("Activity name is already in use");
    }
  }
}
