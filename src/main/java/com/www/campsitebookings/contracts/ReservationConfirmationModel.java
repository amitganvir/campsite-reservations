package com.www.campsitebookings.contracts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationConfirmationModel {
    private String reservationId;
    private String name;
    private String email;
    private String checkoutDate;
    private String checkinDate;
    private ErrorModel errorModel;
}
