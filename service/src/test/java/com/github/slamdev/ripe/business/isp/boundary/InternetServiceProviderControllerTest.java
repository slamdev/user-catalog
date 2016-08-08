package com.github.slamdev.ripe.business.isp.boundary;

import com.github.slamdev.ripe.business.isp.control.InternetServiceProviderResourceProvider;
import com.github.slamdev.ripe.business.isp.entity.InternetServiceProvider;
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
public class InternetServiceProviderControllerTest {

    private static final long ID = 1L;

    @InjectMocks
    private InternetServiceProviderController controller;

    @Mock
    private InternetServiceProviderResourceProvider resourceProvider;

    @Mock
    private InternetServiceProviderRepository repository;

    @Test
    public void should_return_resource_location_after_creating() {
        InternetServiceProvider isp = InternetServiceProvider.builder().build();
        Resource<InternetServiceProvider> resource = new Resource<>(isp);
        resource.add(Link.valueOf("<example.com>;rel=\"foo\"").withSelfRel());
        when(resourceProvider.toResource(isp)).thenReturn(resource);
        ResponseEntity<Void> response = controller.create(isp);
        assertThat(response.getHeaders().getLocation(), is(create("example.com")));
    }

    @Test
    public void should_return_201_status_after_creating() {
        InternetServiceProvider isp = InternetServiceProvider.builder().build();
        Resource<InternetServiceProvider> resource = new Resource<>(isp);
        when(resourceProvider.toResource(isp)).thenReturn(resource);
        ResponseEntity<Void> response = controller.create(isp);
        assertThat(response.getStatusCode(), is(CREATED));
    }

    @Test
    public void should_save_isp_to_repository() {
        InternetServiceProvider isp = InternetServiceProvider.builder().build();
        controller.create(isp);
        verify(repository, times(1)).save(isp);
    }

    @Test
    public void should_return_isp_from_repository() {
        InternetServiceProvider isp = InternetServiceProvider.builder().build();
        when(repository.findOne(ID)).thenReturn(isp);
        controller.get(ID);
        verify(repository, times(1)).findOne(ID);
    }

    @Test
    public void should_return_resource_by_id() {
        InternetServiceProvider isp = InternetServiceProvider.builder().build();
        when(repository.findOne(ID)).thenReturn(isp);
        Resource<InternetServiceProvider> resource = new Resource<>(isp);
        when(resourceProvider.toResource(isp)).thenReturn(resource);
        assertThat(controller.get(ID), is(resource));
    }

    @Test(expected = NotFoundException.class)
    public void should_throw_exception_if_isp_id_not_found() {
        when(repository.findOne(ID)).thenReturn(null);
        controller.get(ID);
    }

    @Test
    public void should_return_204_status_when_after_deleting_isp() {
        when(repository.exists(any())).thenReturn(true);
        ResponseEntity<Void> response = controller.delete(ID);
        assertThat(response.getStatusCode(), is(NO_CONTENT));
    }

    @Test(expected = NotFoundException.class)
    public void should_throw_exception_if_isp_not_found() {
        when(repository.exists(ID)).thenReturn(false);
        controller.delete(ID);
    }

    @Test
    public void should_delete_isp_from_repository() {
        when(repository.exists(ID)).thenReturn(true);
        controller.delete(ID);
        verify(repository, times(1)).delete(ID);
    }

    @Test
    public void should_query_repository_for_all_isp() {
        when(repository.findAll()).thenReturn(emptyList());
        controller.getAll();
        verify(repository, times(1)).findAll();
    }

    @Test
    public void should_return_list_of_resources() {
        InternetServiceProvider isp = InternetServiceProvider.builder().build();
        when(repository.findAll()).thenReturn(singletonList(isp));
        Resource<InternetServiceProvider> resource = new Resource<>(isp);
        when(resourceProvider.toResourcesWrapper(singletonList(isp))).thenReturn(new Resources<>(singletonList(resource)));
        Resources<Resource> resources = controller.getAll();
        assertThat(resources.getContent(), contains(resource));
    }

    @Test
    public void should_return_empty_list_if_no_isp_found() {
        when(repository.findAll()).thenReturn(emptyList());
        when(resourceProvider.toResourcesWrapper(emptyList())).thenReturn(new Resources<>(emptyList()));
        Resources<Resource> resources = controller.getAll();
        assertThat(resources.getContent().size(), is(0));
    }
}
