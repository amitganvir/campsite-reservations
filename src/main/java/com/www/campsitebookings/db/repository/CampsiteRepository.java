package com.www.campsitebookings.db.repository;

import com.www.campsitebookings.db.CampsiteAvailability;
import com.www.campsitebookings.db.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampsiteRepository extends JpaRepository<CampsiteAvailability, String> {

    @Modifying
    @Query(value = "Update CampsiteAvailability campsiteavailability set campsiteavailability.reservation=:reservationId where" +
            " date >= :checkinDate and date <= :checkoutDate and campsiteavailability.reservation is null ")
    int checkoutCampsite(@Param("checkinDate") LocalDate checkinDate, LocalDate checkoutDate, Reservation reservationId);

    @Modifying
    @Query(value = "Update CampsiteAvailability campsiteavailability set campsiteavailability.reservation = NULL where" +
            " date >= :checkinDate and date <= :checkoutDate")
    int cancelCampsiteBooking(LocalDate checkinDate, LocalDate checkoutDate);

    @Query(value = "Select * from CAMPSITE_AVAILABILITY campsiteavailability where campsiteavailability.date >= :checkinDate " +
            " and campsiteavailability.date <= :checkoutDate and campsiteavailability.reservation_id is null", nativeQuery = true)
    List<CampsiteAvailability> findAvailableCampsites(LocalDate checkinDate, LocalDate checkoutDate);

    @Query(value = "Select * from CAMPSITE_AVAILABILITY campsiteavailability where campsiteavailability.date >= :checkinDate " +
            " and campsiteavailability.date <= :checkoutDate", nativeQuery = true)
    List<CampsiteAvailability> findAllBetweenDates(LocalDate checkinDate, LocalDate checkoutDate);


}
