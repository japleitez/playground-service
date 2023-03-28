package eu.europa.ec.eurostat.wihp.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.service.protocols.RemoteDriverFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
class NavigationFilterResourceIT {

    private static final String API_URL = "/api/navigation-filters";
    private static final String API_URL_PAGINATED = API_URL + "?page=0&size=1000";
    private static final String API_URL_ID = API_URL + "/{id}";

    private static final String API_URL_VALIDATE = API_URL + "/validate";

    private static final String FILTER_ID = "eu.europa.ec.eurostat.wihp.navigationfilters.action.ActionNavigationFilter";
    private static final String FILTER_NAME_DEFAULT = "Basic Selenium Filter";
    private static final String FILTER_TRANSLATION_KEY = "eu.europa.ec.eurostat.wihp.navigationfilters.action.ActionNavigationFilter";

    private static final String TEST_FILTER_CONFIG_JSON = "ActionNavigationFilterConfig.json";
    private static final String VALIDATION_VALID_TEST_NAVIGATION_CONFIG =
        "validation_simple/valid_test_navigation_config.json";

    private static final String VALIDATION_INVALID_TEST_NAVIGATION_CONFIG =
        "validation_simple/invalid_test_navigation_config.json";


    @SpyBean
    private RemoteDriverFactory remoteDriverFactory;

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private RemoteWebDriver driver;

    @Test
    @DisplayName("GET navigation filters should return OK with expected List of values")
    void getNavigationFilters() throws Exception {
        restMockMvc
            .perform(get(API_URL, API_URL_PAGINATED))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(FILTER_ID)))
            .andExpect(jsonPath("$.[*].name.default").value(hasItem(FILTER_NAME_DEFAULT)))
            .andExpect(jsonPath("$.[*].name.translationKey").value(hasItem(FILTER_TRANSLATION_KEY)));
    }

    @Test
    @DisplayName("GET navigation filter should return expected filter by id")
    void getNavigationFilter() throws Exception {
        restMockMvc
            .perform(get(API_URL_ID, FILTER_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(FILTER_ID))
            .andExpect(jsonPath("$.name.default").value(FILTER_NAME_DEFAULT))
            .andExpect(jsonPath("$.name.translationKey").value(FILTER_TRANSLATION_KEY));
    }

    @Test
    @DisplayName("POST should apply a filter configuration for a given filter id")
    void applyNavigationFilter() throws Exception {
        JsonNode configuration = TestUtil.getJsonNodeFromResource(TEST_FILTER_CONFIG_JSON);

        String content = "<h1>Filter Success</h1>";
        WebElement button = Mockito.mock(WebElement.class);
        when(driver.getPageSource()).thenReturn(content);
        when(driver.findElement(By.xpath("//button"))).thenReturn(button);
        doReturn(driver).when(remoteDriverFactory).createRemoteWebDriver(any());

        restMockMvc
            .perform(post(API_URL_ID, FILTER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(configuration.toString()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @DisplayName("POST should apply return Status 400 for empty Configuration")
    void applyNavigationFilterEmptyConfiguration() throws Exception {
        JsonNode configuration = objectMapper.createObjectNode();

        restMockMvc
            .perform(post(API_URL_ID, FILTER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(configuration.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("When valid Configuration is provided no error should be found.")
    void whenValidConfig_then_noErrorFound() throws Exception {
        String config = TestUtil.readStringFromResource(VALIDATION_VALID_TEST_NAVIGATION_CONFIG);
        restMockMvc
            .perform(post(API_URL_VALIDATE, FILTER_ID).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(config))
            .andExpect(status().is(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.configurations[0].id").value(FILTER_ID))
            .andExpect(jsonPath("$.configurations[0].validationErrors").isArray())
            .andExpect(jsonPath("$.configurations[0].validationErrors").isEmpty());
    }

    @Test
    @DisplayName("When invalid Configuration is provided an error should be found.")
    void whenInvalidConfig_then_ErrorFound() throws Exception {
        String config = TestUtil.readStringFromResource(VALIDATION_INVALID_TEST_NAVIGATION_CONFIG);
        restMockMvc
            .perform(post(API_URL_VALIDATE, FILTER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(config))
            .andExpect(status().is(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.configurations[0].id").value(FILTER_ID))
            .andExpect(jsonPath("$.configurations[0].validationErrors").isArray())
            .andExpect(jsonPath("$.configurations[0].validationErrors").isNotEmpty());
    }
}
