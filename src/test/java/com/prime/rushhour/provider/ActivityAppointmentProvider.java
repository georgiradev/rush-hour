package com.prime.rushhour.provider;

import com.prime.rushhour.entity.ActivityAppointment;

import java.util.Arrays;
import java.util.List;

public class ActivityAppointmentProvider {

  public static ActivityAppointment getInstance() {
    ActivityAppointment activityAppointment = new ActivityAppointment();

    activityAppointment.setId(1);
    activityAppointment.setActivity(ActivityProvider.getInstance());
    activityAppointment.setAppointment(AppointmentProvider.getInstance());

    return activityAppointment;
  }

  public static List<ActivityAppointment> getActivityAppointmentsInstance() {
    return Arrays.asList(getInstance(), getInstance());
  }
}
