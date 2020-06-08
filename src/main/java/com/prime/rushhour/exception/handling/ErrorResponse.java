package com.prime.rushhour.exception.handling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResponse {

  private LocalDateTime timeStamp;
  private int status;
  private String error;
  private String massage;
  private String path;
}
