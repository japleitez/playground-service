package eu.europa.ec.eurostat.wihp.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;

public interface FilterConfigService {
    String SERVER_ERROR = "serverError";
    String ID_NOT_FOUND = "ID not found: ";
    String ERROR_MAPPING_CONFIGURATION = "Error mapping configuration: ";
    FilterType getType();
    Optional<JsonNode> getFilterConfigurationById(String id);
}
