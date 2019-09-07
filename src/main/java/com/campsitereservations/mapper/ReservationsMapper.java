package com.campsitereservations.mapper;

import com.campsitereservations.contracts.*;
import com.campsitereservations.db.CustomerData;
import com.campsitereservations.db.ReservationDetails;
import com.campsitereservations.db.ReservationsDates;
import javafx.util.converter.LocalDateStringConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ReservationsMapper {

    public ReservationAddUpdateResponse mapToAddReservationResponse(ReservationDetails reservationDetails) {

        return ReservationAddUpdateResponse
                .builder()
                .message("Campsite booked successfully")
                .reservationModel(mapToReservationModel(reservationDetails))
                .build();
    }

    public ReservationDetails mapToReservationDetails(String firstName, String lastName,
                                                      String email, String startDate, String endDate,
                                                      String reservationId) {

        return ReservationDetails
                .builder()
                .customerData(CustomerData
                        .builder()
                        .firstName(firstName).lastName(lastName).email(email)
                        .build())
                .reservationsDates(
                        ReservationsDates
                                .builder()
                                .startDate(LocalDate.parse(startDate))
                                .endDate(LocalDate.parse(endDate))
                                .build())
                .reservationId(reservationId)
                .build();
    }

    public ReservationAddUpdateResponse mapToAddReservationExceptionResponse(ReservationDetails reservationDetails,
                                                                             Exception exception) {

        return ReservationAddUpdateResponse
                .builder()
                .message("Campsite booking failed, Please check error details")
                .errorDetails(ErrorDetails.builder().errorMessage(exception.getMessage()).build())
                .build();
    }

    public ReservationAddUpdateResponse mapToUpdateReservationResponse(ReservationDetails reservationDetails) {

        return ReservationAddUpdateResponse
                .builder()
                .message("Campsite booking updated successfully")
                .reservationModel(mapToReservationModel(reservationDetails))
                .build();
    }

    public ReservationAddUpdateResponse mapToUpdateExceptionResponse(ReservationDetails reservationDetails,
                                                                     Exception exception) {

        return ReservationAddUpdateResponse
                .builder()
                .message("Campsite booking update failed")
                .reservationModel(mapToReservationModel(reservationDetails))
                .errorDetails(ErrorDetails.builder().errorMessage(exception.getMessage()).build())
                .build();
    }

    private ReservationModel mapToReservationModel(ReservationDetails reservationDetails) {

        return reservationDetails != null ? ReservationModel
                .builder()
                .reservationId(reservationDetails.getReservationId())
                .checkInDate(reservationDetails.getReservationsDates().getStartDate().toString())
                .checkoutDate(reservationDetails.getReservationsDates().getEndDate().toString())
                .firstName(reservationDetails.getCustomerData().getFirstName())
                .lastName(reservationDetails.getCustomerData().getLastName())
                .email(reservationDetails.getCustomerData().getEmail())
                .build() : null;
    }

    public ReservationAddUpdateResponse mapToFailedResponseForAddEdit(ReservationDetails reservationDetails,
                                                                      Exception exception) {
        return ReservationAddUpdateResponse
                .builder()
                .message("Booking unsuccessful")
                .reservationModel(mapToReservationModel(reservationDetails))
                .errorDetails(ErrorDetails
                        .builder()
                        .errorMessage(exception.getMessage())
                        .build())
                .build();
    }

    public DeleteReservationResponse mapToDeleteReservationResponse(String reservationId) {
        return DeleteReservationResponse
                .builder()
                .message("Reservation cancelled successfully for reservation id : " + reservationId)
                .build();
    }

    public DeleteReservationResponse mapToDeleteReservationFailedResponse(String reservationId, Exception exception) {
        return DeleteReservationResponse
                .builder()
                .message("Reservation cancellation failed for reservation id : " + reservationId)
                .errorDetails(ErrorDetails.builder().errorMessage(exception.getMessage()).build())
                .build();
    }

    public AvailableReservationDatesResponse mapToAvailableReservationDatesResponse(String startDate,
                                                                                    String endDate,
                                                                                    List<LocalDate> dates) {

        List<String> availableDates = dates.stream().map(LocalDate::toString).collect(Collectors.toList());
        return AvailableReservationDatesResponse
                .builder()
                .message("Available dates for booking between " + startDate + " - " + endDate)
                .availableDates(AvailableReservationDatesModel.builder().dates(availableDates).build())
                .build();
    }

    public AvailableReservationDatesResponse mapToAvailableReservationDatesFailedResponse(
            String startDate, String endDate, Exception exception) {

        return AvailableReservationDatesResponse
                .builder()
                .message("Failed to get dates for booking between " + startDate + " - " + endDate)
                .errorDetails(ErrorDetails.builder().errorMessage(exception.getMessage()).build())
                .build();
    }
}
