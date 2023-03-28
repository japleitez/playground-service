package eu.europa.ec.eurostat.wihp.service.url;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.url.UrlFilter;
import eu.europa.ec.eurostat.wihp.domain.url.UrlFilterResponse;
import eu.europa.ec.eurostat.wihp.domain.url.UrlFilterResult;
import eu.europa.ec.eurostat.wihp.domain.url.UrlResult;
import eu.europa.ec.eurostat.wihp.exceptions.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.exceptions.NotFoundAlertException;
import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;
import eu.europa.ec.eurostat.wihp.urlfilters.WihpUrlFilter;
import eu.europa.ec.eurostat.wihp.utils.StreamUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static eu.europa.ec.eurostat.wihp.utils.PaginationUtils.createPage;

@Service
public class UrlFilterService {

    public static final String ENTITY_NAME = "playGroundServiceUrlFilterResource";

    private final UrlFilterLoaderService filterConfigurationSingleton;
    private final UrlFilterServiceValidation urlFilterValidation;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper mapper;
    private static final URL NULL_SOURCE_URL = null;

    private static final String PACKAGE_PATTERN = "([a-zA-Z_$][a-zA-Z\\d_$]*+\\.)*+[a-zA-Z_$][a-zA-Z\\d_$]*+";
    private static final String ID_INVALID = "idInvalid";

    public UrlFilterService(
        final UrlFilterLoaderService filterConfigurationService,
        final UrlFilterServiceValidation urlFilterValidation,
        final ApplicationProperties applicationProperties,
        final ObjectMapper mapper
    ) {
        this.filterConfigurationSingleton = filterConfigurationService;
        this.urlFilterValidation = urlFilterValidation;
        this.applicationProperties = applicationProperties;
        this.mapper = mapper;
    }

    public Page<UrlFilterResponse> getAllUrlFilterConfiguration(Pageable pageable) {
        Set<UrlFilter> filters = filterConfigurationSingleton.getFilterConfigurations().getUrls();
        List<UrlFilterResponse> filtersResponse = filters.stream()
            .map(UrlFilterResponse::new)
            .collect(Collectors.toList());
        return createPage(filtersResponse, pageable);

    }

    public JsonNode getUrlFilterConfigurationById(String id) {
        if (Strings.isBlank(id) || !isPackageNameValid(id)) {
            throw new BadRequestAlertException("Input parameters are not valid", ENTITY_NAME, ID_INVALID);
        }

        return filterConfigurationSingleton
            .getFilterConfigurationById(id)
            .orElseThrow(() -> new NotFoundAlertException("Id not found: ".concat(id), UrlFilterService.ENTITY_NAME, ID_INVALID));
    }

    public UrlFilterResult applyUrlFilterConfigurationById(String id, JsonNode parameters) {
        if (isInputNotValid(id, parameters)) {
            throw new BadRequestAlertException("Input parameters are not valid", ENTITY_NAME, ID_INVALID);
        }
        WIHPMetadata metadata = getMetadata(parameters);
        WihpUrlFilter filterClass = filterConfigurationSingleton.getFilterClassById(id);
        return applyFilter(parameters, metadata, getUrls(parameters), filterClass);
    }

    private JsonNode getUrls(JsonNode configuration) {
        return configuration.get(applicationProperties.getFiltersPropertyUrls());
    }

    private WIHPMetadata getMetadata(JsonNode configuration) {
        return Optional
            .ofNullable(configuration.get(applicationProperties.getFiltersPropertyMetadata()))
            .map(jsonNode -> mapper.convertValue(jsonNode, new TypeReference<Map<String, String[]>>() {
            }))
            .map(UrlWIHPMetadata::new)
            .orElse(new UrlWIHPMetadata(new HashMap<>()));
    }

    protected UrlFilterResult applyFilter(JsonNode parameters, WIHPMetadata metadata, JsonNode urls, WihpUrlFilter filter) {
        filter.configure(parameters);
        List<UrlResult> results = StreamUtils
            .asStream(urls.elements(), false)
            .map(urlNode -> createFilterResult(filter, metadata, urlNode))
            .collect(Collectors.toList());

        return new UrlFilterResult(results);
    }

    private UrlResult createFilterResult(WihpUrlFilter filter, WIHPMetadata metadata, JsonNode urlNode) {
        if (urlFilterValidation.isUrlValid(urlNode.textValue())) {
            Optional<String> url = filter.filter(NULL_SOURCE_URL, metadata, urlNode.textValue());
            return new UrlResult(url.orElse(urlNode.textValue()), url.isPresent());
        } else {
            return new UrlResult(urlNode.textValue(), null);
        }
    }

    protected boolean isInputNotValid(String id, JsonNode configuration) {
        return (
            Strings.isBlank(id) ||
                !isPackageNameValid(id) ||
                configuration == null ||
                configuration.isEmpty() ||
                urlFilterValidation.isUrlListEmpty(configuration)
        );
    }

    protected boolean isPackageNameValid(String packageName) {
        Pattern pattern = Pattern.compile(PACKAGE_PATTERN);
        Matcher matcher = pattern.matcher(packageName);
        return matcher.matches();
    }

}
