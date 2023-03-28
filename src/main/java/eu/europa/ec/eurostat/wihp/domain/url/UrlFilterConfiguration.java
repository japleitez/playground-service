package eu.europa.ec.eurostat.wihp.domain.url;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlFilterConfiguration {

    @JsonProperty("urls")
    Set<UrlFilter> urls;

    public UrlFilterConfiguration(Set<UrlFilter> urls) {
        this.urls = urls;
    }

    public Set<UrlFilter> getUrls() {
        return urls;
    }

    public void setUrls(Set<UrlFilter> urls) {
        this.urls = urls;
    }

    public Optional<JsonNode> getConfigurationById(String id) {
        Optional<UrlFilter> filter = urls.stream().filter(u -> u.getId().equals(id)).findAny();
        if (filter.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(filter.get().getConfiguration());
        }
    }
}
