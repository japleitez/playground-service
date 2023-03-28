package eu.europa.ec.eurostat.wihp.service.url;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.config.ApplicationPropertiesIT;
import eu.europa.ec.eurostat.wihp.domain.url.UrlFilter;
import eu.europa.ec.eurostat.wihp.domain.url.UrlFilterConfiguration;
import eu.europa.ec.eurostat.wihp.domain.url.UrlFilterResponse;
import eu.europa.ec.eurostat.wihp.exceptions.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.exceptions.NotFoundAlertException;
import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;
import eu.europa.ec.eurostat.wihp.urlfilters.stormcrawler.BasicUrlFilter;
import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class UrlFilterServiceTest {

    public static final String NODE_STRING =
        "{\"param1\": \"test\",\"param2\": 5,\"param3\": 5.35,\"param4\": true,\"param5\": [\"a\",\"b\"],\"param6\": [2,3],\"param7\": [3.5,4.8],\"param8\": [true,false],\"urls\": [\"https://www.test.com\",\"https://www.google.com\",\"invalid\"]}";
    private final String packageNameWrong = "eu$europa@ec.eur1stat.wihp";
    private final String packageNameCorrect = "eu.europa.ec.eurostat.wihp.urlfilters";
    private final String BasicFilterPath = "eu.europa.ec.eurostat.wihp.urlfilters.stormcrawler.BasicUrlFilter";
    private final String NonExistingFilterPath = "eu.europa.ec.eurostat.wihp.urlfilters.dummy.dummy.VictorHugo";

    private static final String FILTER_CONFIGURATION_FILE = "BasicUrlFilter.json";

    @Mock
    UrlFilterLoaderService filterConfigurationService;

    @Mock
    UrlFilterServiceValidation urlFilterValidation;

    @Mock
    ApplicationProperties applicationProperties;

    UrlFilterService unit;

    static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        unit = new UrlFilterService(filterConfigurationService, urlFilterValidation, applicationProperties, MAPPER);
    }

    @Test
    void shouldCheckPackageName() {
        Assertions.assertTrue(unit.isPackageNameValid(packageNameCorrect));
        Assertions.assertFalse(unit.isPackageNameValid(packageNameWrong));
    }

    @Test
    void shouldCheckInput() throws JsonProcessingException {
        JsonNode validNode = MAPPER.readTree(NODE_STRING);
        JsonNode InValidNode = MAPPER.readTree("");

        Assertions.assertTrue(unit.isInputNotValid(packageNameWrong, validNode));
        Assertions.assertTrue(unit.isInputNotValid("", validNode));
        Assertions.assertTrue(unit.isInputNotValid(packageNameCorrect, InValidNode));
        Assertions.assertTrue(unit.isInputNotValid(packageNameCorrect, null));

        Assertions.assertFalse(unit.isInputNotValid(packageNameCorrect, validNode));
    }

    @Test
    void whenNodeNotCorrect_thenApplyFilterReturnsEmptyObjectNode() throws JsonProcessingException {
        JsonNode validNode = MAPPER.readTree(NODE_STRING);
        WIHPMetadata md = new UrlWIHPMetadata();
        Assertions.assertFalse(
            unit
                .applyFilter(validNode, md, validNode.get(ApplicationPropertiesIT.FILTER_PROPERTY_URLS), new BasicUrlFilter())
                .getUrls()
                .isEmpty()
        );
        Assertions.assertTrue(unit.applyFilter(validNode, md, MAPPER.createObjectNode(), new BasicUrlFilter()).getUrls().isEmpty());
    }

    @Test
    void whenIdIsCorrect_thenApplyUrlFilterConfigurationByIdReturnsResponse() throws JsonProcessingException {
        JsonNode validNode = MAPPER.readTree(NODE_STRING);

        Set<Class> filters = new HashSet<>();
        filters.add(BasicUrlFilter.class);

        Mockito.when(applicationProperties.getFiltersPropertyUrls()).thenReturn(ApplicationPropertiesIT.FILTER_PROPERTY_URLS);
        Mockito.when(applicationProperties.getFiltersPropertyMetadata()).thenReturn(ApplicationPropertiesIT.FILTER_PROPERTY_METADATA);
        Mockito.when(filterConfigurationService.getFilterClassById(BasicFilterPath)).thenReturn(Mockito.mock(BasicUrlFilter.class));
        Assertions.assertNotNull(unit.applyUrlFilterConfigurationById(BasicFilterPath, validNode));
    }

    @Test
    void whenIdIsNotCorrect_thenApplyUrlFilterConfigurationByIdThrows() throws JsonProcessingException {
        JsonNode validNode = MAPPER.readTree(NODE_STRING);

        Set<Class> filters = new HashSet<>();
        filters.add(BasicUrlFilter.class);
        Assertions.assertThrows(BadRequestAlertException.class, () -> unit.applyUrlFilterConfigurationById(packageNameWrong, validNode));
    }

    @Test
    void whenIdIsCorrect_thenGetUrlFilterConfigurationByIdReturnsNonNullObject() throws IOException {
        JsonNode CONFIGURATION = TestUtil.getJsonNodeFromResource(FILTER_CONFIGURATION_FILE);

        Mockito.when(filterConfigurationService.getFilterConfigurationById(BasicFilterPath)).thenReturn(Optional.of(CONFIGURATION));

        Assertions.assertNotNull(unit.getUrlFilterConfigurationById(BasicFilterPath));
    }

    @Test
    void whenIdIsNotCorrect_thenGetUrlFilterConfigurationByIdThrowsBadRequest() {
        Assertions.assertThrows(BadRequestAlertException.class, () -> unit.getUrlFilterConfigurationById(packageNameWrong));
        Assertions.assertThrows(BadRequestAlertException.class, () -> unit.getUrlFilterConfigurationById(""));
        Assertions.assertThrows(BadRequestAlertException.class, () -> unit.getUrlFilterConfigurationById(null));
    }

    @Test
    void whenIdIsNotExisting_thenGetUrlFilterConfigurationByIdThrowsNotFound() {
        Assertions.assertThrows(NotFoundAlertException.class, () -> unit.getUrlFilterConfigurationById(NonExistingFilterPath));
    }

    @Test
    void whenPageable_thenGetAllUrlFilterConfigurationReturnsNotEmptyList() throws IOException {
        UrlFilterConfiguration FILTERS_CONFIGURATION = getFilterConfiguration();
        Mockito.when(filterConfigurationService.getFilterConfigurations()).thenReturn(FILTERS_CONFIGURATION);

        Assertions.assertFalse(unit.getAllUrlFilterConfiguration(PageRequest.of(0, 20)).isEmpty());
        Assertions.assertEquals(20, unit.getAllUrlFilterConfiguration(PageRequest.of(0, 20)).getSize());
        Assertions.assertEquals(1, unit.getAllUrlFilterConfiguration(PageRequest.of(0, 20)).getTotalElements());
        Assertions.assertTrue(unit.getAllUrlFilterConfiguration(PageRequest.of(33, 20)).isEmpty());
        Assertions.assertTrue(unit.getAllUrlFilterConfiguration(PageRequest.of(1, 1)).isEmpty());
    }

    private UrlFilterConfiguration getFilterConfiguration() throws IOException {
        JsonNode configuration = TestUtil.getJsonNodeFromResource(FILTER_CONFIGURATION_FILE);

        Map<String, JsonNode> filters = new HashMap<>();
        filters.put(BasicFilterPath, configuration);

        Set<UrlFilter> urls = new HashSet<>();
        for (JsonNode jn : filters.values()) {
            UrlFilter f = MAPPER.readValue(jn.toPrettyString(), UrlFilter.class);
            f.setConfiguration(jn);
            urls.add(f);
        }
        return new UrlFilterConfiguration(urls);
    }
}
