package com.campsitereservations.contracts;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteReservationResponse {
    private String message;
    private ErrorDetails errorDetails;
}
