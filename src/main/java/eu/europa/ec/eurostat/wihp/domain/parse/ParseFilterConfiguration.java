package eu.europa.ec.eurostat.wihp.domain.parse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParseFilterConfiguration {

    @JsonProperty("parseFilters")
    Set<ParseFilter> parseFilters;

    public ParseFilterConfiguration(Set<ParseFilter> parseFilters) {
        this.parseFilters = parseFilters;
    }

    public Set<ParseFilter> getParseFilters() {
        return parseFilters;
    }

    public void setParseFilters(Set<ParseFilter> parseFilters) {
        this.parseFilters = parseFilters;
    }

    public Optional<JsonNode> getConfigurationById(String id) {
        return parseFilters.stream().filter(p -> p.getId().equals(id)).findAny().map(ParseFilter::getConfiguration);
    }
}
