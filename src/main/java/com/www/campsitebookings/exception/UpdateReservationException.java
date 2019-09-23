package com.www.campsitebookings.exception;

import com.www.campsitebookings.util.SystemEvent;

public class UpdateReservationException extends  BaseException {
    public UpdateReservationException(SystemEvent systemEvent) {
        super(systemEvent);
    }

    public UpdateReservationException(SystemEvent systemEvent, String customMessage) {
        super(systemEvent, customMessage);
    }
}
