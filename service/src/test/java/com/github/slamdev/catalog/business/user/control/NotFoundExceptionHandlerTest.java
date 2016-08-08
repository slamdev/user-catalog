package com.github.slamdev.catalog.business.user.control;

import com.github.slamdev.catalog.business.user.boundary.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.VndErrors.VndError;

import static com.github.slamdev.catalog.business.user.control.NotFoundExceptionHandler.ERROR_TYPE;
import static com.github.slamdev.catalog.business.user.control.NotFoundExceptionHandler.NO_DATA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NotFoundExceptionHandlerTest {

    private NotFoundExceptionHandler handler;

    @Before
    public void setUp() {
        handler = new NotFoundExceptionHandler();
    }

    @Test
    public void should_create_vnd_error_with_error_type() {
        Resource<VndError> error = handler.toVndErrors(new NotFoundException());
        assertThat(error.getContent().getLogref(), is(ERROR_TYPE));
    }

    @Test
    public void should_create_vnd_error_with_message_from_exception() {
        String message = "some message";
        Resource<VndError> error = handler.toVndErrors(new NotFoundException(message));
        assertThat(error.getContent().getMessage(), is(message));
    }

    @Test
    public void should_create_vnd_error_with_default_message_is_exception_is_null() {
        Resource<VndError> error = handler.toVndErrors(new NotFoundException(null));
        assertThat(error.getContent().getMessage(), is(NO_DATA));
    }

    @Test
    public void should_create_vnd_error_with_default_message_is_exception_is_empty() {
        Resource<VndError> error = handler.toVndErrors(new NotFoundException(""));
        assertThat(error.getContent().getMessage(), is(NO_DATA));
    }
}