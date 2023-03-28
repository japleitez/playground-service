package eu.europa.ec.eurostat.wihp.service.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.FilterName;
import eu.europa.ec.eurostat.wihp.domain.navigation.NavigationFilter;
import eu.europa.ec.eurostat.wihp.domain.navigation.NavigationFilterConfiguration;
import eu.europa.ec.eurostat.wihp.exceptions.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.exceptions.NotFoundAlertException;
import eu.europa.ec.eurostat.wihp.navigationfilters.ProtocolResponse;
import eu.europa.ec.eurostat.wihp.navigationfilters.action.ActionNavigationFilter;
import eu.europa.ec.eurostat.wihp.service.protocols.RemoteDriverFactory;
import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
class NavigationFilterServiceTest {

    @SpyBean
    private NavigationFilterLoaderService navigationFilterLoaderService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ObjectMapper mapper;

    @SpyBean
    private RemoteDriverFactory remoteDriverFactory;

    @Mock
    private RemoteWebDriver driver;

    private NavigationFilterService navigationFilterService;

    private static final String TEST_FILTER_ID = "eu.europa.ec.eurostat.wihp.navigationfilters.action.ActionNavigationFilter";
    private static final String TEST_FILTER_NAME_DEFAULT = "Basic Selenium Filter";
    private static final String TEST_FILTER_NAME_TRANSLATION =
        "eu.europa.ec.eurostat.wihp.navigationfilters.action.ActionNavigationFilter";
    private static final String TEST_FILTER_JSON = "ActionNavigationFilter.json";

    private static final String TEST_FILTER_CONFIG_JSON = "ActionNavigationFilterConfig.json";

    @BeforeEach
    public void setup() {
        navigationFilterService =
            new NavigationFilterService(navigationFilterLoaderService, applicationProperties, mapper, remoteDriverFactory);
    }

    @Test
    void validConfiguration() throws IOException {
        JsonNode node = TestUtil.getJsonNodeFromResource(TEST_FILTER_JSON);

        doReturn(Optional.of(node)).when(navigationFilterLoaderService).getFilterConfigurationById(TEST_FILTER_ID);
        JsonNode result = navigationFilterService.getNavigationFilterConfigurationById(TEST_FILTER_ID);
        assertNotNull(result);
        assertEquals(TEST_FILTER_ID, result.get("id").textValue());
    }

    @Test
    @DisplayName("applyNavigationFilterConfigurationById returns expected results")
    void applyNavigationFilterConfigurationById() throws IOException {

        JsonNode configuration = TestUtil.getJsonNodeFromResource(TEST_FILTER_CONFIG_JSON);
        doReturn(new ActionNavigationFilter()).when(navigationFilterLoaderService).getNavigationFilterClassById(TEST_FILTER_ID);

        String content = "<h1>Filter Success</h1>";
        WebElement button = Mockito.mock(WebElement.class);
        when(driver.getPageSource()).thenReturn(content);
        when(driver.findElement(By.xpath("//button"))).thenReturn(button);
        doReturn(driver).when(remoteDriverFactory).createRemoteWebDriver(any());

        ProtocolResponse response = navigationFilterService.applyNavigationFilterConfigurationById(TEST_FILTER_ID, configuration);
        assertNotNull(response);
        assertEquals(content, new String(response.getContent()));
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        assertEquals(1, response.getSteps().size());
    }

    @Test
    @DisplayName("applyNavigationFilterConfigurationById with invalid ID throws exception")
    void applyNavigationFilterConfigurationByInvalidId() throws IOException {
        String wrongPackageFormat = "invalid-package-format";
        JsonNode configuration = TestUtil.getJsonNodeFromResource(TEST_FILTER_CONFIG_JSON);

        Assertions.assertThrows(
            BadRequestAlertException.class,
            () -> navigationFilterService.applyNavigationFilterConfigurationById(wrongPackageFormat, configuration)
        );

        String nonExistingId = "org.test.filters.MyFilter";

        Assertions.assertThrows(
            NotFoundAlertException.class,
            () -> navigationFilterService.applyNavigationFilterConfigurationById(nonExistingId, configuration)
        );
    }

    @Test
    @DisplayName("getAllNavigationFilterConfigurations returns expected content for different page sizes")
    void getAllNavigationFilterConfigurations() {
        NavigationFilterConfiguration navigationFilterConfiguration = createFilterConfiguration();

        doReturn(navigationFilterConfiguration).when(navigationFilterLoaderService).getNavigationFilterConfiguration();

        assertFalse(navigationFilterService.getAllNavigationFilterConfigurations(PageRequest.of(0, 20)).isEmpty());
        assertEquals(1, navigationFilterService.getAllNavigationFilterConfigurations(PageRequest.of(0, 20)).getTotalElements());
        assertTrue(navigationFilterService.getAllNavigationFilterConfigurations(PageRequest.of(30, 20)).isEmpty());
        assertTrue(navigationFilterService.getAllNavigationFilterConfigurations(PageRequest.of(1, 1)).isEmpty());
    }

    @Test
    @DisplayName("getNavigationFilterConfigurationById returns expected content for correct id")
    void getNavigationFilterConfigurationById() {
        Optional<JsonNode> optionalJsonNode = navigationFilterLoaderService
            .getNavigationFilterConfiguration()
            .getNavigationFilters()
            .stream()
            .filter(filter -> filter.getId().equals(TEST_FILTER_ID))
            .findFirst()
            .map(NavigationFilter::getConfiguration);

        doReturn(optionalJsonNode).when(navigationFilterLoaderService).getFilterConfigurationById(any());

        JsonNode result = navigationFilterService.getNavigationFilterConfigurationById(TEST_FILTER_ID);

        assertNotNull(result);
        assertEquals(TEST_FILTER_ID, result.get("id").textValue());
    }

    @Test
    @DisplayName("getNavigationFilterConfigurationById throws error for id that does not exist")
    void nonExistentIdGetNavigationFilterConfigurationById() {
        String wrongId = "wrong.package.wrong.id";
        Assertions.assertThrows(NotFoundAlertException.class, () -> navigationFilterService.getNavigationFilterConfigurationById(wrongId));
    }

    @Test
    @DisplayName("getNavigationFilterConfigurationById throws error invalid package name")
    void invalidPackageGetNavigationFilterConfigurationById() {
        String wrongId = "invalid-package-format";
        Assertions.assertThrows(
            BadRequestAlertException.class,
            () -> navigationFilterService.getNavigationFilterConfigurationById(wrongId)
        );
    }

    private NavigationFilterConfiguration createFilterConfiguration() {
        Set<NavigationFilter> filters = new HashSet<>();
        FilterName filterName = new FilterName();
        filterName.setDefaultName(TEST_FILTER_NAME_DEFAULT);
        filterName.setTranslationKey(TEST_FILTER_NAME_TRANSLATION);
        NavigationFilter navigationFilter = new NavigationFilter();
        navigationFilter.setId(TEST_FILTER_ID);
        navigationFilter.setName(filterName);
        filters.add(navigationFilter);
        return new NavigationFilterConfiguration(filters);
    }
}
