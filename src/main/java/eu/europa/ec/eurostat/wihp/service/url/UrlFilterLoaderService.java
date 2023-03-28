package eu.europa.ec.eurostat.wihp.service.url;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.url.*;
import eu.europa.ec.eurostat.wihp.exceptions.UnprocessableEntityException;
import eu.europa.ec.eurostat.wihp.service.FilterClassHelper;
import eu.europa.ec.eurostat.wihp.service.FilterConfigService;
import eu.europa.ec.eurostat.wihp.service.FilterType;
import eu.europa.ec.eurostat.wihp.urlfilters.WihpUrlFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UrlFilterLoaderService implements FilterConfigService {

    private final ObjectMapper mapper;
    private final ApplicationProperties applicationProperties;
    private final UrlFilterConfiguration filterConfiguration;
    private final FilterClassHelper<WihpUrlFilter> filterClassHelper = new FilterClassHelper<>(WihpUrlFilter.class);

    public UrlFilterLoaderService(final ApplicationProperties applicationProperties, ObjectMapper mapper) {
        this.applicationProperties = applicationProperties;
        this.mapper = mapper;
        this.filterConfiguration = loadFilterConfiguration();
    }

    private UrlFilterConfiguration loadFilterConfiguration() {
        String packageName = applicationProperties.getFiltersPackage();
        Set<UrlFilter> urls = filterClassHelper
            .instantiateAllFilters(packageName)
            .stream()
            .map(this::fromClassToResourceStream)
            .map(this::fromStreamToJsonNode)
            .map(this::fromJsonNodeToUrlFilter)
            .collect(Collectors.toSet());
        return new UrlFilterConfiguration(urls);
    }

    private InputStream fromClassToResourceStream(WihpUrlFilter filter) {
        return filter.getClass().getResourceAsStream(filter.getClass().getSimpleName().concat(".json"));
    }

    private JsonNode fromStreamToJsonNode(InputStream inputStream) {
        try {
            return mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new UnprocessableEntityException(ERROR_MAPPING_CONFIGURATION.concat(e.getMessage()), "", SERVER_ERROR);
        }
    }

    public UrlFilterConfiguration getFilterConfigurations() {
        return filterConfiguration;
    }

    @Override
    public FilterType getType() {
        return FilterType.URL_FILTER;
    }

    @Override
    public Optional<JsonNode> getFilterConfigurationById(String id) {
        return filterConfiguration.getConfigurationById(id);
    }

    public WihpUrlFilter getFilterClassById(String id) {
        return filterClassHelper.instantiateFilterByName(id);
    }

    protected UrlFilter fromJsonNodeToUrlFilter(JsonNode configuration) {
        try {
            UrlFilter filterConf = mapper.treeToValue(configuration, UrlFilter.class);
            return filterConf.setConfiguration(configuration);
        } catch (JsonProcessingException e) {
            throw new UnprocessableEntityException(ERROR_MAPPING_CONFIGURATION.concat(e.getMessage()), "", SERVER_ERROR);
        }
    }
}
