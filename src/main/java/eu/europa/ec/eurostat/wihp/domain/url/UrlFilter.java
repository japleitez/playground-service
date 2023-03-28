package eu.europa.ec.eurostat.wihp.domain.url;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.FilterName;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private FilterName name;

    private transient JsonNode configuration;

    public String getId() {
        return id;
    }

    public UrlFilter setId(String id) {
        this.id = id;
        return this;
    }

    public FilterName getName() {
        return name;
    }

    public UrlFilter setName(FilterName name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "UrlFilter{" + "id='" + id + '\'' + ", name=" + name + '}';
    }

    public JsonNode getConfiguration() {
        return configuration;
    }

    public UrlFilter setConfiguration(JsonNode configuration) {
        this.configuration = configuration;
        return this;
    }
}
