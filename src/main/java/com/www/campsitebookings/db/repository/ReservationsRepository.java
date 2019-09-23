package com.www.campsitebookings.db.repository;

import com.www.campsitebookings.db.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for handling CRUD operations for Reservations.
 */
@Repository
public interface ReservationsRepository extends JpaRepository<Reservation, String> {
}
