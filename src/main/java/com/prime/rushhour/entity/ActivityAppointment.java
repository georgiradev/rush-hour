package com.prime.rushhour.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "activity_appointment")
public class ActivityAppointment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "appointment_id")
  private Appointment appointment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "activity_id")
  private Activity activity;
}
