package eu.europa.ec.eurostat.wihp.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class ApplicationPropertiesIT {

    @Autowired
    private ApplicationProperties unit;

    public static final String FILTERS_PACKAGE_NAME = "eu.europa.ec.eurostat.wihp.urlfilters";
    public static final String FILTER_PROPERTY_URL = "url";
    public static final String FILTER_PROPERTY_URLS = "urls";
    public static final String FILTER_PROPERTY_RESPONSE = "response";
    public static final String FILTER_PROPERTY_METADATA = "metadata";
    public static final String SELENIUM_ADDRESS = "http://host.docker.internal:4444";
    public static final String USER_AGENT_DETAILS = "user agent details";

    @Test
    public void shouldLoadFiltersPackage() {
        String result = unit.getFiltersPackage();

        assertNotNull(result);
        assertTrue(result.contains(FILTERS_PACKAGE_NAME));
    }

    @Test
    public void shouldLoadFiltersUrl() {
        String result = unit.getFiltersPropertyUrl();

        assertNotNull(result);
        assertTrue(result.contains(FILTER_PROPERTY_URL));
    }

    @Test
    public void shouldLoadFiltersUrls() {
        String result = unit.getFiltersPropertyUrls();

        assertNotNull(result);
        assertTrue(result.contains(FILTER_PROPERTY_URLS));
    }

    @Test
    public void shouldLoadFiltersResponse() {
        String result = unit.getFiltersPropertyResponse();

        assertNotNull(result);
        assertTrue(result.contains(FILTER_PROPERTY_RESPONSE));
    }

    @Test
    public void shouldLoadSeleniumAddress() {
        String result = unit.getSeleniumAddress();

        assertNotNull(result);
        assertTrue(result.contains(SELENIUM_ADDRESS));
    }

    @Test
    public void shouldLoadUserAgentDetails() {
        String result = unit.getUserAgentDetails();

        assertNotNull(result);
        assertTrue(result.contains(USER_AGENT_DETAILS));
    }
}
