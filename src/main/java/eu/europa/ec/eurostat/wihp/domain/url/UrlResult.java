package eu.europa.ec.eurostat.wihp.domain.url;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlResult {

    @JsonProperty("url")
    String url;

    @JsonProperty("result")
    Boolean response;

    public UrlResult(String url, Boolean response) {
        this.response = response;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getResponse() {
        return response;
    }
}
