package com.github.slamdev.catalog.integration;

import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.join;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

    private static final String ERROR_TYPE = "FORM_VALIDATION_ERROR";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Resources<VndError> toVndErrors(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        List<VndError> errors = concat(toFieldVndErrors(result.getFieldErrors()),
                toGlobalVndErrors(result.getGlobalErrors())).map(this::toVndError).collect(toList());
        return new Resources<>(new VndErrors(errors));
    }

    private Stream<String> toFieldVndErrors(List<FieldError> errors) {
        return errors.stream().map(error -> join(", ", error.getField(), error.getDefaultMessage()));
    }

    private Stream<String> toGlobalVndErrors(List<ObjectError> errors) {
        return errors.stream().map(ObjectError::getDefaultMessage);
    }

    private VndError toVndError(String message) {
        return new VndError(ERROR_TYPE, message);
    }
}
