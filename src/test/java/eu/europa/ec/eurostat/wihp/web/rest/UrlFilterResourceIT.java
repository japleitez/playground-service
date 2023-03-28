package eu.europa.ec.eurostat.wihp.web.rest;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link UrlFilterResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UrlFilterResourceIT {

    private static final String ENTITY_API_URL = "/api/url-filters";
    private static final String ENTITY_API_URL_PAGINATED = ENTITY_API_URL + "?page=0&size=1000";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    public static final String BASIC_FILTER_NAME = "Basic Url Filter";
    public static final String BASIC_FILTER_ID = "eu.europa.ec.eurostat.wihp.urlfilters.stormcrawler.BasicUrlFilter";
    public static final String POST_CONFIGURATION = "{\"param1\": \"test\",\"param2\": 5,\"param3\": 5.35,\"param4\": true,\"param5\": [\"a\",\"b\"],\"param6\": [2,3],\"param7\": [3.5,4.8],\"param8\": [true,false],\"urls\": [\"https://www.test.com\",\"https://www.google.com\",\"invalid\"]}";
    private static final String EXPECTED_DUMMY_URL = "https://www.test.com";

    @Autowired
    private MockMvc restCrawlerMockMvc;

    @Test
    void getUrlFilters() throws Exception {
        restCrawlerMockMvc
            .perform(get(ENTITY_API_URL,ENTITY_API_URL_PAGINATED))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(BASIC_FILTER_ID)))
            .andExpect(jsonPath("$.[*].name.default").value(hasItem(BASIC_FILTER_NAME)))
            .andExpect(jsonPath("$.[*].name.translationKey").value(hasItem(BASIC_FILTER_ID)));
    }

    @Test
    void getUrlFilter() throws Exception {
        restCrawlerMockMvc
            .perform(get(ENTITY_API_URL_ID, BASIC_FILTER_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(BASIC_FILTER_ID))
            .andExpect(jsonPath("$.name.default").value(BASIC_FILTER_NAME))
            .andExpect(jsonPath("$.name.translationKey").value(BASIC_FILTER_ID));
    }

    @Test
    void applyUrlFilter() throws Exception {
        restCrawlerMockMvc
            .perform(post(ENTITY_API_URL_ID, BASIC_FILTER_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(POST_CONFIGURATION))
            .andExpect(status().is(201))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.urls.[*].url").value(hasItem(EXPECTED_DUMMY_URL)));
    }
}

