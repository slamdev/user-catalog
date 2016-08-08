package com.github.slamdev.catalog.business.user.control;

import com.github.slamdev.catalog.business.user.boundary.UserController;
import com.github.slamdev.catalog.business.user.entity.User;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserResourceProvider extends ResourceAssemblerSupport<User, Resource> {

    private static final Class<UserController> CONTROLLER = UserController.class;

    public UserResourceProvider() {
        super(CONTROLLER, Resource.class);
    }

    public Resources<Resource> toResourcesWrapper(Iterable<User> entities) {
        Link selfLink = linkTo(methodOn(CONTROLLER).getAll()).withSelfRel();
        return new Resources<>(toResources(entities), selfLink);
    }

    @Override
    public Resource<User> toResource(User entity) {
        Link selfLink = linkTo(methodOn(CONTROLLER).get(entity.getId())).withSelfRel();
        return new Resource<>(entity, selfLink);
    }
}
