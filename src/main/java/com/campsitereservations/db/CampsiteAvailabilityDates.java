package com.campsitereservations.db;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CampsiteAvailabilityDates {
    private LocalDate availableDate;
}
