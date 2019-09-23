package com.www.campsitebookings.db.repository;

import com.www.campsitebookings.db.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservation, String> {
}
