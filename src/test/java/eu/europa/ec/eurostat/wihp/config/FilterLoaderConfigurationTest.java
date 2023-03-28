package eu.europa.ec.eurostat.wihp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.service.parse.ParseFilterLoaderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class FilterLoaderConfigurationTest {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ParseFilterLoaderService first;

    @Autowired
    private ParseFilterLoaderService second;

    private FilterLoaderConfiguration configuration;

    @BeforeEach
    public void setup() {
        configuration = new FilterLoaderConfiguration(applicationProperties, mapper);
    }

    @Test
    void shouldLoadTheConfigurationAsSingleton() {
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertEquals(second, first);
    }
}
