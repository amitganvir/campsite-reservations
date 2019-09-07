package com.campsitereservations.service;

import com.campsitereservations.contracts.AvailableReservationDatesResponse;
import com.campsitereservations.contracts.DeleteReservationResponse;
import com.campsitereservations.contracts.ReservationAddUpdateResponse;
import com.campsitereservations.db.CampsiteInMemoryDatabase;
import com.campsitereservations.db.ReservationDetails;
import com.campsitereservations.db.ReservationsDates;
import com.campsitereservations.mapper.ReservationsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationOperationsService {

    private CampsiteInMemoryDatabase campsiteInMemoryDatabase;
    private ReservationsMapper reservationsMapper;

    @Autowired
    public ReservationOperationsService(CampsiteInMemoryDatabase campsiteInMemoryDatabase,
                                        ReservationsMapper reservationsMapper) {
        this.campsiteInMemoryDatabase = campsiteInMemoryDatabase;
        this.reservationsMapper = reservationsMapper;
    }

    public AvailableReservationDatesResponse getAvailableDates(String startDate, String endDate) {
        try {
            ReservationsDates reservationsDates = validateAndAdjustDates(startDate, endDate);
            List<LocalDate> campsiteAvailability = campsiteInMemoryDatabase
                    .getCampsiteAvailability(reservationsDates.getStartDate(), reservationsDates.getEndDate());
            return reservationsMapper.mapToAvailableReservationDatesResponse(startDate, endDate, campsiteAvailability);

        } catch (Exception exception) {
            return reservationsMapper.mapToAvailableReservationDatesFailedResponse(startDate, endDate, exception);
        }
    }

    private ReservationsDates validateAndAdjustDates(String startDate, String endDate) {

        LocalDate startLocalDate = validString(startDate) ? LocalDate.parse(startDate) : LocalDate.now();
        LocalDate endLocalDate = validString(endDate) ? LocalDate.parse(endDate) : LocalDate.now().plusDays(32);

        return ReservationsDates.builder().startDate(startLocalDate).endDate(endLocalDate).build();
    }

    private boolean validString(String date) {
        return !StringUtils.isEmpty(date);
    }

    public ReservationAddUpdateResponse addReservation(String firstName, String lastName, String email,
                                                       String startDate, String endDate) {

        if (!(validString(startDate) || validString(endDate) ||
                validString(firstName) || validString(lastName) || validString(email))) {

            StringBuilder stringBuilder = new StringBuilder()
                    .append("firstName=").append(firstName).append("\n")
                    .append("lastName=").append(lastName).append("\n")
                    .append("email=").append(email).append("\n")
                    .append("startDate=").append(startDate).append("\n")
                    .append("endDate=").append(endDate).append("\n");

            return reservationsMapper.mapToAddReservationExceptionResponse(null,
                    new RuntimeException("Invalid input : Empty field provided " + stringBuilder.toString()));
        }

        ReservationDetails reservationDetails = reservationsMapper
                .mapToReservationDetails(firstName, lastName, email, startDate, endDate, getReservationUniqueId());

        try {
            boolean success = campsiteInMemoryDatabase.addReservation(reservationDetails);
            return reservationsMapper.mapToAddReservationResponse(reservationDetails);
        } catch (Exception exception) {
            return reservationsMapper.mapToAddReservationExceptionResponse(reservationDetails, exception);
        }
    }

    private String getReservationUniqueId() {
        return UUID.randomUUID().toString();
    }

    public DeleteReservationResponse cancelReservation(String reservationId) {

        try {
            campsiteInMemoryDatabase.deleteReservation(reservationId);
            return reservationsMapper.mapToDeleteReservationResponse(reservationId);
        } catch (Exception exception) {
            return reservationsMapper.mapToDeleteReservationFailedResponse(reservationId, exception);
        }
    }

    public ReservationAddUpdateResponse updateReservation(String reservationId,
                                                          String firstName, String lastName, String email,
                                                          String startDate, String endDate) {

        if (!validInputFields(firstName, lastName, email, startDate, endDate)) {

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("firstName=").append(firstName).append("\n")
                    .append("lastName=").append(lastName).append("\n")
                    .append("email=").append(email).append("\n")
                    .append("startDate=").append(startDate).append("\n")
                    .append("endDate=").append(endDate).append("\n");

            return reservationsMapper.mapToAddReservationExceptionResponse(null,
                    new RuntimeException("Invalid input : Empty field provided " + strBuilder.toString()));
        }

        ReservationDetails newReservation = null;
        try {
            ReservationDetails oldReservation = campsiteInMemoryDatabase.findReservation(reservationId);
            newReservation = reservationsMapper.mapToReservationDetails(firstName, lastName, email,
                    startDate, endDate, oldReservation.getReservationId());

            campsiteInMemoryDatabase.updateReservation(oldReservation, newReservation);

            return reservationsMapper.mapToUpdateReservationResponse(newReservation);

        } catch (Exception exception) {
            return reservationsMapper.mapToUpdateExceptionResponse(newReservation, exception);
        }
    }

    private boolean validInputFields(String firstName, String lastName, String email,
                                     String startDate, String endDate) {

        return validString(startDate) && validString(endDate) && validString(firstName) && validString(lastName) &&
                validString(email);
    }
}
