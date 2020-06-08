package com.prime.rushhour.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class UserRequestDto {

  @Size(min = 2, max = 15, message = "Invalid firstName. Must be between 2 and 15 characters long")
  private String firstName;

  @Size(min = 2, max = 15, message = "Invalid lastName. Must be between 2 and 15 characters long")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Not a valid email")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

  @NotNull(message = "Role is required")
  private RoleRequestDto role;
}
