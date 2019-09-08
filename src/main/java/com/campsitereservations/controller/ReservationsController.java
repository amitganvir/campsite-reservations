package com.campsitereservations.controller;

import com.campsitereservations.contracts.AvailableReservationDatesResponse;
import com.campsitereservations.contracts.DeleteReservationResponse;
import com.campsitereservations.contracts.ReservationAddUpdateResponse;
import com.campsitereservations.service.ReservationOperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/")
public class ReservationsController {

    private ReservationOperationsService reservationOperationsService;

    @Autowired
    public ReservationsController(ReservationOperationsService reservationOperationsService) {
        this.reservationOperationsService = reservationOperationsService;
    }

    @GetMapping("available-dates")
    public ResponseEntity<AvailableReservationDatesResponse> getAvailableDates(@RequestParam("checkinDate") String checkinDate, @RequestParam("checkoutDate") String checkoutDate) {

        AvailableReservationDatesResponse availableReservationDatesResponse = reservationOperationsService
                .getAvailableDates(checkinDate, checkoutDate);

        return new ResponseEntity<>(availableReservationDatesResponse, HttpStatus.OK);
    }

    @PostMapping("add-reservation")
    public ResponseEntity<ReservationAddUpdateResponse> addReservation(@RequestParam("firstName") String firstName,
                                                                       @RequestParam("lastName") String lastName,
                                                                       @RequestParam("email") String email,
                                                                       @RequestParam("checkinDate") String checkinDate,
                                                                       @RequestParam("checkoutDate") String checkoutDate) {

        ReservationAddUpdateResponse reservationAddUpdateResponse = reservationOperationsService
                .addReservation(firstName, lastName, email, checkinDate, checkoutDate);

        return new ResponseEntity<>(reservationAddUpdateResponse, HttpStatus.OK);
    }

    @DeleteMapping("cancel-reservation")
    public ResponseEntity<DeleteReservationResponse> cancelReservation(@RequestParam("reservationId") String reservationId) {

        DeleteReservationResponse deleteReservationResponse = reservationOperationsService
                .cancelReservation(reservationId);

        return new ResponseEntity<>(deleteReservationResponse, HttpStatus.OK);
    }

    @PutMapping("update-reservation")
    public ResponseEntity<ReservationAddUpdateResponse> updateReservation(String reservationId, String firstName,
                                                                          String lastName, String email,
                                                                          String checkinDate, String checkoutDate) {

        ReservationAddUpdateResponse reservationAddUpdateResponse = reservationOperationsService
                .updateReservation(reservationId, firstName,
                        lastName, email,
                        checkinDate, checkoutDate);

        return new ResponseEntity<>(reservationAddUpdateResponse, HttpStatus.OK);
    }

}
