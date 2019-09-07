package com.campsitereservations.db;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReservationsDates {
    private LocalDate startDate;
    private LocalDate endDate;
}
