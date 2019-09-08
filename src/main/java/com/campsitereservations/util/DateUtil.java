package com.campsitereservations.util;

import java.time.LocalDate;

public class DateUtil {

    public static boolean isEqualOrAfter(LocalDate firstDate, LocalDate secondDate) {
        return secondDate.isEqual(firstDate) || secondDate.isAfter(firstDate);
    }

    public static boolean isEqualOrBefore(LocalDate firstDate, LocalDate secondDate) {
        return secondDate.isEqual(firstDate) || secondDate.isBefore(firstDate);
    }
}
