package com.www.campsitebookings.exception;

import com.www.campsitebookings.util.SystemEvent;

public class BaseException extends Exception {

    private SystemEvent systemEvent;

    public BaseException(SystemEvent systemEvent) {
        super();
        this.systemEvent = systemEvent;
    }

    public BaseException(SystemEvent systemEvent, String customMessage) {
        super(customMessage);
        this.systemEvent = systemEvent;

    }

    public SystemEvent getSystemEvent() {
        return systemEvent;
    }
}
