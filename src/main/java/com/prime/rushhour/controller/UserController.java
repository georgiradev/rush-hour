package com.prime.rushhour.controller;

import com.prime.rushhour.dto.*;
import com.prime.rushhour.entity.User;
import com.prime.rushhour.mapper.Mapper;
import com.prime.rushhour.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1")
public class UserController {

  private final UserService userService;
  private final Mapper mapper;

  @PostMapping("/auth")
  public ResponseEntity<LoginResponseDto> authenticateUser(
      @Valid @RequestBody LoginRequestDto loginRequestDto) {
    String token = userService.authenticateUser(loginRequestDto);

    return ResponseEntity.ok(new LoginResponseDto(token));
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponseDto> registerUser(
      @Valid @RequestBody UserRequestDto userRequestDto) {

    User user = mapper.userDtoToUser(userRequestDto);
    UserResponseDto userResponseDto = mapper.userToUserDto(userService.registerUser(user));

    return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
  }

  @GetMapping("/user")
  public ResponseEntity<UserResponseDto> getUserById(@RequestParam("id") @Min(1) int id) {
    Optional<User> foundUser = userService.findUserById(id);

    if (foundUser.isPresent()) {
      UserResponseDto userDto = mapper.userToUserDto(foundUser.get());

      return ResponseEntity.ok(userDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @GetMapping("/users")
  public ResponseEntity<List<UserResponseDto>> getAllUsers(
      @RequestParam(defaultValue = "0") int pageNo,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "id") String sortBy) {

    List<User> users = userService.findAll(pageNo, pageSize, sortBy);

    List<UserResponseDto> userDtos = mapper.usersToUserDtos(users);

    return ResponseEntity.ok(userDtos);
  }

  @PutMapping("/user")
  public ResponseEntity<UserResponseDtoWithoutAppointments> updateUserById(
      @Valid @RequestBody UserResponseDtoWithoutAppointments userDto, HttpServletRequest request) {

    Optional<User> updatedUser = userService.update(userDto, request);

    if(updatedUser.isPresent()) {
      userDto = mapper.userToUserDtoWithoutAppointments(updatedUser.get());

      return ResponseEntity.ok(userDto);
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @DeleteMapping("/user")
  public ResponseEntity<String> deleteUserById(
          @RequestParam("id") @Min(1) int id) {
    userService.delete(id);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
