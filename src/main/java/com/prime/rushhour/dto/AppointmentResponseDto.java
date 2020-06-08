package com.prime.rushhour.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AppointmentResponseDto {

    @Min(value = 1, message = "Id must be greater than zero")
    private int id;

    @NotNull(message = "Start date is required")
    @DateTimeFormat
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @DateTimeFormat
    private LocalDateTime endDate;

    @Min(value = 1, message = "Id must be greater than zero")
    private int userId;

    private List<ActivityDtoWithoutAppointments> activities;
}
