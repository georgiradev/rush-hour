package com.prime.rushhour.exception.handling;

import com.prime.rushhour.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> userConflict(
      UserConflictException exc, HttpServletRequest servletRequest) {

    ErrorResponse error =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            exc.getMessage(),
            servletRequest.getRequestURI());

    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());

    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());

    body.put("errors", errors);

    return new ResponseEntity<>(body, headers, status);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public void constraintViolationException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value());
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public void credentialsViolationException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value());
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public void jwtExpiredException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.UNAUTHORIZED.value());
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public void paginationPropertyNotValidException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value());
  }

  @ExceptionHandler(ActivityNotFoundException.class)
  public void activityNotFoundException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.NOT_FOUND.value());
  }

  @ExceptionHandler(ActivityConflictException.class)
  public void activityConflictException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.CONFLICT.value());
  }

  @ExceptionHandler(DateTimeParseException.class)
  public void durationParseException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value());
  }

  @ExceptionHandler(AppointmentNotFoundException.class)
  public void appointmentNotFoundException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.NOT_FOUND.value());
  }

  @ExceptionHandler(UnauthorizedActionException.class)
  public void accountMismatchException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.UNAUTHORIZED.value());
  }

  @ExceptionHandler(OverlappingAppointmentsException.class)
  public void overlappingAppointmentsException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public void userNotFoundException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.NOT_FOUND.value());
  }

  @ExceptionHandler(RoleNotFoundException.class)
  public void roleException(HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.NOT_FOUND.value());
  }
}
