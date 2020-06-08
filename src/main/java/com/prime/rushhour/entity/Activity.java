package com.prime.rushhour.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "activity")
public class Activity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;

  private Duration duration;

  private BigDecimal price;

  @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ActivityAppointment> activityAppointments = new ArrayList<>();
}
