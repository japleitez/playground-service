package eu.europa.ec.eurostat.wihp.domain.parse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.eurostat.wihp.domain.FilterName;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParseFilterResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private FilterName name;

    public ParseFilterResponse(ParseFilter parseFilter) {
        this.name = parseFilter.getName();
        this.id = parseFilter.getId();
    }

    public String getId() {
        return id;
    }

    public ParseFilterResponse setId(String id) {
        this.id = id;
        return this;
    }

    public FilterName getName() {
        return name;
    }

    public ParseFilterResponse setName(FilterName name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "PFR{" + "id='" + id + '\'' + ", name=" + name + '}';
    }
}
