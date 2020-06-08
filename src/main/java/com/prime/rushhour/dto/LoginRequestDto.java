package com.prime.rushhour.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequestDto {

  @NotBlank(message = "Email is required")
  @Email(message = "Not a valid email")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;
}
