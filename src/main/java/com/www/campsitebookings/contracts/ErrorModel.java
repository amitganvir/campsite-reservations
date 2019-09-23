package com.www.campsitebookings.contracts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorModel {
    private Integer errorCode;
    private String errorMessage;
}
