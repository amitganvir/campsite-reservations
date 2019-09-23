package com.www.campsitebookings.contracts;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CancelReservationModel {
    private ErrorModel errorModel;
    private String message;
}
