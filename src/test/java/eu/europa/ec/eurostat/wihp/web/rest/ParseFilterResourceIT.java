package eu.europa.ec.eurostat.wihp.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link UrlFilterResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ParseFilterResourceIT {

    private static final String API_URL = "/api/parse-filters";
    private static final String API_URL_PAGINATED = API_URL + "?page=0&size=1000";
    private static final String API_URL_VALIDATE = API_URL + "/validate";
    private static final String API_URL_ID = API_URL + "/{id}";
    private static final String FILTER_ID = "eu.europa.ec.eurostat.wihp.parsefilters.examples.TestParseFilter";
    private static final String FILTER_NAME_DEFAULT = "Test Filter";
    private static final String FILTER_TRANSLATION_KEY = "eu.europa.ec.eurostat.wihp.parsefilters.TestParseFilter";
    private static final String FILTER_CONFIG =
        "{\"id\":\"eu.europa.ec.eurostat.wihp.parsefilters.examples.TestParseFilter\",\"protocol\":\"STATIC\",\"property\":\"page.title\",\"url\":\"https://www.google.com/\"}";

    @Autowired
    private MockMvc restMockMvc;

    @Test
    void getParseFilters() throws Exception {
        restMockMvc
            .perform(get(API_URL, API_URL_PAGINATED))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(FILTER_ID)))
            .andExpect(jsonPath("$.[*].name.default").value(hasItem(FILTER_NAME_DEFAULT)))
            .andExpect(jsonPath("$.[*].name.translationKey").value(hasItem(FILTER_TRANSLATION_KEY)));
    }

    @Test
    void getParseFilter() throws Exception {
        restMockMvc
            .perform(get(API_URL_ID, FILTER_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(FILTER_ID))
            .andExpect(jsonPath("$.name.default").value(FILTER_NAME_DEFAULT))
            .andExpect(jsonPath("$.name.translationKey").value(FILTER_TRANSLATION_KEY));
    }

    @Test
    void applyParseFilter() throws Exception {
        restMockMvc
            .perform(post(API_URL_ID, FILTER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(FILTER_CONFIG))
            .andExpect(status().is(201))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$['page.title']").exists());
    }

    @Test
    void whenValidConfig_then_noErrorFound() throws Exception {
        String config = TestUtil.readStringFromResource("validation_simple/valid_test_parse_config.json");
        restMockMvc
            .perform(post(API_URL_VALIDATE, FILTER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(config))
            .andExpect(status().is(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.configurations[0].id").value("eu.europa.ec.eurostat.wihp.parsefilters.examples.TestParseFilter"))
            .andExpect(jsonPath("$.configurations[0].validationErrors").isArray())
            .andExpect(jsonPath("$.configurations[0].validationErrors").isEmpty());
    }

    @Test
    void whenInvalidConfig_then_ErrorFound() throws Exception {
        String config = TestUtil.readStringFromResource("validation_simple/invalid_test_parse_config.json");
        restMockMvc
            .perform(post(API_URL_VALIDATE, FILTER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(config))
            .andExpect(status().is(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.configurations[0].id").value("eu.europa.ec.eurostat.wihp.parsefilters.examples.TestParseFilter"))
            .andExpect(jsonPath("$.configurations[0].validationErrors").isArray())
            .andExpect(jsonPath("$.configurations[0].validationErrors").isNotEmpty());
    }
}
