package eu.europa.ec.eurostat.wihp.domain.navigation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NavigationFilterConfiguration {

    @JsonProperty("navigationFilters")
    Set<NavigationFilter> navigationFilters;

    public NavigationFilterConfiguration(Set<NavigationFilter> navigationFilters) {
        this.navigationFilters = navigationFilters;
    }

    public Set<NavigationFilter> getNavigationFilters() {
        return navigationFilters;
    }

    public void setNavigationFilters(Set<NavigationFilter> navigationFilters) {
        this.navigationFilters = navigationFilters;
    }

    public Optional<JsonNode> getConfigurationById(String id) {
        return navigationFilters.stream().filter(p -> p.getId().equals(id)).findAny().map(NavigationFilter::getConfiguration);
    }
}
