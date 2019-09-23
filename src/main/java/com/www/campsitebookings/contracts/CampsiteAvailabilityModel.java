package com.www.campsitebookings.contracts;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CampsiteAvailabilityModel {
    private List<String> availableDates;
    private ErrorModel errorModel;
}
