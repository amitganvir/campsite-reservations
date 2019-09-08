package com.campsitereservations.service;

import com.campsitereservations.contracts.AvailableReservationDatesResponse;
import com.campsitereservations.contracts.DeleteReservationResponse;
import com.campsitereservations.contracts.ReservationAddUpdateResponse;
import com.campsitereservations.db.CampsiteInMemoryDatabase;
import com.campsitereservations.db.ReservationDetails;
import com.campsitereservations.db.ReservationsDates;
import com.campsitereservations.exceptions.InvalidInputException;
import com.campsitereservations.mapper.ReservationsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.campsitereservations.util.DateUtil.isEqualOrAfter;
import static com.campsitereservations.util.DateUtil.isEqualOrBefore;
import static com.campsitereservations.validation.FieldsValidator.validInputFields;
import static com.campsitereservations.validation.FieldsValidator.validString;

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
                    .getCampsiteAvailability(reservationsDates.getCheckinDate(), reservationsDates.getCheckoutDate());
            return reservationsMapper.mapToAvailableReservationDatesResponse(reservationsDates.getCheckinDate().toString(),
                    reservationsDates.getCheckoutDate().toString(), campsiteAvailability);

        } catch (Exception exception) {
            return reservationsMapper.mapToAvailableReservationDatesFailedResponse(startDate, endDate, exception);
        }
    }

    private ReservationsDates validateAndAdjustDates(String startDate, String endDate) throws Exception{


        LocalDate startLocalDate = validString(startDate) ? LocalDate.parse(startDate) : LocalDate.now();
        LocalDate endLocalDate = validString(endDate) ? LocalDate.parse(endDate) : LocalDate.now().plusDays(32);

        String errorMessage = null;

        if (startLocalDate.isBefore(LocalDate.now())) {
            errorMessage = "Checkin date should be a future date ";
        } else if (startLocalDate.isAfter(endLocalDate)) {
            errorMessage = "Checkin date should be before checkout date ";
        } else if (endLocalDate.isBefore(LocalDate.now())) {
            errorMessage = "Checkout date should be a future date ";
        } else if (endLocalDate.isBefore(startLocalDate)) {
            errorMessage = "Checkout date should be after checkin date ";
        }

        if (!StringUtils.isEmpty(errorMessage)) {
            throw new InvalidInputException(errorMessage);
        }

        return ReservationsDates.builder().checkinDate(startLocalDate).checkoutDate(endLocalDate).build();
    }

    public ReservationAddUpdateResponse addReservation(String firstName, String lastName, String email,
                                                       String startDate, String endDate) {

        try {

            validInputFields(firstName, lastName, email, startDate, endDate);
            ReservationDetails reservationDetails = reservationsMapper
                    .mapToReservationDetails(firstName, lastName, email, startDate, endDate, getReservationUniqueId());
            campsiteInMemoryDatabase.addReservation(reservationDetails);
            return reservationsMapper.mapToAddReservationResponse(reservationDetails);
        } catch (Exception exception) {
            return reservationsMapper.mapToAddReservationExceptionResponse(exception);
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

        ReservationDetails newReservation = null;
        try {
            validInputFields(firstName, lastName, email, startDate, endDate);

            ReservationDetails oldReservation = campsiteInMemoryDatabase.findReservation(reservationId);
            newReservation = reservationsMapper.mapToReservationDetails(firstName, lastName, email,
                    startDate, endDate, oldReservation.getReservationId());

            validateReservationUpdate(oldReservation.getReservationsDates(), newReservation.getReservationsDates());
            campsiteInMemoryDatabase.updateReservation(oldReservation, newReservation);

            return reservationsMapper.mapToUpdateReservationResponse(newReservation);

        } catch (Exception exception) {
            return reservationsMapper.mapToUpdateExceptionResponse(newReservation, exception);
        }
    }

    private void validateReservationUpdate(ReservationsDates oldReservationDates,
                                           ReservationsDates newReservationDates) {

        List<LocalDate> campsiteAvailability = campsiteInMemoryDatabase
                .getCampsiteAvailability(newReservationDates.getCheckinDate(), newReservationDates.getCheckoutDate());

        Set<LocalDate> availableDates = new HashSet<>(campsiteAvailability);

        LocalDate newCheckInDate = newReservationDates.getCheckinDate();
        LocalDate newCheckoutDate = newReservationDates.getCheckoutDate();

        while (newCheckInDate.isBefore(newCheckoutDate.plusDays(1))) {

            if (!availableDates.contains(newCheckInDate) &&
                    (isEqualOrAfter(newCheckInDate, oldReservationDates.getCheckinDate()) &&
                            isEqualOrBefore(newCheckInDate, oldReservationDates.getCheckoutDate()))) {

                throw new RuntimeException("Campsite is not available for new checkin and checkout dates");
            }

            newCheckInDate = newCheckInDate.plusDays(1);
        }
    }


}
