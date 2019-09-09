package com.campsitereservations.db;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Customer {
    private String firstName;
    private String lastName;
    private String email;
}
