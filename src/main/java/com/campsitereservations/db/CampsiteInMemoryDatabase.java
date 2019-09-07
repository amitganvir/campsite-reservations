package com.campsitereservations.db;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class CampsiteInMemoryDatabase {

    private HashMap<String, ReservationDetails> reservations;
    private HashSet<LocalDate> campsiteAvailabilityData;
    private Lock lock = new ReentrantLock();


    @PostConstruct
    public void initialize() {
        reservations = new HashMap<>();
        campsiteAvailabilityData = initializeCampsiteAvailabilityData();
    }

    private HashSet<LocalDate> initializeCampsiteAvailabilityData() {
        HashSet<LocalDate> campsiteAvailabilityData = new HashSet<>();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(32);

        while (startDate.isBefore(endDate)) {
            campsiteAvailabilityData.add(startDate);
            startDate = startDate.plusDays(1);
        }

        return campsiteAvailabilityData;
    }

    public List<LocalDate> getCampsiteAvailability(LocalDate startDate, LocalDate endDate) {

        lock.lock();
        try {
            return campsiteAvailabilityData.stream().filter(date -> isEqualOrAfter(startDate, date) &&
                    isEqualOrBefore(endDate, date)).collect(Collectors.toList());
        } catch (Exception exception) {
            throw new RuntimeException("Exception while reading data for campsite");
        } finally {
            lock.unlock();
        }
    }

    public boolean addReservation(ReservationDetails reservationDetails) {

        lock.lock();

        try {

            if (datesAvailableForReservation(reservationDetails.getReservationsDates())) {
                reservations.put(reservationDetails.getReservationId(), reservationDetails);
                deleteCampsiteAvailabilityData(reservationDetails.getReservationsDates());
            } else {
                throw new RuntimeException("Campsite already booked for given dates. Please try other dates");
            }
        } catch(RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new RuntimeException("Exception while booking campsite");
        } finally {
            lock.unlock();
        }
        return true;
    }

    public ReservationDetails findReservation(String reservationId) {
        lock.lock();
        ReservationDetails reservationDetails = null;

        try {
            reservationDetails = reservations.get(reservationId);
        } catch (Exception exception) {
            System.out.println("Exception while finding reservation with reservation id : " + reservationId);
        } finally {
            lock.unlock();
        }

        return reservationDetails;
    }

    public boolean deleteReservation(String reservationId) {
        lock.lock();

        try {
            if (!reservations.containsKey(reservationId)) {
                throw new RuntimeException("No reservation found with reservation id " + reservationId);
            }

            ReservationDetails reservationDetails = reservations.remove(reservationId);
            return reservationDetails != null && deleteCampsiteAvailabilityData(reservationDetails.getReservationsDates());
        } catch (Exception exception) {
            throw new RuntimeException("Exception while adding reservation data");
        } finally {
            lock.unlock();
        }
    }

    private boolean deleteCampsiteAvailabilityData(ReservationsDates reservationsDates) {

        lock.lock();
        try {
            LocalDate startDate = reservationsDates.getStartDate();
            LocalDate endDate = reservationsDates.getEndDate().plusDays(1);

            while (startDate.isBefore(endDate)) {
                campsiteAvailabilityData.remove(startDate);
                startDate = startDate.plusDays(1);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Exception while deleting campsite availability data");
        } finally {
            lock.unlock();
        }
        return true;
    }

    private boolean addCampsiteAvailabilityData(ReservationsDates reservationsDates) {

        lock.lock();
        try {
            LocalDate startDate = reservationsDates.getStartDate();
            LocalDate endDate = reservationsDates.getEndDate().plusDays(1);

            while (startDate.isBefore(endDate)) {
                campsiteAvailabilityData.add(startDate);
                startDate = startDate.plusDays(1);
            }
        } catch (Exception exception) {
            System.out.println("Exception while adding reservation data");
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }

    public boolean updateReservation(ReservationDetails oldReservationDetails,
                                      ReservationDetails newReservationDetails) {

        lock.lock();
        try {

            if (datesAvailableForReservation(newReservationDetails.getReservationsDates())) {
                reservations.put(newReservationDetails.getReservationId(), newReservationDetails);
                deleteCampsiteAvailabilityData(oldReservationDetails.getReservationsDates());
                addCampsiteAvailabilityData(newReservationDetails.getReservationsDates());
            }
        } catch (Exception exception) {
            System.out.println("Exception while adding reservation data");
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }

    private boolean isEqualOrAfter(LocalDate firstDate, LocalDate secondDate) {
        return secondDate.isEqual(firstDate) || secondDate.isAfter(firstDate);
    }

    private boolean isEqualOrBefore(LocalDate firstDate, LocalDate secondDate) {
        return secondDate.isEqual(firstDate) || secondDate.isBefore(firstDate);
    }

    private boolean datesAvailableForReservation(ReservationsDates reservationsDates) {

        lock.lock();
        try {
            LocalDate currentDate = reservationsDates.getStartDate();
            LocalDate finalEndDate = reservationsDates.getEndDate().plusDays(1);

            while (currentDate.isBefore(finalEndDate)) {

                if (!campsiteAvailabilityData.contains(currentDate)) {
                    return false;
                }

                currentDate = currentDate.plusDays(1);
            }

        } catch (Exception exception) {
            throw new RuntimeException("Exception while reading data for campsite");
        } finally {
            lock.unlock();
        }

        return true;
    }
}
