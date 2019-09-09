package com.campsitereservations.db;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationDetails {
    private String reservationId;
    private Customer customer;
    private ReservationsDates reservationsDates;
}
