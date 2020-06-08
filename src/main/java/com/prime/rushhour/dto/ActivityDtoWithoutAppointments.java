package com.prime.rushhour.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class ActivityDtoWithoutAppointments {

    @Min(value = 1, message = "Id must be greater than zero")
    private int id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Duration is required")
    private String duration;

    @Min(value = 1, message = "Price must be positive value")
    @NotNull(message = "Price is required")
    private BigDecimal price;
}
