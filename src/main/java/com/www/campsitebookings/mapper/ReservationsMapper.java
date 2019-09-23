package com.www.campsitebookings.mapper;

import com.www.campsitebookings.contracts.CampsiteAvailabilityModel;
import com.www.campsitebookings.contracts.CancelReservationModel;
import com.www.campsitebookings.contracts.ErrorModel;
import com.www.campsitebookings.contracts.ReservationConfirmationModel;
import com.www.campsitebookings.db.Reservation;
import com.www.campsitebookings.util.SystemEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper class that maps input requests to Reservation request.
 * Maps Database results to reservation status models.
 *
 */
@Component
public class ReservationsMapper {

    public CampsiteAvailabilityModel mapToCampsiteAvailabilityModel(List<LocalDate> availableDates) {

        return CampsiteAvailabilityModel.builder()
                .availableDates(availableDates.stream().map(LocalDate::toString)
                        .collect(Collectors.toList()))
                .build();
    }

    public ReservationConfirmationModel mapToReservationConfirmationModel(Reservation reservation,
                                                                          LocalDate checkinDate,
                                                                          LocalDate checkoutDate) {
        return ReservationConfirmationModel
                .builder()
                .name(reservation.getName())
                .email(reservation.getEmail())
                .checkinDate(checkinDate.toString())
                .checkoutDate(checkoutDate.toString())
                .reservationId(reservation.getReservationId())
                .build();
    }

    public ReservationConfirmationModel mapToFailedReservationConfirmationModel(SystemEvent systemEvent) {
        return ReservationConfirmationModel
                .builder()
                .errorModel(
                        ErrorModel.builder()
                                .errorCode(systemEvent.getErrorCode())
                                .errorMessage(systemEvent.getDescription()).build())
                .build();
    }

    public CampsiteAvailabilityModel mapToNoAvailabilityModel(SystemEvent systemEvent) {
        return CampsiteAvailabilityModel.builder()
                .errorModel(ErrorModel
                        .builder()
                        .errorCode(systemEvent.getErrorCode())
                        .errorMessage(systemEvent.getDescription())
                        .build())
                .build();
    }

    public Reservation mapToReservation(String name, String email) {

        String reservationId = UUID.randomUUID().toString();


        return Reservation.builder()
                .name(name)
                .email(email)
                .reservationId(reservationId)
                .build();
    }

    public CancelReservationModel mapToSuccessfulCancelReservationModel() {

        return CancelReservationModel.builder().message("Reservation cancelled successfully").build();
    }

    public CancelReservationModel mapToFailedCancelReservationModel(SystemEvent systemEvent) {

        return CancelReservationModel.builder()
                .errorModel(ErrorModel.builder()
                        .errorMessage(systemEvent.getDescription()).errorCode(systemEvent.getErrorCode()).build())
                .build();
    }
}
