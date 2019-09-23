package com.www.campsitebookings.util;

import com.www.campsitebookings.exception.InvalidInputException;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.www.campsitebookings.util.SystemEvent.*;


public class RequestValidation {

    private static final int MAX_BOOKING_DAYS = 3;
    private static final int LATEST_BOOKING_DAY = 1;
    private static final int EARLIEST_BOOKING_DAY = 31;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";



    public static void validateInputParameters(String name, String email, String checkinDate, String checkoutDate) throws InvalidInputException {

        validateName(name);
        validateEmail(email);
        validateDates(checkinDate, checkoutDate);

    }

    public static void validateReservationId(String reservationId) throws InvalidInputException{

        if (StringUtils.isEmpty(reservationId)) {
            throw new InvalidInputException(INVALID_RESERVATION_ID, "Reservation Id cannot be empty");
        }
    }

    private static void validateName(String name) throws InvalidInputException {
        if (StringUtils.isEmpty(name)) {
            throw new InvalidInputException(INVALID_NAME, "Name cannot be empty");
        }
    }

    private static void validateEmail(String emailAddress) throws InvalidInputException {
        Matcher matcher = Pattern.compile(EMAIL_PATTERN).matcher(emailAddress);
        if (!matcher.matches()) {
            throw new InvalidInputException(INVALID_EMAIL_ADDRESS, "Invalid email address : " + emailAddress);
        }
    }

    public static void validateCheckinCheckoutDateFormat(String startDate, String endDate) throws InvalidInputException {

        try {
            LocalDate.from(DateTimeFormatter.ofPattern(DATE_FORMAT).parse(startDate));
            LocalDate.from(DateTimeFormatter.ofPattern(DATE_FORMAT).parse(endDate));
        } catch (Exception exception) {
            throw new InvalidInputException(INVALID_DATE_FORMAT);
        }
    }

    private static void validateDates(String checkinDate, String checkoutDate) throws InvalidInputException{
        validateCheckinCheckoutDateFormat(checkinDate, checkoutDate);
        validateReservationDates(checkinDate, checkoutDate);
    }

    private static void validateReservationDates(String checkinDate, String checkoutDate) throws InvalidInputException {

        LocalDate localCheckinDate = LocalDate.parse(checkinDate);
        LocalDate localCheckoutDate = LocalDate.parse(checkoutDate);

        validateCheckInAndCheckoutDateRange(localCheckinDate, localCheckoutDate);
        validateBookingDate(localCheckinDate);
    }

    private static void validateBookingDate(LocalDate checkinDate) throws InvalidInputException {

        if (checkinDate.minusDays(LATEST_BOOKING_DAY).isBefore(LocalDate.now())) {
            throw new InvalidInputException(INVALID_CHECKIN_DATE,
                    "Campsite has to be book by at least " + LATEST_BOOKING_DAY + " day in advance");
        } else if (checkinDate.minusDays(EARLIEST_BOOKING_DAY).isAfter(LocalDate.now())) {
            throw new InvalidInputException(INVALID_CHECKIN_DATE,  "Campsite cannot be book before " + EARLIEST_BOOKING_DAY + " days");
        }
    }

    private static void  validateCheckInAndCheckoutDateRange(LocalDate checkinDate, LocalDate checkoutDate) throws InvalidInputException {

        if (checkoutDate.minusDays(MAX_BOOKING_DAYS).isAfter(checkinDate)) {
            throw new InvalidInputException(INVALID_BOOKING_DURATION, "Invalid checkin & checkout dates: "
                    + checkinDate.toString() + " & " + checkoutDate.toString() +
                    " . Campsite cannot be booked for more than 3 days");
        } else if (checkoutDate.isBefore(checkinDate)) {
            throw new InvalidInputException(INVALID_BOOKING_DURATION, "Invalid checkout date: " + checkoutDate.toString() +
                    " . Checkout data should be after checkin date.");
        }
    }

}
