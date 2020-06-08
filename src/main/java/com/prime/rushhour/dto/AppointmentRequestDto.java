package com.prime.rushhour.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AppointmentRequestDto {

  @NotNull(message = "Start Date is required")
  @DateTimeFormat(pattern = "YYYY-mm-ddTHH:MM:SS")
  private LocalDateTime startDate;

  @NotNull(message = "Activities is required")
  private List<ActivityIdDto> activities;
}
