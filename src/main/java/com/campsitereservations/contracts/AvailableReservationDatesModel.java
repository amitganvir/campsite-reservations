package com.campsitereservations.contracts;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AvailableReservationDatesModel {
    private List<String> dates;
}
