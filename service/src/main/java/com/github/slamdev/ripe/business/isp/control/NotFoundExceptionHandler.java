package com.github.slamdev.ripe.business.isp.control;

import com.github.slamdev.ripe.business.isp.boundary.NotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.StringUtils.isEmpty;

@ControllerAdvice
public class NotFoundExceptionHandler {

    static final String NO_DATA = "No data";

    static final String ERROR_TYPE = "ERROR";

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public Resource<VndError> toVndErrors(NotFoundException e) {
        String message = isEmpty(e.getMessage()) ? NO_DATA : e.getMessage();
        return new Resource<>(new VndError(ERROR_TYPE, message));
    }
}
