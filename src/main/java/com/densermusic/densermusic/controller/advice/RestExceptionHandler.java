package com.densermusic.densermusic.controller.advice;

import com.densermusic.densermusic.dto.errorHandlingDTO.ApiErrorDTO;
import com.densermusic.densermusic.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDTO> handleBusinessException(BusinessException ex) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ApiErrorDTO error = new ApiErrorDTO(ex.getMessage(), status.getReasonPhrase());

        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorDTO error = new ApiErrorDTO(ex.getMessage(), status.getReasonPhrase());

        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorDTO handleGenericException(Exception ex) {
        System.err.println("Ocorreu um erro inesperado: " + ex.getMessage());
        ex.printStackTrace();

        return new ApiErrorDTO("Ocorreu um erro inesperado no servidor.", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
