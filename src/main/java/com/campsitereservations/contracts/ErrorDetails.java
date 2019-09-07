package com.campsitereservations.contracts;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorDetails {
    private String errorMessage;
}
