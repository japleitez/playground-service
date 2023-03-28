package eu.europa.ec.eurostat.wihp.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationResult;
import eu.europa.ec.eurostat.wihp.domain.parse.ParseFilterResponse;
import eu.europa.ec.eurostat.wihp.service.FilterType;
import eu.europa.ec.eurostat.wihp.service.ValidationService;
import eu.europa.ec.eurostat.wihp.service.parse.ParseFilterService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class ParseFilterResource {

    private final ParseFilterService parseFilterService;
    private final ValidationService validationService;

    public ParseFilterResource(ParseFilterService parseFilterService, ValidationService validationService) {
        this.parseFilterService = parseFilterService;
        this.validationService = validationService;
    }

    @GetMapping("/parse-filters")
    public ResponseEntity<List<ParseFilterResponse>> getParseFilters(Pageable pageable) {
        Page<ParseFilterResponse> response = parseFilterService.getAllParseFilterConfiguration(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), response);
        return ResponseEntity.ok().headers(headers).body(response.getContent());
    }

    @GetMapping("/parse-filters/{id}")
    public ResponseEntity<JsonNode> getParseFilter(@PathVariable String id) {
        JsonNode configuration = parseFilterService.getParseFilterConfigurationById(id);
        return ResponseEntity.ok().body(configuration);
    }

    @PostMapping("/parse-filters/{id}")
    public ResponseEntity<Map<String, String[]>> applyParseFilter(@PathVariable String id, @RequestBody JsonNode parameters)
        throws URISyntaxException {
        Map<String, String[]> result = parseFilterService.applyParseFilterConfigurationById(id, parameters);
        return ResponseEntity.created(new URI("/parse-filters/:" + id)).body(result);
    }

    @PostMapping("/parse-filters/validate")
    public ResponseEntity<ValidationResult> validate(@RequestBody JsonNode configurations) {
        ValidationResult validationResult = validationService.validate(configurations, FilterType.PARSE_FILTER);

        return ResponseEntity.ok(validationResult);
    }
}
