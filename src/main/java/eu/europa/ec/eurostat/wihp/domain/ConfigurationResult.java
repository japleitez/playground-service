package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class ConfigurationResult {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("validationErrors")
    private final List<ValidationError> validationErrors = new ArrayList<>();

    public ConfigurationResult(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<ValidationError> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    public Optional<ValidationError> findValidationErrorByIdAndType(String id, ValidationResultType type) {
        return validationErrors.stream().filter(v -> v.getId().equals(id) && v.getType().equals(type)).findFirst();
    }

    public void addValidationError(ValidationError error) {
        validationErrors.add(error);
    }

    public void addValidationErrors(List<ValidationError> errors) {
        validationErrors.addAll(errors);
    }
}
