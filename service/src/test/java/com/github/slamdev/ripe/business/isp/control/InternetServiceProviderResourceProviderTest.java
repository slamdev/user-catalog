package com.github.slamdev.ripe.business.isp.control;

import com.github.slamdev.ripe.business.isp.entity.InternetServiceProvider;
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

public class InternetServiceProviderResourceProviderTest {

    private static final long ID = 1L;

    private static final InternetServiceProvider ISP = InternetServiceProvider.builder().id(ID).build();

    private InternetServiceProviderResourceProvider provider;

    @Before
    public void setUp() {
        provider = new InternetServiceProviderResourceProvider();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    public void should_create_resource_from_isp() {
        Resource<InternetServiceProvider> resource = provider.toResource(ISP);
        assertThat(resource.getContent(), is(ISP));
    }

    @Test
    public void should_add_self_link_to_resource() {
        Resource<InternetServiceProvider> resource = provider.toResource(ISP);
        assertThat(resource.getLink(REL_SELF).getHref(), endsWith("api/isp/" + ID));
        assertThat(resource.getLink(REL_SELF).getRel(), is(REL_SELF));
    }

    @Test
    public void should_create_wrapper_from_list_of_isp() {
        Resources<Resource> resources = provider.toResourcesWrapper(singleton(ISP));
        assertThat(resources.getContent(), hasItem(hasProperty("content", is(ISP))));
    }

    @Test
    public void should_add_self_link_to_resources_wrapper() {
        Resources<Resource> resources = provider.toResourcesWrapper(singleton(ISP));
        assertThat(resources.getLink(REL_SELF).getHref(), endsWith("api/isp"));
        assertThat(resources.getLink(REL_SELF).getRel(), is(REL_SELF));
    }
}
