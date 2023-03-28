package eu.europa.ec.eurostat.wihp.service.url;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.url.UrlFilterConfiguration;
import eu.europa.ec.eurostat.wihp.exceptions.NotFoundAlertException;
import eu.europa.ec.eurostat.wihp.urlfilters.examples.TestUrlFilter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class UrlFilterConfigurationIT {

    public static final String BASIC_FILTER_CLASS_NAME = "eu.europa.ec.eurostat.wihp.urlfilters.stormcrawler.BasicUrlFilter";
    private final String packageNameWrong = "eu$europa@ec.eur1stat.wihp";
    private final String NonExistingFilterPath = "eu.europa.ec.eurostat.wihp.urlfilters.dummy.dummy.VictorHugo";

    @Autowired
    private ApplicationProperties applicationProperties;

    UrlFilterLoaderService unit;

    @BeforeEach
    public void setUp() {
        unit = new UrlFilterLoaderService(applicationProperties, new ObjectMapper());
    }

    @Test
    void shouldGetDummyFilterConfigurations() {
        UrlFilterConfiguration configuration = unit.getFilterConfigurations();
        Assertions.assertNotNull(configuration);
        Assertions.assertTrue(configuration.getUrls().size() > 1);
        Optional<JsonNode> dummyFilter = configuration.getConfigurationById(BASIC_FILTER_CLASS_NAME);
        Assertions.assertFalse(dummyFilter.isEmpty());
    }

    @Test
    void whenIdIsCorrect_thenGetFilterClassesReturnsNotNull() {
        Set<Class> filters = new HashSet<>();
        filters.add(TestUrlFilter.class);

        Assertions.assertNotNull(unit.getFilterClassById(BASIC_FILTER_CLASS_NAME));
    }

    @Test
    void whenIdIsNotCorrect_thenGetFilterClassesThrows() {
        Set<Class> filters = new HashSet<>();
        filters.add(TestUrlFilter.class);
        Assertions.assertThrows(NotFoundAlertException.class, () -> unit.getFilterClassById(packageNameWrong));
    }

    @Test
    void whenIdIsInCorrect_thenApplyUrlFilterConfigurationById_throws() {
        Assertions.assertThrows(NotFoundAlertException.class, () -> unit.getFilterClassById(NonExistingFilterPath));
    }
}
