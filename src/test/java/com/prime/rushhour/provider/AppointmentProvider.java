package com.prime.rushhour.provider;

import com.prime.rushhour.entity.Appointment;
import com.prime.rushhour.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class AppointmentProvider {

  public static Appointment getInstance() {
    Appointment appointment = new Appointment();

    appointment.setId(1);
    appointment.setStartDate(LocalDateTime.now());

    User user = UserProvider.getInstance();
    appointment.setUser(user);

    return appointment;
  }

  public static List<Appointment> getAppointmentsInstance() {
    return Arrays.asList(getInstance(), getInstance());
  }
}
