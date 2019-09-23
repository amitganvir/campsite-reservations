package com.www.campsitebookings.exception;

import com.www.campsitebookings.util.SystemEvent;

public class InvalidInputException extends BaseException {

    public InvalidInputException(SystemEvent systemEvent, String customMessage) {
        super(systemEvent, customMessage);
    }

    public InvalidInputException(SystemEvent systemEvent) {
        super(systemEvent);
    }
}
