package eu.europa.ec.eurostat.wihp.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationResult;
import eu.europa.ec.eurostat.wihp.domain.navigation.NavigationFilterResponse;
import eu.europa.ec.eurostat.wihp.navigationfilters.ProtocolResponse;
import eu.europa.ec.eurostat.wihp.service.FilterType;
import eu.europa.ec.eurostat.wihp.service.ValidationService;
import eu.europa.ec.eurostat.wihp.service.navigation.NavigationFilterService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/navigation-filters")
public class NavigationFilterResource {

    private final NavigationFilterService navigationFilterService;

    private final ValidationService validationService;

    public NavigationFilterResource(
        NavigationFilterService navigationFilterService, ValidationService validationService) {
        this.navigationFilterService = navigationFilterService;
        this.validationService = validationService;
    }

    @GetMapping
    public ResponseEntity<List<NavigationFilterResponse>> getNavigationFilter(Pageable pageable) {
        Page<NavigationFilterResponse> response = navigationFilterService.getAllNavigationFilterConfigurations(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), response);
        return ResponseEntity.ok().headers(headers).body(response.getContent());
    }

    @GetMapping("{id}")
    public ResponseEntity<JsonNode> getNavigationFilter(@PathVariable String id) {
        JsonNode configuration = navigationFilterService.getNavigationFilterConfigurationById(id);
        return ResponseEntity.ok().body(configuration);
    }

    @PostMapping("{id}")
    public ResponseEntity<ProtocolResponse> applyParseFilter(@PathVariable String id, @RequestBody JsonNode parameters)
        throws URISyntaxException {
        ProtocolResponse response = navigationFilterService.applyNavigationFilterConfigurationById(id, parameters);
        return ResponseEntity.created(new URI("/navigation-filters/" + id)).body(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validate(@RequestBody JsonNode configurations) {
        ValidationResult validationResult = validationService.validate(configurations, FilterType.NAVIGATION_FILTER);
        return ResponseEntity.ok(validationResult);
    }
}
