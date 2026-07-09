package com.example.carinsurance.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> details;
}
