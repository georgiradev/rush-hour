package com.prime.rushhour.service;

import com.prime.rushhour.dto.LoginRequestDto;
import com.prime.rushhour.dto.UserResponseDtoWithoutAppointments;
import com.prime.rushhour.entity.Role;
import com.prime.rushhour.entity.User;
import com.prime.rushhour.exception.*;
import com.prime.rushhour.repository.UserRepository;
import com.prime.rushhour.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserDetailsService userDetailsService;
  private final RoleService roleService;
  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;
  private final HttpServletRequest request;

  public String authenticateUser(LoginRequestDto loginRequestDto) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequestDto.getEmail(), loginRequestDto.getPassword()));
    } catch (BadCredentialsException exc) {
      throw new InvalidCredentialsException("Incorrect email or password");
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDto.getEmail());

    return jwtUtils.generateToken(userDetails);
  }

  public User registerUser(User user) {
    final String roleName = user.getRole().getName();

    Role role =
        roleService
            .findByName(user.getRole().getName())
            .orElseThrow(() -> new RoleNotFoundException("Role not found with name " + roleName));
    user.setRole(role);

    Optional<User> createdUser = createUser(user);

    user = createdUser.get();
    user.setAppointments(new ArrayList<>());

    return user;
  }

  public Optional<User> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<User> findUserById(int id) {
    return Optional.of(
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id " + id)));
  }

  private Optional<User> createUser(User user) {
    Optional<User> foundUser = findUserByEmail(user.getEmail());

    if (foundUser.isPresent()) {
      throw new UserConflictException("User email is already in use");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return Optional.of(userRepository.saveAndFlush(user));
  }

  public Optional<User> update(
      UserResponseDtoWithoutAppointments userDto, HttpServletRequest request) {
    User user = checkUserPermission(userDto.getId(), request, "update");

    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setEmail(userDto.getEmail());
    user.setFirstName(userDto.getFirstName());
    user.setLastName(userDto.getLastName());

    checkIfEmailIsTaken(request, user);

    User updatedUser = userRepository.saveAndFlush(user);

    return Optional.of(updatedUser);
  }

  private void checkIfEmailIsTaken(HttpServletRequest request, User user) {
    String loggedInUserEmail = request.getUserPrincipal().getName();

    if(userRepository.findByEmail(user.getEmail()).isPresent() && !loggedInUserEmail.equals(user.getEmail())) {
      throw new UserConflictException("Email is already in use");
    }
  }

  public void delete(int userId) {
    Optional<User> user = findUserById(userId);

    if (user.isPresent()) {
      checkUserPermission(user.get().getId(), request, "delete");
      userRepository.delete(user.get());
    }
  }

  public List<User> findAll(int pageNo, int pageSize, String sortBy) {
    Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

    Page<User> pagedResult = userRepository.findAll(paging);

    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return Collections.emptyList();
    }
  }

  private User checkUserPermission(int userId, HttpServletRequest request, String action) {
    String loggedInUserEmail = request.getUserPrincipal().getName();
    UserDetails userDetails = userDetailsService.loadUserByUsername(loggedInUserEmail);

    Optional<User> user = findUserById(userId);

    if (!userDetails.getUsername().equals(user.get().getEmail())
        && !userDetails.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().contains("ROLE_ADMIN"))) {
      throw new UnauthorizedActionException(
          "You don't have permission to " + action + " account that isn't yours.");
    }

    return user.get();
  }
}
