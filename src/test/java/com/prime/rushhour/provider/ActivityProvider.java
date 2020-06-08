package com.prime.rushhour.provider;

import com.prime.rushhour.entity.Activity;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class ActivityProvider {

  public static Activity getInstance() {
    Activity activity = new Activity();

    activity.setId(1);
    activity.setName("Sky Diving");
    activity.setDuration(Duration.parse("PT60M"));
    activity.setPrice(BigDecimal.valueOf(59.99));

    return activity;
  }

  public static List<Activity> getActivitiesInstance() {
    return Arrays.asList(getInstance(), getInstance());
  }
}
