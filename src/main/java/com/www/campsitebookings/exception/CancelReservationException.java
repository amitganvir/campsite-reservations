package com.www.campsitebookings.exception;

import com.www.campsitebookings.util.SystemEvent;

public class CancelReservationException extends BaseException {

    public CancelReservationException(SystemEvent systemEvent) {
        super(systemEvent);
    }
}
