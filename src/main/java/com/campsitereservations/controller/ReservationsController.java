package com.campsitereservations.controller;

import com.campsitereservations.contracts.AvailableReservationDatesResponse;
import com.campsitereservations.contracts.DeleteReservationResponse;
import com.campsitereservations.contracts.ReservationAddUpdateResponse;
import com.campsitereservations.service.ReservationOperationsService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/v1/api/")
public class ReservationsController {

    private ReservationOperationsService reservationOperationsService;

    @Autowired
    public ReservationsController(ReservationOperationsService reservationOperationsService) {
        this.reservationOperationsService = reservationOperationsService;
    }

    @GetMapping("available-dates")
    public ResponseEntity<AvailableReservationDatesResponse> getAvailableDates(String startDate, String endDate) {

        AvailableReservationDatesResponse availableReservationDatesResponse = reservationOperationsService
                .getAvailableDates(startDate, endDate);

        return new ResponseEntity<>(availableReservationDatesResponse, HttpStatus.OK);
    }

    @PostMapping("add-reservation")
    public ResponseEntity<ReservationAddUpdateResponse> addReservation(String firstName, String lastName,
                                         String email, String startDate, String endDate) {

        ReservationAddUpdateResponse reservationAddUpdateResponse = reservationOperationsService
                .addReservation(firstName, lastName, email, startDate, endDate);

        return new ResponseEntity<>(reservationAddUpdateResponse, HttpStatus.OK);
    }

    @DeleteMapping("cancel-reservation")
    public ResponseEntity<DeleteReservationResponse> cancelReservation(String reservationId) {

        DeleteReservationResponse deleteReservationResponse = reservationOperationsService
                .cancelReservation(reservationId);

        return new ResponseEntity<>(deleteReservationResponse, HttpStatus.OK);
    }

    @PutMapping("update-resevation")
    public ResponseEntity<ReservationAddUpdateResponse> updateReservation(String reservationId, String firstName,
                                                                          String lastName, String email,
                                                                          String startDate, String endDate) {

        ReservationAddUpdateResponse reservationAddUpdateResponse = reservationOperationsService
                .updateReservation(reservationId, firstName,
                        lastName, email,
                        startDate, endDate);

        return new ResponseEntity<>(reservationAddUpdateResponse, HttpStatus.OK);
    }

}
