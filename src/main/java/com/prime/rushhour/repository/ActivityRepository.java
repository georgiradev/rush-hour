package com.prime.rushhour.repository;

import com.prime.rushhour.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    Optional<Activity> findByName(String name);
}
