package eu.europa.ec.eurostat.wihp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.config.FilterLoaderConfiguration;
import eu.europa.ec.eurostat.wihp.service.url.UrlFilterLoaderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class SingletonLoaderTest {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ObjectMapper mapper;

    private FilterLoaderConfiguration unit;

    @Autowired
    UrlFilterLoaderService first;

    @Autowired
    UrlFilterLoaderService second;

    @BeforeEach
    public void setUp() {
        unit = new FilterLoaderConfiguration(applicationProperties, mapper);
    }

    @Test
    void shouldLoadTheConfigurationAsSingleton() {
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertEquals(second, first);
    }
}
