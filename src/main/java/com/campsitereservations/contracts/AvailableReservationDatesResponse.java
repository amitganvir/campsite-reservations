package com.campsitereservations.contracts;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvailableReservationDatesResponse {
    private String message;
    private AvailableReservationDatesModel availableDates;
    private ErrorDetails errorDetails;
}
