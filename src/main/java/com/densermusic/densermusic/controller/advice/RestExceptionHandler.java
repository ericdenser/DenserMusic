package com.densermusic.densermusic.controller.advice;

import com.densermusic.densermusic.dto.errorHandlingDTO.ApiErrorDTO;
import com.densermusic.densermusic.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestControllerAdvice
@CrossOrigin(origins = "*")
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDTO> handleBusinessException(BusinessException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ApiErrorDTO error = new ApiErrorDTO(
                ex.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                request.getDescription(false));

        logger.warn("BusinessException: {}", ex.getMessage());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorDTO error = new ApiErrorDTO(
                ex.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                request.getDescription(false));

        logger.warn("Illegal Argument: {}", ex.getMessage());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;


        ApiErrorDTO error = new ApiErrorDTO(
                "Ocorreu um erro inesperado no servidor.",
                status.getReasonPhrase(),
                status.value(),
                request.getDescription(false));

        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;


        // captura todas mensagens de erro de validacão
        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ApiErrorDTO error = new ApiErrorDTO(
                ex.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                request.getDescription(false),
                validationErrors);

        logger.warn("Erro de validacão {}: ", ex.getMessage(), ex);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDTO> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());

        ApiErrorDTO error = new ApiErrorDTO(
                ex.getReason(),
                status != null ? status.getReasonPhrase() : "Error",  // BAD_REQUEST, NOT_FOUND etc.
                ex.getStatusCode().value(),
                request.getDescription(false)
        );

        logger.warn("ResponseStatusException: {}", ex.getReason());
        return new ResponseEntity<>(error, status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
