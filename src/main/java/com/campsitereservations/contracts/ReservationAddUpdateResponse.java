package com.campsitereservations.contracts;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationAddUpdateResponse {
    private String message;
    private ReservationModel reservationModel;
    private ErrorDetails errorDetails;
}

