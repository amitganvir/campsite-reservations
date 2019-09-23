package com.www.campsitebookings.exception;

import com.www.campsitebookings.util.SystemEvent;

public class CampsiteNotAvailableException extends BaseException {

    public CampsiteNotAvailableException(SystemEvent systemEvent) {
        super(systemEvent);
    }
}
