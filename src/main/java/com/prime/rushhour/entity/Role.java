package com.prime.rushhour.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "role")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;

  @OneToMany(mappedBy = "role", cascade = CascadeType.DETACH)
  private Set<User> users = new HashSet<>();
}
