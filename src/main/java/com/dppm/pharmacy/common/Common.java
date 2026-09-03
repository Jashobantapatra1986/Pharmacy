package com.dppm.pharmacy.common;


import com.dppm.pharmacy.constants.PharmacyConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


import static com.dppm.pharmacy.constants.PharmacyConstants.DATE_FORMAT_VIEW;
import static com.dppm.pharmacy.constants.PharmacyConstants.DATE_ONLY_FORMAT_VIEW;

public class Common {
    private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter VIEW_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_VIEW);
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern(DATE_ONLY_FORMAT_VIEW);
    private static final DateTimeFormatter ID_DATE_FORMATTER = DateTimeFormatter.ofPattern(
            PharmacyConstants.DATE_TIME_FORMATTER_ID_CREATION
    );

    private Common() {
        throw new IllegalStateException("Utility class: Common");
    }

    public static LocalDateTime getIndiaLocalDateTime() {
        return LocalDateTime.now(INDIA_ZONE);
    }

    public static void sleep(Integer milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static boolean isTrue(Boolean input) {
        return Boolean.TRUE.equals(input);
    }

    private static String generateGuid() {
        return UUID.randomUUID().toString();
    }

    public static String generateId(String registrationCode) {
        long currentTimeMillis = System.currentTimeMillis();
        return registrationCode + "-"
                + generateGuid()
                .replace("-", "")
                .substring(0, 8) + "-"
                + currentTimeMillis;
    }

    public static String convertDateToString(LocalDateTime date) {
        return date.format(VIEW_DATE_FORMATTER);
    }

    public static LocalDateTime convertStringToLocalDateTime(String date) {
        return LocalDate.parse(date, DATE_ONLY_FORMATTER).atTime(LocalTime.MIDNIGHT);
    }

    public static String generateDateGUID() {
        String date = LocalDateTime.now().format(ID_DATE_FORMATTER);
        return generateGuid() + "_" + date;
    }
}
