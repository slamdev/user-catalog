package com.github.slamdev.catalog.business.user.boundary;

import com.github.slamdev.catalog.business.user.control.UserResourceProvider;
import com.github.slamdev.catalog.business.user.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;

import static java.net.URI.create;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private static final long ID = 1L;

    @InjectMocks
    private UserController controller;

    @Mock
    private UserResourceProvider resourceProvider;

    @Mock
    private UserRepository repository;

    @Test
    public void should_return_resource_location_after_creating() {
        User user = User.builder().build();
        Resource<User> resource = new Resource<>(user);
        resource.add(Link.valueOf("<example.com>;rel=\"foo\"").withSelfRel());
        when(resourceProvider.toResource(user)).thenReturn(resource);
        ResponseEntity<Void> response = controller.create(user);
        assertThat(response.getHeaders().getLocation(), is(create("example.com")));
    }

    @Test
    public void should_return_201_status_after_creating() {
        User user = User.builder().build();
        Resource<User> resource = new Resource<>(user);
        when(resourceProvider.toResource(user)).thenReturn(resource);
        ResponseEntity<Void> response = controller.create(user);
        assertThat(response.getStatusCode(), is(CREATED));
    }

    @Test
    public void should_save_user_to_repository() {
        User user = User.builder().build();
        controller.create(user);
        verify(repository, times(1)).save(user);
    }

    @Test
    public void should_return_user_from_repository() {
        User user = User.builder().build();
        when(repository.findOne(ID)).thenReturn(user);
        controller.get(ID);
        verify(repository, times(1)).findOne(ID);
    }

    @Test
    public void should_return_resource_by_id() {
        User user = User.builder().build();
        when(repository.findOne(ID)).thenReturn(user);
        Resource<User> resource = new Resource<>(user);
        when(resourceProvider.toResource(user)).thenReturn(resource);
        assertThat(controller.get(ID), is(resource));
    }

    @Test(expected = NotFoundException.class)
    public void should_throw_exception_if_user_id_not_found() {
        when(repository.findOne(ID)).thenReturn(null);
        controller.get(ID);
    }

    @Test
    public void should_return_204_status_when_after_deleting_user() {
        when(repository.exists(ID)).thenReturn(true);
        ResponseEntity<Void> response = controller.delete(ID);
        assertThat(response.getStatusCode(), is(NO_CONTENT));
    }

    @Test(expected = NotFoundException.class)
    public void should_throw_exception_if_user_not_found() {
        when(repository.exists(ID)).thenReturn(false);
        controller.delete(ID);
    }

    @Test
    public void should_delete_user_from_repository() {
        when(repository.exists(ID)).thenReturn(true);
        controller.delete(ID);
        verify(repository, times(1)).delete(ID);
    }

    @Test
    public void should_query_repository_for_all_users() {
        when(repository.findAll()).thenReturn(emptyList());
        controller.getAll();
        verify(repository, times(1)).findAll();
    }

    @Test
    public void should_return_list_of_resources() {
        User user = User.builder().build();
        when(repository.findAll()).thenReturn(singletonList(user));
        Resource<User> resource = new Resource<>(user);
        when(resourceProvider.toResourcesWrapper(singletonList(user))).thenReturn(new Resources<>(singletonList(resource)));
        Resources<Resource> resources = controller.getAll();
        assertThat(resources.getContent(), contains(resource));
    }

    @Test
    public void should_return_empty_list_if_no_users_found() {
        when(repository.findAll()).thenReturn(emptyList());
        when(resourceProvider.toResourcesWrapper(emptyList())).thenReturn(new Resources<>(emptyList()));
        Resources<Resource> resources = controller.getAll();
        assertThat(resources.getContent().size(), is(0));
    }
}
