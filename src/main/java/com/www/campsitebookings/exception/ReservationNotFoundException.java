package com.www.campsitebookings.exception;

import com.www.campsitebookings.util.SystemEvent;

public class ReservationNotFoundException extends BaseException{

    public ReservationNotFoundException(SystemEvent systemEvent) {
        super(systemEvent);
    }
}
