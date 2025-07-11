package com.lojatenis.util;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private ValidationUtils() {
        // Utility class
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}[\\s-]?\\d{4}$");

    private static final Pattern CEP_PATTERN =
            Pattern.compile("^\\d{8}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidCep(String cep) {
        return cep != null && CEP_PATTERN.matcher(cep).matches();
    }

    public static String formatCep(String cep) {
        if (cep == null) return null;
        return cep.replaceAll("\\D", "");
    }
}
