package com.github.slamdev.ripe.business.isp.control;

import com.github.slamdev.ripe.business.isp.boundary.InternetServiceProviderController;
import com.github.slamdev.ripe.business.isp.entity.InternetServiceProvider;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class InternetServiceProviderResourceProvider
        extends ResourceAssemblerSupport<InternetServiceProvider, Resource> {

    private static final Class<InternetServiceProviderController> CONTROLLER = InternetServiceProviderController.class;

    public InternetServiceProviderResourceProvider() {
        super(CONTROLLER, Resource.class);
    }

    public Resources<Resource> toResourcesWrapper(Iterable<InternetServiceProvider> entities) {
        Link selfLink = linkTo(methodOn(CONTROLLER).getAll()).withSelfRel();
        return new Resources<>(toResources(entities), selfLink);
    }

    @Override
    public Resource<InternetServiceProvider> toResource(InternetServiceProvider entity) {
        Link selfLink = linkTo(methodOn(CONTROLLER).get(entity.getId())).withSelfRel();
        return new Resource<>(entity, selfLink);
    }
}
