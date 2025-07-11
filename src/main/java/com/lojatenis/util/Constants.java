package com.lojatenis.util;

public final class Constants {

    private Constants() {
        // Utility class
    }

    // JWT Constants
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String JWT_CLAIM_ROLES = "roles";

    // Pagination Constants
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "id";

    // Validation Messages
    public static final String EMAIL_INVALID = "Email deve ter formato válido";
    public static final String REQUIRED_FIELD = "Campo obrigatório";
    public static final String PRICE_INVALID = "Preço deve ser maior que zero";

    // Business Rules
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
}
