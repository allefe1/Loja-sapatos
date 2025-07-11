package com.lojatenis.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private DateUtils() {
        // Utility class
    }

    public static final DateTimeFormatter BRAZILIAN_DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static final DateTimeFormatter ISO_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String formatToBrazilian(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(BRAZILIAN_DATE_TIME) : null;
    }

    public static String formatToISO(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_DATE_TIME) : null;
    }

    public static boolean isExpired(LocalDateTime expirationDate) {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }
}
