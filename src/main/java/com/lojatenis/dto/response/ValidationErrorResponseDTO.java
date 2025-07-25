package com.lojatenis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponseDTO {
    private int status;
    private String error;
    private Map<String, String> validationErrors;
    private LocalDateTime timestamp;
}
