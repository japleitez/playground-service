package eu.europa.ec.eurostat.wihp.service.parse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.parse.ParseFilter;
import eu.europa.ec.eurostat.wihp.domain.parse.ParseFilterConfiguration;
import eu.europa.ec.eurostat.wihp.exceptions.UnprocessableEntityException;
import eu.europa.ec.eurostat.wihp.parsefilters.WihpParseFilter;
import eu.europa.ec.eurostat.wihp.service.FilterClassHelper;
import eu.europa.ec.eurostat.wihp.service.FilterConfigService;
import eu.europa.ec.eurostat.wihp.service.FilterType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ParseFilterLoaderService implements FilterConfigService {

    private final ObjectMapper mapper;
    private final ApplicationProperties applicationProperties;
    private final ParseFilterConfiguration parseFilterConfiguration;
    private final FilterClassHelper<WihpParseFilter> filterClassHelper = new FilterClassHelper<>(WihpParseFilter.class);

    public ParseFilterLoaderService(final ApplicationProperties applicationProperties, final ObjectMapper mapper) {
        this.applicationProperties = applicationProperties;
        this.mapper = mapper;
        this.parseFilterConfiguration = loadConfiguration();
    }

    public ParseFilterConfiguration getParseFilterConfiguration() {
        return this.parseFilterConfiguration;
    }

    @Override
    public FilterType getType() {
        return FilterType.PARSE_FILTER;
    }

    @Override
    public Optional<JsonNode> getFilterConfigurationById(String id) {
        return this.parseFilterConfiguration.getConfigurationById(id);
    }

    public WihpParseFilter getParseFilterClassById(String id) {
        return filterClassHelper.instantiateFilterByName(id);
    }

    private ParseFilterConfiguration loadConfiguration() {
        String packageName = applicationProperties.getParseFilterPackage();
        Set<ParseFilter> parseFilters = filterClassHelper.instantiateAllFilters(packageName)
            .stream()
            .map(this::fromClassToResourceStream)
            .map(this::fromStreamToJsonNode)
            .map(this::fromJsonNodeToUrlFilter)
            .collect(Collectors.toSet());
        return new ParseFilterConfiguration(parseFilters);
    }

    private InputStream fromClassToResourceStream(WihpParseFilter filter) {
        return filter.getClass().getResourceAsStream(filter.getClass().getSimpleName().concat(".json"));
    }

    private JsonNode fromStreamToJsonNode(InputStream inputStream) {
        try {
            return this.mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new UnprocessableEntityException(ERROR_MAPPING_CONFIGURATION.concat(e.getMessage()), "", SERVER_ERROR);
        }
    }

    protected ParseFilter fromJsonNodeToUrlFilter(JsonNode configuration) {
        try {
            ParseFilter filterConf = this.mapper.treeToValue(configuration, ParseFilter.class);
            return filterConf.setConfiguration(configuration);
        } catch (JsonProcessingException e) {
            throw new UnprocessableEntityException(ERROR_MAPPING_CONFIGURATION.concat(e.getMessage()), "", SERVER_ERROR);
        }
    }

}
