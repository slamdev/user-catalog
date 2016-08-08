package com.github.slamdev.catalog.business.user.control;

import com.github.slamdev.catalog.business.user.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.springframework.hateoas.Link.REL_SELF;

public class UserResourceProviderTest {

    private static final long ID = 1L;

    private static final User USER = User.builder().id(ID).build();

    private UserResourceProvider provider;

    @Before
    public void setUp() {
        provider = new UserResourceProvider();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    public void should_create_resource_from_user() {
        Resource<User> resource = provider.toResource(USER);
        assertThat(resource.getContent(), is(USER));
    }

    @Test
    public void should_add_self_link_to_resource() {
        Resource<User> resource = provider.toResource(USER);
        assertThat(resource.getLink(REL_SELF).getHref(), endsWith("api/user/" + ID));
        assertThat(resource.getLink(REL_SELF).getRel(), is(REL_SELF));
    }

    @Test
    public void should_create_wrapper_from_list_of_users() {
        Resources<Resource> resources = provider.toResourcesWrapper(singleton(USER));
        assertThat(resources.getContent(), hasItem(hasProperty("content", is(USER))));
    }

    @Test
    public void should_add_self_link_to_resources_wrapper() {
        Resources<Resource> resources = provider.toResourcesWrapper(singleton(USER));
        assertThat(resources.getLink(REL_SELF).getHref(), endsWith("api/user"));
        assertThat(resources.getLink(REL_SELF).getRel(), is(REL_SELF));
    }
}
