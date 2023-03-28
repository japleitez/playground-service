package eu.europa.ec.eurostat.wihp.service.parse;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.parse.ParseFilter;
import eu.europa.ec.eurostat.wihp.domain.parse.ParseFilterResponse;
import eu.europa.ec.eurostat.wihp.exceptions.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.exceptions.NotFoundAlertException;
import eu.europa.ec.eurostat.wihp.parsefilters.WIHPParseData;
import eu.europa.ec.eurostat.wihp.parsefilters.WIHPParseResult;
import eu.europa.ec.eurostat.wihp.parsefilters.WihpParseFilter;
import eu.europa.ec.eurostat.wihp.service.protocols.Protocol;
import eu.europa.ec.eurostat.wihp.service.protocols.ProtocolFactory;
import eu.europa.ec.eurostat.wihp.service.protocols.ProtocolResponse;
import eu.europa.ec.eurostat.wihp.service.protocols.ProtocolService;
import eu.europa.ec.eurostat.wihp.service.url.UrlWIHPMetadata;
import eu.europa.ec.eurostat.wihp.utils.FilterUtils;
import eu.europa.ec.eurostat.wihp.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.w3c.dom.DocumentFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.europa.ec.eurostat.wihp.utils.PaginationUtils.createPage;

@Service
public class ParseFilterService {

    private final ParseFilterLoaderService parseFilterLoaderService;
    private final ProtocolFactory protocolFactory;

    public static final String ENTITY_NAME = "ParseFilterService";
    private static final String ID_INVALID = "idInvalid";

    public ParseFilterService(ParseFilterLoaderService parseFilterLoaderService, ProtocolFactory protocolFactory) {
        this.parseFilterLoaderService = parseFilterLoaderService;
        this.protocolFactory = protocolFactory;
    }

    public Page<ParseFilterResponse> getAllParseFilterConfiguration(Pageable pageable) {
        Set<ParseFilter> filters = this.parseFilterLoaderService.getParseFilterConfiguration().getParseFilters();
        List<ParseFilterResponse> filtersResponse = filters.stream()
            .map(ParseFilterResponse::new)
            .collect(Collectors.toUnmodifiableList());
        return createPage(filtersResponse, pageable);
    }

    public JsonNode getParseFilterConfigurationById(String id) {
        if (!FilterUtils.isPackageNameValid(id)) {
            throw new BadRequestAlertException("Filter ID is not valid", ENTITY_NAME, ID_INVALID);
        }
        return parseFilterLoaderService
            .getFilterConfigurationById(id)
            .orElseThrow(() -> new NotFoundAlertException("ParseFilter not found. ID=".concat(id), ENTITY_NAME, ID_INVALID));
    }

    public Map<String, String[]> applyParseFilterConfigurationById(String id, JsonNode configuration) {
        String url = resolveUrl(configuration);
        Protocol protocol = resolveProtocol(configuration);
        ProtocolService protocolService = protocolFactory.getProtocolService(protocol);
        ProtocolResponse protocolResponse = protocolService.getProtocolOutput(url, new UrlWIHPMetadata());
        if (protocolResponse.getStatusCode() == 200) {
            DocumentFragment doc = DocumentFragmentBuilder.getDocumentFragment(url, protocolResponse.getContent());
            WihpParseFilter filter = parseFilterLoaderService.getParseFilterClassById(id);
            filter.configure(configuration);
            WIHPParseResult result = new WIHPParseResultImpl();
            filter.filter(url, doc, result);
            WIHPParseData parseData = result.get(url);
            return parseData.getMetadata().asMap();
        }
        return new HashMap<>();
    }

    protected String resolveUrl(JsonNode configuration) {
        String url = JsonUtils.parseString(configuration, "url");
        if (StringUtils.isEmpty(url)) {
            throw new BadRequestAlertException("Input parameters are not valid. URL must be set", ENTITY_NAME, ID_INVALID);
        }
        return url;
    }

    protected Protocol resolveProtocol(JsonNode configuration) {
        String name = JsonUtils.parseString(configuration, "protocol");
        if (StringUtils.isEmpty(name)) {
            throw new BadRequestAlertException("Input parameters are not valid. Protocol must be set", ENTITY_NAME, ID_INVALID);
        }
        return Protocol.valueOf(name.toUpperCase());
    }
}
