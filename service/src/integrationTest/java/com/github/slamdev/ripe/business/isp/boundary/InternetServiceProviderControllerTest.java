package com.github.slamdev.ripe.business.isp.boundary;

import com.github.slamdev.ripe.Application;
import com.github.slamdev.ripe.business.isp.entity.InternetServiceProvider;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@ActiveProfiles("InternetServiceProviderControllerTest")
public class InternetServiceProviderControllerTest {

    private static final Long ID = 1L;

    private static final InternetServiceProvider ISP = InternetServiceProvider.builder()
            .id(ID)
            .companyName("name")
            .email("some@email.com")
            .website(URI.create("http://example.com"))
            .build();

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private InternetServiceProviderRepository repository;

    private MockMvc mvc;

    private static String toJson(Object o) {
        return ofNullable(o).map(JSONObject::new).orElse(new JSONObject()).toString();
    }

    @Before
    public void setUp() {
        mvc = webAppContextSetup(context).build();
    }

    @Test
    public void should_return_correct_headers_on_success_creation() throws Exception {
        mvc.perform(post("/api/isp").contentType(APPLICATION_JSON_UTF8).content(toJson(ISP)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", endsWith("/api/isp/" + ID)))
                .andExpect(content().string(""));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void should_return_validation_errors_on_invalid_isp_creation() throws Exception {
        mvc.perform(post("/api/isp").contentType(APPLICATION_JSON_UTF8).content(toJson(null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$._embedded.vndErrors.[*].logref", hasItem("FORM_VALIDATION_ERROR")))
                .andExpect(jsonPath("$._embedded.vndErrors.[*].message", hasItem(startsWith("companyName"))))
                .andExpect(jsonPath("$._embedded.vndErrors.[*].message", hasItem(startsWith("website"))))
                .andExpect(jsonPath("$._embedded.vndErrors.[*].message", hasItem(startsWith("email"))));
    }

    @Test
    public void should_return_resource_by_id() throws Exception {
        when(repository.findOne(ID)).thenReturn(ISP);
        mvc.perform(get("/api/isp/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.companyName", is("name")))
                .andExpect(jsonPath("$.website", is("http://example.com")))
                .andExpect(jsonPath("$.email", is("some@email.com")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/isp/" + ID)));
    }

    @Test
    public void should_return_404_when_resource_not_found_on_acquire() throws Exception {
        when(repository.findOne(ID)).thenReturn(null);
        mvc.perform(get("/api/isp/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.logref", is("ERROR")))
                .andExpect(jsonPath("$.message", is("No data")));
    }

    @Test
    public void should_return_empty_response_when_resource_deleted() throws Exception {
        when(repository.exists(ID)).thenReturn(true);
        mvc.perform(delete("/api/isp/{id}", ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void should_return_404_when_resource_not_found_on_delete() throws Exception {
        when(repository.exists(ID)).thenReturn(false);
        mvc.perform(delete("/api/isp/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.logref", is("ERROR")))
                .andExpect(jsonPath("$.message", is("No data")));
    }

    @Test
    public void should_return_all_resources() throws Exception {
        when(repository.findAll()).thenReturn(singletonList(ISP));
        mvc.perform(get("/api/isp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/isp")))
                .andExpect(jsonPath("$._embedded.internetServiceProviders.[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.internetServiceProviders.[0].companyName", is("name")))
                .andExpect(jsonPath("$._embedded.internetServiceProviders.[0].website", is("http://example.com")))
                .andExpect(jsonPath("$._embedded.internetServiceProviders.[0].email", is("some@email.com")))
                .andExpect(jsonPath("$._embedded.internetServiceProviders.[0]._links.self.href", endsWith("/api/isp/" + ID)));
    }

    @Test
    public void should_return_empty_resources_if_nothing_found() throws Exception {
        when(repository.findAll()).thenReturn(emptyList());
        mvc.perform(get("/api/isp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/isp")));
    }

    @Configuration
    @Profile("InternetServiceProviderControllerTest")
    public static class Config {

        @Bean
        @Primary
        InternetServiceProviderRepository repository() {
            return mock(InternetServiceProviderRepository.class);
        }
    }
}
