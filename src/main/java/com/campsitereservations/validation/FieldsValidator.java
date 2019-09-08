package com.campsitereservations.validation;

import com.campsitereservations.exceptions.InvalidInputException;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldsValidator {

    private static final int MAX_BOOKING_DAYS = 3;
    private static final int LATEST_BOOKING_DAY = 1;
    private static final int EARLIEST_BOOKING_DAY = 31;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    private static void validateEmail(String emailAddress) throws Exception {
            Matcher matcher = Pattern.compile(EMAIL_PATTERN).matcher(emailAddress);
             if (!matcher.matches()) {
                 throw new InvalidInputException("Invalid email address : " + emailAddress);
             }
    }

    public static boolean validInputFields(String firstName, String lastName, String email,
                                           String startDate, String endDate) throws Exception {

        if (validString(startDate) && validString(endDate) && validString(firstName) && validString(lastName) &&
                validString(email)) {

            validateEmail(email);
            validateCheckinCheckoutDates(startDate, endDate);
            return true;
        }

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Invalid input parameters : ")
                .append("firstName=").append(firstName).append("\n")
                .append("lastName=").append(lastName).append("\n")
                .append("email=").append(email).append("\n")
                .append("startDate=").append(startDate).append("\n")
                .append("endDate=").append(endDate).append("\n");

        throw new InvalidInputException(strBuilder.toString());
    }

    private static boolean validateCheckinCheckoutDates(String startDate, String endDate) throws Exception {

        LocalDate checkInDate = null;
        LocalDate checkoutDate = null;
        try {
            checkInDate = LocalDate.from(DateTimeFormatter.ofPattern(DATE_FORMAT).parse(startDate));
            checkoutDate = LocalDate.from(DateTimeFormatter.ofPattern(DATE_FORMAT).parse(endDate));
        } catch (Exception exception) {
            throw new InvalidInputException("Invalid checkin - checkout date format. Valid date format is  yyyy-MM-dd");
        }

        validateReservationDates(checkInDate, checkoutDate);

        return true;
    }

    public static boolean validString(String inputString) {
        return !StringUtils.isEmpty(inputString.trim());
    }

    private static boolean validateReservationDates(LocalDate checkinDate, LocalDate checkoutDate) throws Exception {

        validateCheckInAndCheckoutDateRange(checkinDate, checkoutDate);
        validateBookingDate(checkinDate);
        return true;
    }

    private static boolean validateBookingDate(LocalDate checkinDate) throws Exception {

        if (checkinDate.minusDays(LATEST_BOOKING_DAY).isBefore(LocalDate.now())) {
            throw new InvalidInputException("Invalid booking date. You should book at least 1 day in advance ");
        } else if (checkinDate.minusDays(EARLIEST_BOOKING_DAY).isAfter(LocalDate.now())) {
            throw new InvalidInputException("Invalid booking date. You can book upto 1 month in advance");
        }

        return true;
    }

    private static boolean validateCheckInAndCheckoutDateRange(LocalDate checkinDate, LocalDate checkoutDate) throws Exception {

        if (checkoutDate.minusDays(MAX_BOOKING_DAYS).isAfter(checkinDate)) {
            throw new InvalidInputException("Invalid checkin & checkout dates: "
                    + checkinDate.toString() + " & " + checkoutDate.toString() +
                    " . Campsite cannot be booked for more than 3 days");
        } else if (checkoutDate.isBefore(checkinDate)) {
            throw new InvalidInputException("Invalid checkout date: " + checkoutDate.toString() +
                    " . Checkout data should be after checkin date.");
        }
        return true;
    }
}
