package com.campsitereservations.contracts;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationModel {
    private String reservationId;
    private String firstName;
    private String lastName;
    private String email;
    private String checkInDate;
    private String checkoutDate;
}
