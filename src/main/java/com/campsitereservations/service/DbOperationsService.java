package com.campsitereservations.service;

import com.campsitereservations.db.CampsiteInMemoryDatabase;
import com.campsitereservations.db.ReservationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DbOperationsService {

    private CampsiteInMemoryDatabase campsiteInMemoryDatabase;

    @Autowired
    public DbOperationsService(CampsiteInMemoryDatabase campsiteInMemoryDatabase) {
        this.campsiteInMemoryDatabase = campsiteInMemoryDatabase;
    }

    public List<LocalDate> getAvailableDates(LocalDate startDate, LocalDate endDate) {
        return campsiteInMemoryDatabase.getCampsiteAvailability(startDate, endDate);
    }

    public boolean addReservation(ReservationDetails reservationDetails) {
        return campsiteInMemoryDatabase.addReservation(reservationDetails);
    }

    public boolean deleteReservation(String reservationId) {
        return campsiteInMemoryDatabase.deleteReservation(reservationId);
    }

    public boolean updateReservation(ReservationDetails oldReservation, ReservationDetails newReservation) {
        return campsiteInMemoryDatabase.updateReservation(oldReservation, newReservation);
    }
}
