package com.prime.rushhour.service;

import com.prime.rushhour.entity.Role;
import com.prime.rushhour.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  public Optional<Role> findByName(String name) {
    return roleRepository.findByName(name);
  }
}
