package com.prime.rushhour.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ActivityResponseDto {

  @Min(value = 1, message = "Id must be positive value")
  private int id;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Duration is required")
  private String duration;

  @NotNull(message = "Price is required")
  private BigDecimal price;

  private List<AppointmentDtoWithoutActivities> appointments = new ArrayList<>();
}
