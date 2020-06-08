package com.prime.rushhour.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class ActivityIdDto {

    @Min(value = 1, message = "Id must be greater than zero")
    private int id;
}
