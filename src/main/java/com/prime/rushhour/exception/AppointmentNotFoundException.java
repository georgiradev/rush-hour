package com.prime.rushhour.exception;

public class AppointmentNotFoundException extends RuntimeException {
  public AppointmentNotFoundException(String message) {
    super(message);
  }
}
