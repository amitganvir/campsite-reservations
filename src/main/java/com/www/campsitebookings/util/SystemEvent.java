package com.www.campsitebookings.util;

public enum SystemEvent {

    CAMPSITE_UNAVAILABLE(2, "Campsite is not available for requested dates "),
    RESERVATION_NOT_FOUND(3, "No reservation found with given reservation id "),


    //SERVICE ERRORS
    ADD_RESERVATION_EXCEPTION(50, "Exception while adding new reservation"),
    CANCEL_RESERVATION_EXCEPTION(51, "Error while cancelling reservation"),
    UPDATE_RESERVATION_EXCEPTION(52, "Exception while adding new reservation"),


    //INVALID INPUT ERRORS
    INVALID_NAME(100, "Invalid input name "),
    INVALID_EMAIL_ADDRESS(101, "Invalid email address "),
    INVALID_DATE_FORMAT(102, "Invalid date format "),
    INVALID_CHECKIN_DATE(103, "Invalid checkin date"),
    INVALID_BOOKING_DURATION(104, "Invalid booking duration "),
    INVALID_RESERVATION_ID(105, "Invalid reseration id provided ");

    private int errorCode;
    private String description;

    public int getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    SystemEvent(int errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }


}
