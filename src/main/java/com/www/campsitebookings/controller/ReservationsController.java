package com.www.campsitebookings.controller;


import com.www.campsitebookings.contracts.CampsiteAvailabilityModel;
import com.www.campsitebookings.contracts.CancelReservationModel;
import com.www.campsitebookings.contracts.ErrorModel;
import com.www.campsitebookings.contracts.ReservationConfirmationModel;
import com.www.campsitebookings.exception.InvalidInputException;
import com.www.campsitebookings.service.ReservationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.www.campsitebookings.util.SystemEvent.ADD_RESERVATION_EXCEPTION;

/**
 * Rest Controller with methods to add, update, cancel and get campsites
 */
@RestController
@RequestMapping("/v1/api/")
public class ReservationsController {

    private ReservationsService reservationsService;
    private Logger logger = LoggerFactory.getLogger(ReservationsController.class);

    @Autowired
    public ReservationsController(ReservationsService reservationsService) {
        this.reservationsService = reservationsService;
    }

    @GetMapping("available-dates")
    public ResponseEntity<CampsiteAvailabilityModel> getAvailableDates(@RequestParam("checkinDate") String checkinDate,
                                                                       @RequestParam("checkoutDate") String checkoutDate) {

        CampsiteAvailabilityModel campsiteAvailability = null;
        try {
            campsiteAvailability = reservationsService.getAvailableDates(checkinDate, checkoutDate);
        } catch (InvalidInputException invalidInputException) {
            return new ResponseEntity<>(campsiteAvailability, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(campsiteAvailability, HttpStatus.OK);
    }

    @PostMapping("add-reservation")
    public ResponseEntity<ReservationConfirmationModel> addReservation(@RequestParam("name") String name,
                                                                       @RequestParam("email") String email,
                                                                       @RequestParam("checkinDate") String checkinDate,
                                                                       @RequestParam("checkoutDate") String checkoutDate) {

        ReservationConfirmationModel reservationConfirmationModel = null;
        try {
            reservationConfirmationModel = reservationsService.addReservation(name, email, checkinDate, checkoutDate);
        } catch (InvalidInputException invalidInputException) {
            return new ResponseEntity<>(reservationConfirmationModel, HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            logger.error(ADD_RESERVATION_EXCEPTION.getDescription(), exception);
            return new ResponseEntity<>(ReservationConfirmationModel.builder()
                    .errorModel(ErrorModel.builder()
                            .errorCode(ADD_RESERVATION_EXCEPTION.getErrorCode())
                            .errorMessage(ADD_RESERVATION_EXCEPTION.getDescription())
                            .build()).build(), HttpStatus.OK);
        }

        return new ResponseEntity<>(reservationConfirmationModel, HttpStatus.OK);
    }

    @DeleteMapping("cancel-reservation")
    public ResponseEntity<CancelReservationModel> cancelReservation(@RequestParam("reservationId") String reservationId) {

        CancelReservationModel deleteReservationResponse = null;

        try {
            deleteReservationResponse = reservationsService
                    .cancelReservation(reservationId);

        } catch (InvalidInputException invalidInputException) {
            return new ResponseEntity<>(deleteReservationResponse, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(deleteReservationResponse, HttpStatus.OK);
    }

    @PutMapping("update-reservation")
    public ResponseEntity<ReservationConfirmationModel> updateReservation(String reservationId,
                                                                          String name, String email,
                                                                          String checkinDate,
                                                                          String checkoutDate) {

        ReservationConfirmationModel reservationConfirmationModel = null;

        try {
            reservationConfirmationModel = reservationsService.updateReservation(reservationId, name, email,
                    checkinDate, checkoutDate);
        } catch (InvalidInputException invalidInputException) {
            return new ResponseEntity<>(reservationConfirmationModel, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reservationConfirmationModel, HttpStatus.OK);
    }

}