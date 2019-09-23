package com.www.campsitebookings.db.repository;

import com.www.campsitebookings.db.CampsiteAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampsiteRepository extends JpaRepository<CampsiteAvailability, String> {

    /**
     * Cancels campsite booking for given date range. Set reservation to null for given checkin checkout dates.
     *
     * @param checkinDate
     * @param checkoutDate
     * @return
     */
    @Modifying
    @Query(value = "Update CampsiteAvailability campsiteavailability set campsiteavailability.reservation = NULL where" +
            " date >= :checkinDate and date <= :checkoutDate")
    int cancelCampsiteBooking(LocalDate checkinDate, LocalDate checkoutDate);

    /**
     * Gets campsites which are not booked within the given date range.
     *
     * @param checkinDate
     * @param checkoutDate
     * @return
     */
    @Query(value = "Select * from CAMPSITE_AVAILABILITY campsiteavailability where campsiteavailability.date >= :checkinDate " +
            " and campsiteavailability.date <= :checkoutDate and campsiteavailability.reservation_id is null", nativeQuery = true)
    List<CampsiteAvailability> findAvailableCampsites(LocalDate checkinDate, LocalDate checkoutDate);

    /**
     * Gets all campsites irrespective of booking status.
     *
     * @param checkinDate
     * @param checkoutDate
     * @return
     */
    @Query(value = "Select * from CAMPSITE_AVAILABILITY campsiteavailability where campsiteavailability.date >= :checkinDate " +
            " and campsiteavailability.date <= :checkoutDate", nativeQuery = true)
    List<CampsiteAvailability> findAllBetweenDates(LocalDate checkinDate, LocalDate checkoutDate);


}
