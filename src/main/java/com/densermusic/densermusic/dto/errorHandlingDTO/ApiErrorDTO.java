package com.densermusic.densermusic.dto.errorHandlingDTO;

import java.util.List;

public record ApiErrorDTO(
        String message,
        String error,
        int status,
        String path,
        List<String> details
) {
    public ApiErrorDTO(String message, String error, int status, String path) {
        this(message, error, status, path, null);
    }
}
