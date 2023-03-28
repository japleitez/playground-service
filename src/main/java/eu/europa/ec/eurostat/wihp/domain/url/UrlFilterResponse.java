package eu.europa.ec.eurostat.wihp.domain.url;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.eurostat.wihp.domain.FilterName;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlFilterResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private FilterName name;

    public UrlFilterResponse(UrlFilter filter) {
        this.name = filter.getName();
        this.id = filter.getId();
    }

    public String getId() {
        return id;
    }

    public UrlFilterResponse setId(String id) {
        this.id = id;
        return this;
    }

    public FilterName getName() {
        return name;
    }

    public UrlFilterResponse setName(FilterName name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "UrlFilter{" + "id='" + id + '\'' + ", name=" + name + '}';
    }
}
