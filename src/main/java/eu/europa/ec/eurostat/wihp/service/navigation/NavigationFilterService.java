package eu.europa.ec.eurostat.wihp.service.navigation;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.navigation.NavigationFilterConfiguration;
import eu.europa.ec.eurostat.wihp.domain.navigation.NavigationFilterResponse;
import eu.europa.ec.eurostat.wihp.exceptions.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.exceptions.NotFoundAlertException;
import eu.europa.ec.eurostat.wihp.navigationfilters.ProtocolResponse;
import eu.europa.ec.eurostat.wihp.navigationfilters.WIHPNavigationFilter;
import eu.europa.ec.eurostat.wihp.service.protocols.RemoteDriverFactory;
import eu.europa.ec.eurostat.wihp.service.url.UrlWIHPMetadata;
import eu.europa.ec.eurostat.wihp.utils.FilterUtils;
import eu.europa.ec.eurostat.wihp.utils.JsonUtils;
import eu.europa.ec.eurostat.wihp.utils.PaginationUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NavigationFilterService {

    private final NavigationFilterLoaderService navigationFilterLoaderService;

    private final ApplicationProperties applicationProperties;

    private final RemoteDriverFactory remoteDriverFactory;

    private final ObjectMapper mapper;

    public static final String ENTITY_NAME = "NavigationFilterService";
    private static final String ID_INVALID = "idInvalid";

    public NavigationFilterService(
        final NavigationFilterLoaderService navigationFilterLoaderService,
        final ApplicationProperties applicationProperties,
        final ObjectMapper mapper,
        final RemoteDriverFactory remoteDriverFactory
    ) {
        this.navigationFilterLoaderService = navigationFilterLoaderService;
        this.applicationProperties = applicationProperties;
        this.mapper = mapper;
        this.remoteDriverFactory = remoteDriverFactory;
    }

    public Page<NavigationFilterResponse> getAllNavigationFilterConfigurations(Pageable pageable) {
        NavigationFilterConfiguration navigationFilterConfiguration = this.navigationFilterLoaderService.getNavigationFilterConfiguration();
        List<NavigationFilterResponse> responses = navigationFilterConfiguration
            .getNavigationFilters()
            .stream()
            .map(NavigationFilterResponse::new)
            .collect(toUnmodifiableList());
        return PaginationUtils.createPage(responses, pageable);
    }

    public JsonNode getNavigationFilterConfigurationById(String id) {
        if (!FilterUtils.isPackageNameValid(id)) {
            throw new BadRequestAlertException("Filter ID is not valid", ENTITY_NAME, ID_INVALID);
        }
        return navigationFilterLoaderService
            .getFilterConfigurationById(id)
            .orElseThrow(() -> new NotFoundAlertException("NavigationFilter not found. ID=".concat(id), ENTITY_NAME, ID_INVALID));
    }

    public ProtocolResponse applyNavigationFilterConfigurationById(String id, JsonNode configuration) {
        validateJson(configuration);
        validateId(id);
        WIHPNavigationFilter navigationFilter = navigationFilterLoaderService.getNavigationFilterClassById(id);

        navigationFilter.configure(configuration);
        NavigationWIHPMetadata metadata = getMetadata(configuration);
        RemoteWebDriver driver = remoteDriverFactory.createRemoteWebDriver(convert(metadata));
        String url = JsonUtils.parseString(configuration, "url");
        driver.get(url);
        return navigationFilter.filter(driver, metadata);
    }

    protected void validateId(String id) {
        if (!FilterUtils.isPackageNameValid(id)) {
            throw new BadRequestAlertException("Filter ID is not valid", ENTITY_NAME, ID_INVALID);
        }
        if (!navigationFilterLoaderService.filterExists(id)) {
            throw new NotFoundAlertException("Filter ID " + id + " is not found.", ENTITY_NAME, ID_INVALID);
        }
    }

    protected void validateJson(JsonNode configuration) {
        if (configuration.isNull() || configuration.isEmpty()) {
            throw new BadRequestAlertException("Configuration is empty", ENTITY_NAME, ID_INVALID);
        }

        String url = JsonUtils.parseString(configuration, "url");
        if (StringUtils.isEmpty(url)) {
            throw new BadRequestAlertException("Input parameters are not valid. Field URL must be set", ENTITY_NAME, ID_INVALID);
        }

        JsonNode stepsNode = configuration.get("steps");
        if (stepsNode.isNull() || stepsNode.isEmpty()) {
            throw new BadRequestAlertException("Input parameters are not valid. Field steps must be set", ENTITY_NAME, ID_INVALID);
        }
    }

    private NavigationWIHPMetadata getMetadata(JsonNode configuration) {
        return Optional
            .ofNullable(configuration.get(applicationProperties.getFiltersPropertyMetadata()))
            .map(jsonNode -> mapper.convertValue(jsonNode, new TypeReference<Map<String, String[]>>() {}))
            .map(NavigationWIHPMetadata::new)
            .orElse(new NavigationWIHPMetadata(new HashMap<>()));
    }

    private UrlWIHPMetadata convert(NavigationWIHPMetadata navigationWIHPMetadata) {
        UrlWIHPMetadata urlWIHPMetadata = new UrlWIHPMetadata();
        BeanUtils.copyProperties(navigationWIHPMetadata, urlWIHPMetadata);
        return urlWIHPMetadata;
    }
}
