package com.billing.subscription.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {
    private String errorCode;
    private String errorType;
    private Map<String, List<String>> fieldErrors;
    private String details;

    public static ErrorDetails of(String errorCode, String errorType, String details) {
        return ErrorDetails.builder()
                .errorCode(errorCode)
                .errorType(errorType)
                .details(details)
                .build();
    }

    public static ErrorDetails of(String errorCode, String errorType, Map<String, List<String>> fieldErrors) {
        return ErrorDetails.builder()
                .errorCode(errorCode)
                .errorType(errorType)
                .fieldErrors(fieldErrors)
                .build();
    }
}