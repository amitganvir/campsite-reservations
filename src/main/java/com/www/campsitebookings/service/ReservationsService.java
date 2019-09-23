package com.www.campsitebookings.service;

import com.www.campsitebookings.contracts.CampsiteAvailabilityModel;
import com.www.campsitebookings.contracts.CancelReservationModel;
import com.www.campsitebookings.contracts.ReservationConfirmationModel;
import com.www.campsitebookings.db.CampsiteAvailability;
import com.www.campsitebookings.db.Reservation;
import com.www.campsitebookings.db.repository.CampsiteRepository;
import com.www.campsitebookings.db.repository.ReservationsRepository;
import com.www.campsitebookings.exception.CampsiteNotAvailableException;
import com.www.campsitebookings.exception.CancelReservationException;
import com.www.campsitebookings.exception.InvalidInputException;
import com.www.campsitebookings.exception.ReservationNotFoundException;
import com.www.campsitebookings.mapper.ReservationsMapper;
import com.www.campsitebookings.util.RequestValidation;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.www.campsitebookings.util.SystemEvent.*;


@Service
@Transactional
public class ReservationsService {

    private Logger logger = LoggerFactory.getLogger(ReservationsService.class);

    private CampsiteRepository campsiteRepository;
    private ReservationsRepository reservationsRepository;
    private ReservationsMapper reservationsMapper;

    @Autowired
    public ReservationsService(CampsiteRepository campsiteRepository,
                               ReservationsRepository reservationsRepository,
                               ReservationsMapper reservationsMapper) {
        this.campsiteRepository = campsiteRepository;
        this.reservationsRepository = reservationsRepository;
        this.reservationsMapper = reservationsMapper;
    }

    @Transactional(readOnly = true)
    public CampsiteAvailabilityModel getAvailableDates(String checkinDate, String checkoutDate) throws InvalidInputException{

        try {

            RequestValidation.validateCheckinCheckoutDateFormat(checkinDate, checkoutDate);
            LocalDate localCheckinDate = LocalDate.parse(checkinDate);
            LocalDate localCheckoutDate = LocalDate.parse(checkoutDate);

            List<CampsiteAvailability> availableDates = findCampsiteAvailability(localCheckinDate, localCheckoutDate);
            return reservationsMapper.mapToCampsiteAvailabilityModel(availableDates.stream().map(CampsiteAvailability::getDate).collect(Collectors.toList()));
        } catch (CampsiteNotAvailableException campsiteUnavailableException) {
            logger.error(campsiteUnavailableException.getSystemEvent().getDescription());
            return reservationsMapper.mapToNoAvailabilityModel(campsiteUnavailableException.getSystemEvent());
        }
    }

    @Transactional
    public ReservationConfirmationModel addReservation(String name, String email, String checkinDate, String checkoutDate) throws InvalidInputException {


        try {

            RequestValidation.validateInputParameters(name, email, checkinDate, checkoutDate);

            LocalDate localCheckinDate = LocalDate.parse(checkinDate);
            LocalDate localCheckoutDate = LocalDate.parse(checkoutDate);

            List<CampsiteAvailability> availableCampsites = findCampsiteAvailability(localCheckinDate, localCheckoutDate);
            int numberOfDays = findNumberOfDays(localCheckinDate, localCheckoutDate);

            if (availableCampsites.size() != numberOfDays) {
                throw new CampsiteNotAvailableException(CAMPSITE_UNAVAILABLE);
            }
            Reservation reservation = reservationsMapper.mapToReservation(name, email, availableCampsites.stream()
                    .map(CampsiteAvailability::getDate).collect(Collectors.toList()));

            availableCampsites.stream().forEach(campsiteAvailability -> campsiteAvailability.setReservation(reservation));

            campsiteRepository.saveAll(availableCampsites);
            return reservationsMapper.mapToReservationConfirmationModel(reservation, localCheckinDate, localCheckoutDate);


        } catch (CampsiteNotAvailableException campsiteUnavailableException) {
            logger.error(campsiteUnavailableException.getSystemEvent().getDescription());
            return reservationsMapper.mapToFailedReservationConfirmationModel(campsiteUnavailableException.getSystemEvent());
        }
    }

    public CancelReservationModel cancelReservation(String reservationId) throws InvalidInputException{

        try {

            RequestValidation.validateReservationId(reservationId);

            Reservation reservation = findReservation(reservationId);

            LocalDate checkinDate = reservation.getCampsiteAvailabilities().get(0).getDate();
            LocalDate checkoutDate = reservation.getCampsiteAvailabilities().get(reservation.getCampsiteAvailabilities().size() - 1).getDate();

            int numberOfDays = findNumberOfDays(checkinDate, checkoutDate);
            int updateCount = campsiteRepository.cancelCampsiteBooking(checkinDate, checkoutDate);

            if (numberOfDays != updateCount) {
                throw new CancelReservationException(CANCEL_RESERVATION_EXCEPTION);
            }

            reservationsRepository.deleteById(reservationId);
            return reservationsMapper.mapToSuccessfulCancelReservationModel();

        } catch (ReservationNotFoundException | CancelReservationException exception) {
            logger.error(exception.getSystemEvent().getDescription());
            return reservationsMapper.mapToFailedCancelReservationModel(exception.getSystemEvent());
        }
    }

    private Reservation findReservation(String reservationId) throws ReservationNotFoundException {
        Optional<Reservation> reservationOptional = reservationsRepository.findById(reservationId);

        if (reservationOptional.isPresent()) {
            return reservationOptional.get();
        }
        throw new ReservationNotFoundException(RESERVATION_NOT_FOUND);
    }

    private List<CampsiteAvailability> findCampsiteAvailability(LocalDate checkinDate,
                                                     LocalDate checkoutDate) throws CampsiteNotAvailableException {

        List<CampsiteAvailability> availableCampsites = campsiteRepository.findAvailableCampsites(checkinDate, checkoutDate);

        if (CollectionUtils.isEmpty(availableCampsites)) {
            throw new CampsiteNotAvailableException(CAMPSITE_UNAVAILABLE);
        }

        return availableCampsites;

    }

    public ReservationConfirmationModel updateReservation(String reservationId, String name, String email,
                                                          String checkinDate, String checkoutDate)
            throws InvalidInputException {

        try {
            RequestValidation.validateInputParameters(name, email, checkinDate, checkoutDate);
            LocalDate localCheckinDate = LocalDate.parse(checkinDate);
            LocalDate localCheckoutDate = LocalDate.parse(checkoutDate);

            Reservation reservation = findReservation(reservationId);
            reservation.setName(name);
            reservation.setEmail(email);

            List<CampsiteAvailability> oldAvailabilities = reservation.getCampsiteAvailabilities();

            LocalDate oldCheckInDate = oldAvailabilities.get(0).getDate();
            LocalDate oldCheckoutDate = oldAvailabilities.get(oldAvailabilities.size() -1 ).getDate();

            Reservation updatedReservation;

            if (oldCheckInDate.equals(localCheckinDate) && oldCheckoutDate.equals(localCheckoutDate)) {
                updatedReservation = reservationsRepository.save(reservation);
            } else {
                List<CampsiteAvailability> availableDates = findCampsiteAvailability(localCheckinDate, localCheckoutDate);

                int numberOfDays =  findNumberOfDays(localCheckinDate, localCheckoutDate);
                Set<String> numberOfOverlappingDays = findNumberOfOverlappingDays(oldCheckInDate, oldCheckoutDate,
                        localCheckinDate, localCheckoutDate);

                if ((numberOfOverlappingDays.size() == 0 && (numberOfDays != availableDates.size()))) {
                    throw new CampsiteNotAvailableException(CAMPSITE_UNAVAILABLE);
                }

                if (numberOfOverlappingDays.size() > 0 && (numberOfDays != availableDates.size() &&
                        (numberOfOverlappingDays.size() + availableDates.size()) != numberOfDays)) {
                    throw new CampsiteNotAvailableException(CAMPSITE_UNAVAILABLE);
                }

                List<CampsiteAvailability> availableCampsites = campsiteRepository.findAllBetweenDates(oldCheckInDate, oldCheckoutDate);
                availableCampsites.forEach(campsiteAvailability -> campsiteAvailability.setReservation(null));

                List<CampsiteAvailability> newCampsites = campsiteRepository.findAllBetweenDates(localCheckinDate, localCheckoutDate);
                newCampsites.forEach(availableDate -> availableDate.setReservation(reservation));

                campsiteRepository.saveAll(availableDates);
                updatedReservation = reservation;
                updatedReservation.setCampsiteAvailabilities(newCampsites);
            }

            return reservationsMapper.mapToReservationConfirmationModel(updatedReservation,
                    updatedReservation.getCampsiteAvailabilities().get(0).getDate(),
                    updatedReservation.getCampsiteAvailabilities()
                            .get(updatedReservation.getCampsiteAvailabilities().size() - 1).getDate());


        } catch (CampsiteNotAvailableException | ReservationNotFoundException exception) {
            return reservationsMapper.mapToFailedReservationConfirmationModel(exception.getSystemEvent());
        }
    }

    private Set<String> findNumberOfOverlappingDays(LocalDate oldCheckinDate, LocalDate oldCheckoutDate,
                                            LocalDate newCheckinDate, LocalDate newCheckoutDate) {


        if (newCheckinDate.isAfter(oldCheckinDate) || newCheckinDate.equals(oldCheckinDate)) {
           return findDates(newCheckinDate, oldCheckoutDate);
        } else if (newCheckoutDate.isBefore(oldCheckoutDate)) {
            return findDates(newCheckoutDate, oldCheckoutDate);
        }

        return Collections.emptySet();
    }

    private int findNumberOfDays(LocalDate checkinDate, LocalDate checkoutDate) {
        return checkinDate.until(checkoutDate.plusDays(1)).getDays();
    }

    private Set<String> findDates(LocalDate checkinDate, LocalDate checkoutDate) {

        Set<String> dates = new HashSet<>();

        while (checkinDate.isBefore(checkoutDate.plusDays(1))) {
            dates.add(checkinDate.toString());
            checkinDate = checkinDate.plusDays(1);
        }

        return dates;
    }
}