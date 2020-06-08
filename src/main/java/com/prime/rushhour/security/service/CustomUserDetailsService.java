package com.prime.rushhour.security.service;

import com.prime.rushhour.entity.User;
import com.prime.rushhour.security.CustomUserDetails;
import com.prime.rushhour.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserService userService;

  public CustomUserDetailsService(@Lazy UserService userService) {
    this.userService = userService;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) {
    User user =
        userService
            .findUserByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));

    return CustomUserDetails.create(user);
  }
}
