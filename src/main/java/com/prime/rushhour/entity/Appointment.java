package com.prime.rushhour.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "appointment")
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "appointment", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<ActivityAppointment> activityAppointments = new ArrayList<>();

  public void addActivityAppointment(ActivityAppointment activityAppointment) {
    activityAppointments.add(activityAppointment);
  }

  public void removeActivityAppointment(ActivityAppointment appointmentActivity) {
    activityAppointments.remove(appointmentActivity);
  }
}
