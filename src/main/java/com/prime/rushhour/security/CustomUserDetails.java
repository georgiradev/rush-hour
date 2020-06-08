package com.prime.rushhour.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prime.rushhour.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  @Id
  private final int id;

  private final String email;

  @JsonIgnore
  private final String password;

  private final List<GrantedAuthority> authorities;

  public static CustomUserDetails create(User user) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));

    return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
