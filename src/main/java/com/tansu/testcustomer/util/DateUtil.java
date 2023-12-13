package com.tansu.testcustomer.util;

import java.time.format.DateTimeFormatter;

/**
 * @author Get Arrays (https://www.getarrays.io/)
 * @version 1.0
 * @since 5/24/2021
 */
public class DateUtil {
    public static DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");
    }
}
