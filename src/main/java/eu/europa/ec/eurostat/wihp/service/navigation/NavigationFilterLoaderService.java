package eu.europa.ec.eurostat.wihp.service.navigation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.navigation.NavigationFilter;
import eu.europa.ec.eurostat.wihp.domain.navigation.NavigationFilterConfiguration;
import eu.europa.ec.eurostat.wihp.exceptions.UnprocessableEntityException;
import eu.europa.ec.eurostat.wihp.navigationfilters.WIHPNavigationFilter;
import eu.europa.ec.eurostat.wihp.service.FilterClassHelper;
import eu.europa.ec.eurostat.wihp.service.FilterConfigService;
import eu.europa.ec.eurostat.wihp.service.FilterType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NavigationFilterLoaderService implements FilterConfigService {

    private final ApplicationProperties applicationProperties;
    private final ObjectMapper mapper;

    private NavigationFilterConfiguration navigationFilterConfiguration;

    private final FilterClassHelper<WIHPNavigationFilter> filterClassHelper = new FilterClassHelper<>(WIHPNavigationFilter.class);

    public NavigationFilterLoaderService(final ApplicationProperties applicationProperties, final ObjectMapper mapper) {
        this.applicationProperties = applicationProperties;
        this.mapper = mapper;
        loadConfiguration();
    }

    public NavigationFilterConfiguration getNavigationFilterConfiguration() {
        return this.navigationFilterConfiguration;
    }

    @Override
    public FilterType getType() {
        return FilterType.NAVIGATION_FILTER;
    }

    @Override
    public Optional<JsonNode> getFilterConfigurationById(String id) {
        return this.navigationFilterConfiguration.getConfigurationById(id);
    }

    public WIHPNavigationFilter getNavigationFilterClassById(String id) {
        return filterClassHelper.instantiateFilterByName(id);
    }

    public boolean filterExists(String filterName) {
        return navigationFilterConfiguration
            .getNavigationFilters()
            .stream()
            .map(NavigationFilter::getId)
            .anyMatch(n -> n.equals(filterName));
    }

    public void loadConfiguration() {
        String packageName = applicationProperties.getNavigationFilterPackage();
        Set<NavigationFilter> navigationFilters = filterClassHelper
            .instantiateAllFilters(packageName)
            .stream()
            .map(this::fromClassToResourceStream)
            .map(this::fromStreamToJsonNode)
            .map(this::fromJsonNodeToUrlFilter)
            .collect(Collectors.toSet());
        this.navigationFilterConfiguration = new NavigationFilterConfiguration(navigationFilters);
    }

    private InputStream fromClassToResourceStream(WIHPNavigationFilter filter) {
        return filter.getClass().getResourceAsStream(filter.getClass().getSimpleName().concat(".json"));
    }

    private JsonNode fromStreamToJsonNode(InputStream inputStream) {
        try {
            return this.mapper.readTree(inputStream);
        } catch (IOException e) {
            throw new UnprocessableEntityException("Error mapping NavigationFilter configuration: " + e.getMessage(), "", SERVER_ERROR);
        }
    }

    protected NavigationFilter fromJsonNodeToUrlFilter(JsonNode configuration) {
        try {
            NavigationFilter filterConf = this.mapper.treeToValue(configuration, NavigationFilter.class);
            return filterConf.setConfiguration(configuration);
        } catch (JsonProcessingException e) {
            throw new UnprocessableEntityException("Error mapping NavigationFilter configuration: " + e.getMessage(), "", SERVER_ERROR);
        }
    }
}
