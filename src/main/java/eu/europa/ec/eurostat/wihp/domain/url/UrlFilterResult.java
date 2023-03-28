package eu.europa.ec.eurostat.wihp.domain.url;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlFilterResult {

    @JsonProperty("urls")
    List<UrlResult> urls;

    public UrlFilterResult(List<UrlResult> urls) {
        this.urls = urls;
    }

    public List<UrlResult> getUrls() {
        return urls;
    }
}
