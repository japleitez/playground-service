package eu.europa.ec.eurostat.wihp.web.rest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
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
class XPathFilterResourceIT {

    private static final String API_URL = "/api/parse-filters";
    private static final String API_URL_ID = API_URL + "/{id}";
    private static final String FILTER_ID = "eu.europa.ec.eurostat.wihp.parsefilters.stormcrawler.xpathfilter.XPathFilter";
    private static final String FILTER_NAME_DEFAULT = "XPath Filter";
    private static final String FILTER_TRANSLATION_KEY = "eu.europa.ec.eurostat.wihp.parsefilters.stormcrawler.xpathfilter.XPathFilter";

    @Autowired
    private MockMvc restMockMvc;

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
        String content = TestUtil.readStringFromResource("xpathfilter/XPathFilterConfig.json");
        restMockMvc
            .perform(post(API_URL_ID, FILTER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(content))
            .andExpect(status().is(201))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.title").isNotEmpty());
    }
}
