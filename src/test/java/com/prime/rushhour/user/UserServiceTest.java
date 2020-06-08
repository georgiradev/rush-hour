package com.prime.rushhour.user;

import com.prime.rushhour.dto.LoginRequestDto;
import com.prime.rushhour.dto.UserResponseDtoWithoutAppointments;
import com.prime.rushhour.entity.User;
import com.prime.rushhour.exception.InvalidCredentialsException;
import com.prime.rushhour.exception.UnauthorizedActionException;
import com.prime.rushhour.exception.UserConflictException;
import com.prime.rushhour.exception.UserNotFoundException;
import com.prime.rushhour.provider.UserDetailsProvider;
import com.prime.rushhour.provider.UserProvider;
import com.prime.rushhour.repository.UserRepository;
import com.prime.rushhour.security.jwt.JwtUtils;
import com.prime.rushhour.service.RoleService;
import com.prime.rushhour.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks private UserService userService;

  @Mock private UserRepository userRepository;

  @Mock private RoleService roleService;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private UserDetailsService userDetailsService;

  @Mock private HttpServletRequest request;

  @Mock private JwtUtils jwtUtils;

  @Mock private AuthenticationManager authenticationManager;

  @Test
  void testUserCreation() {
    User user = UserProvider.getInstance();

    when(this.roleService.findByName(any(String.class))).thenReturn(Optional.of(user.getRole()));
    when(this.userRepository.saveAndFlush(any(User.class))).thenReturn(user);

    User registerUser = userService.registerUser(user);

    assertEquals(user, registerUser);
  }

  @Test
  void testUserCreationDuplicateEmailShouldFail() {
    User user = UserProvider.getInstance();

    when(this.roleService.findByName(any(String.class))).thenReturn(Optional.of(user.getRole()));
    when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

    assertThrows(UserConflictException.class, () -> userService.registerUser(user));
  }

  @Test
  void testFindUserById() {
    User user = UserProvider.getInstance();

    when(this.userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));

    Optional<User> foundUser = userService.findUserById(user.getId());

    assertEquals(Optional.of(user), foundUser);
  }

  @Test
  void testFindUserByNonExistingIdShouldFail() {
    when(this.userRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.findUserById(1));
  }

  @Test
  void testFindUserByEmail() {
    User user = UserProvider.getInstance();

    when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

    Optional<User> foundUser = userService.findUserByEmail(user.getEmail());

    assertEquals(Optional.of(user), foundUser);
  }

  @Test
  void testFindUserByNonExistingEmailShouldFail() {
    User user = UserProvider.getInstance();

    when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

    assertEquals(Optional.empty(), userService.findUserByEmail(user.getEmail()));
  }

  @Test
  void testAuthentication() {
    User user = UserProvider.getInstance();
    UserDetails userDetails = new UserDetailsProvider();
    String token = "token";

    LoginRequestDto loginRequestDto = new LoginRequestDto();
    loginRequestDto.setEmail(user.getEmail());
    loginRequestDto.setPassword(user.getPassword());

    when(this.userDetailsService.loadUserByUsername(any(String.class))).thenReturn(userDetails);
    when(this.jwtUtils.generateToken(userDetails)).thenReturn(token);

    String jwtToken = userService.authenticateUser(loginRequestDto);

    assertEquals(token, jwtToken);
  }

  @Test
  void testAuthenticationEnteredBadCredentialsShouldFail() {
    User user = UserProvider.getInstance();

    LoginRequestDto loginRequestDto = new LoginRequestDto();
    loginRequestDto.setEmail(user.getEmail());
    loginRequestDto.setPassword(user.getPassword());

    when(this.authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException(""));

    assertThrows(
        InvalidCredentialsException.class, () -> userService.authenticateUser(loginRequestDto));
  }

  @Test
  void testFindAllUsers() {
    List<User> users = UserProvider.getUsersInstance();
    Page<User> paging = new PageImpl<>(users);

    when(this.userRepository.findAll(any(Pageable.class))).thenReturn(paging);

    List<User> foundUsers = userService.findAll(0, 10, "id");

    assertEquals(users, foundUsers);
  }

  @Test
  void testFindAllUsersButNoContentFound() {
    List<User> users = new ArrayList<>();
    Page<User> paging = new PageImpl<>(users);

    when(this.userRepository.findAll(any(Pageable.class))).thenReturn(paging);

    List<User> foundUsers = userService.findAll(0, 10, "id");

    assertEquals(users, foundUsers);
  }

  @Test
  void testDeleteUser() {
    User user = UserProvider.getInstance();
    Principal userPrincipal = user::getEmail;
    UserDetails userDetails = new UserDetailsProvider();

    when(this.userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(this.request.getUserPrincipal()).thenReturn(userPrincipal);
    when(this.userDetailsService.loadUserByUsername(any(String.class))).thenReturn(userDetails);

    userService.delete(user.getId());

    when(this.userRepository.findById(user.getId())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.delete(user.getId()));
  }

  @Test
  void testUpdateUser() {
    User user = UserProvider.getInstance();

    UserResponseDtoWithoutAppointments userDto = new UserResponseDtoWithoutAppointments();
    userDto.setId(user.getId());
    userDto.setEmail(user.getEmail());
    userDto.setFirstName(user.getFirstName());
    userDto.setLastName(user.getLastName());
    userDto.setPassword(user.getPassword());

    Principal userPrincipal = user::getEmail;
    UserDetails userDetails = new UserDetailsProvider();

    when(this.userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(this.request.getUserPrincipal()).thenReturn(userPrincipal);
    when(this.userDetailsService.loadUserByUsername(any(String.class))).thenReturn(userDetails);
    when(this.userRepository.saveAndFlush(any(User.class))).thenReturn(user);
    when(this.passwordEncoder.encode(any())).thenReturn(user.getPassword());

    Optional<User> updatedUser = userService.update(userDto, request);

    assertEquals(Optional.of(user), updatedUser);
  }

  @Test
  void testDeleteUserByNonExistingIdShouldFail() {
    User user = UserProvider.getInstance();

    when(this.userRepository.findById(user.getId())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.delete(user.getId()));
  }

  @Test
  void testDeleteUserByUnauthorizedAccountShouldFail() {
    User user = UserProvider.getInstance();
    user.setEmail("hacker@abv.bg");

    Principal userPrincipal = user::getEmail;
    UserDetails userDetails = new UserDetailsProvider();

    when(this.userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(this.request.getUserPrincipal()).thenReturn(userPrincipal);
    when(this.userDetailsService.loadUserByUsername(any(String.class))).thenReturn(userDetails);

    assertThrows(UnauthorizedActionException.class, () -> userService.delete(user.getId()));
  }
}
