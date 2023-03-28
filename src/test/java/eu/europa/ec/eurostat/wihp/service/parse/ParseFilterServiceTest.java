package eu.europa.ec.eurostat.wihp.service.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.FilterName;
import eu.europa.ec.eurostat.wihp.domain.parse.ParseFilter;
import eu.europa.ec.eurostat.wihp.domain.parse.ParseFilterConfiguration;
import eu.europa.ec.eurostat.wihp.exceptions.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.parsefilters.examples.TestParseFilter;
import eu.europa.ec.eurostat.wihp.service.protocols.Protocol;
import eu.europa.ec.eurostat.wihp.service.protocols.ProtocolFactory;
import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
class ParseFilterServiceTest {

    private ParseFilterService parseFilterService;

    @Mock
    ParseFilterLoaderService parseFilterLoaderService;

    @Autowired
    ProtocolFactory protocolFactory;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TEST_PARSE_FILTER_ID = "eu.europa.ec.eurostat.wihp.parsefilters.examples.TestParseFilter";
    private static final String TEST_PARSE_FILTER_NAME_DEFAULT = "Test Filter";
    private static final String TEST_PARSE_FILTER_NAME_TRANSLATION = "eu.europa.ec.eurostat.wihp.parsefilters.TestParseFilter";
    private static final String TEST_PARSE_FILTER_JSON = "TestParseFilter.json";
    private static final String TEST_PARSE_FILTER_CONFIG_JSON = "TestParseFilterConfig.json";
    private static final String TEST_PARSE_FILTER_CONFIG_INVALID_JSON = "TestParseFilterConfigInvalid.json";

    @BeforeEach
    public void setup() {
        parseFilterService = new ParseFilterService(parseFilterLoaderService, protocolFactory);
    }

    @Test
    public void validConfiguration() throws IOException {
        JsonNode node = TestUtil.getJsonNodeFromResource(TEST_PARSE_FILTER_JSON);
        Mockito.when(parseFilterLoaderService.getFilterConfigurationById(TEST_PARSE_FILTER_ID)).thenReturn(Optional.of(node));
        JsonNode result = parseFilterService.getParseFilterConfigurationById(TEST_PARSE_FILTER_ID);
        assertNotNull(result);
        assertEquals(TEST_PARSE_FILTER_ID, result.get("id").textValue());
    }

    @Test
    public void validConfigUrl() throws IOException {
        JsonNode node = TestUtil.getJsonNodeFromResource(TEST_PARSE_FILTER_CONFIG_JSON);
        String url = parseFilterService.resolveUrl(node);
        assertNotNull(url);
        assertEquals("https://www.google.com/", url);
    }

    @Test
    public void emptyConfigUrl() throws IOException {
        JsonNode node = MAPPER.readTree("{}");
        Assertions.assertThrows(BadRequestAlertException.class, () -> parseFilterService.resolveUrl(node));
    }

    @Test
    public void validConfigProtocol() throws IOException {
        JsonNode node = TestUtil.getJsonNodeFromResource(TEST_PARSE_FILTER_CONFIG_JSON);
        Protocol protocol = parseFilterService.resolveProtocol(node);
        assertNotNull(protocol);
        assertEquals(Protocol.STATIC, protocol);
    }

    @Test
    public void invalidConfigProtocol() throws IOException {
        JsonNode node = TestUtil.getJsonNodeFromResource(TEST_PARSE_FILTER_CONFIG_INVALID_JSON);
        Assertions.assertThrows(IllegalArgumentException.class, () -> parseFilterService.resolveProtocol(node));
    }

    @Test
    public void emptyConfigProtocol() throws IOException {
        JsonNode node = MAPPER.readTree("{}");
        Assertions.assertThrows(BadRequestAlertException.class, () -> parseFilterService.resolveProtocol(node));
    }

    @Test
    public void applyParseFilterConfigurationById() throws IOException {
        JsonNode configuration = TestUtil.getJsonNodeFromResource(TEST_PARSE_FILTER_CONFIG_JSON);
        Mockito.when(parseFilterLoaderService.getParseFilterClassById(TEST_PARSE_FILTER_ID)).thenReturn(new TestParseFilter());
        Map<String, String[]> result = parseFilterService.applyParseFilterConfigurationById(TEST_PARSE_FILTER_ID, configuration);
        assertNotNull(result);
        assertNotNull(result.get("page.title"));
    }

    @Test
    public void getAllParseFilterConfiguration() {
        ParseFilterConfiguration parseFilterConfiguration = createParseFilterConfiguration();
        Mockito.when(parseFilterLoaderService.getParseFilterConfiguration()).thenReturn(parseFilterConfiguration);

        assertFalse(parseFilterService.getAllParseFilterConfiguration(PageRequest.of(0, 20)).isEmpty());
        assertEquals(20, parseFilterService.getAllParseFilterConfiguration(PageRequest.of(0, 20)).getSize());
        assertEquals(1, parseFilterService.getAllParseFilterConfiguration(PageRequest.of(0, 20)).getTotalElements());
        assertTrue(parseFilterService.getAllParseFilterConfiguration(PageRequest.of(30, 20)).isEmpty());
        assertTrue(parseFilterService.getAllParseFilterConfiguration(PageRequest.of(1, 1)).isEmpty());
    }

    @Test
    public void invalidGetParseFilterConfigurationById() {
        String wrongId = "eu/europa/ec/eurostat/wihp/parsefilters/examples/TestParseFilter";
        Assertions.assertThrows(BadRequestAlertException.class, () -> parseFilterService.getParseFilterConfigurationById(wrongId));
    }

    private ParseFilterConfiguration createParseFilterConfiguration() {
        Set<ParseFilter> parseFilters = new HashSet<>();
        FilterName filterName = new FilterName();
        filterName.setDefaultName(TEST_PARSE_FILTER_NAME_DEFAULT);
        filterName.setTranslationKey(TEST_PARSE_FILTER_NAME_TRANSLATION);
        ParseFilter parseFilter = new ParseFilter();
        parseFilter.setId(TEST_PARSE_FILTER_ID);
        parseFilter.setName(filterName);
        parseFilters.add(parseFilter);
        return new ParseFilterConfiguration(parseFilters);
    }
}
