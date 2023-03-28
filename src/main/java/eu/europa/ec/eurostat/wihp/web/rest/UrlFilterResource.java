package eu.europa.ec.eurostat.wihp.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationResult;
import eu.europa.ec.eurostat.wihp.domain.url.UrlFilterResponse;
import eu.europa.ec.eurostat.wihp.domain.url.UrlFilterResult;
import eu.europa.ec.eurostat.wihp.service.FilterType;
import eu.europa.ec.eurostat.wihp.service.ValidationService;
import eu.europa.ec.eurostat.wihp.service.url.UrlFilterService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class UrlFilterResource {

    private final UrlFilterService urlFilterService;
    private final ValidationService validationService;

    public UrlFilterResource(final UrlFilterService urlFilterService, ValidationService validationService) {
        this.urlFilterService = urlFilterService;
        this.validationService = validationService;
    }

    @GetMapping("/url-filters")
    public ResponseEntity<List<UrlFilterResponse>> getUrlFilters(Pageable pageable) {
        Page<UrlFilterResponse> response = urlFilterService.getAllUrlFilterConfiguration(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), response);
        return ResponseEntity.ok().headers(headers).body(response.getContent());
    }

    @GetMapping("/url-filters/{id}")
    public ResponseEntity<JsonNode> getUrlFilter(@PathVariable String id) {
        JsonNode configuration = urlFilterService.getUrlFilterConfigurationById(id);

        return ResponseEntity.ok().body(configuration);
    }

    @PostMapping("/url-filters/{id}")
    public ResponseEntity<UrlFilterResult> applyUrlFilter(@PathVariable String id, @RequestBody JsonNode parameters)
        throws URISyntaxException {
        UrlFilterResult urlFilterResult = urlFilterService.applyUrlFilterConfigurationById(id, parameters);

        return ResponseEntity.created(new URI("/url-filters/:" + id)).body(urlFilterResult);
    }

    @PostMapping("/url-filters/validate")
    public ResponseEntity<ValidationResult> validate(@RequestBody JsonNode configurations) {
        ValidationResult validationResult = validationService.validate(configurations, FilterType.URL_FILTER);

        return ResponseEntity.ok(validationResult);
    }
}
