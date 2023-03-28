package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterName implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("default")
    private String defaultName;

    @JsonProperty("translationKey")
    private String translationKey;

    public FilterName defaultName(String defaultName) {
        this.defaultName = defaultName;
        return this;
    }

    public FilterName translationKey(String translationKey) {
        this.translationKey = translationKey;
        return this;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    @Override
    public String toString() {
        return "FilterName{" + "defaultName='" + defaultName + '\'' + ", translationKey='" + translationKey + '\'' + '}';
    }
}
