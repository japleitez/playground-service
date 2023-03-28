package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class ValidationResult {

    @JsonProperty("configurations")
    private final List<ConfigurationResult> configurations;

    public ValidationResult(List<ConfigurationResult> configurations){
        this.configurations = configurations;
    }

    public void add(ConfigurationResult result) {
        configurations.add(result);
    }

    public boolean hasValidationErrors() {
        return configurations.stream().anyMatch(c -> !c.getValidationErrors().isEmpty());
    }

    public int totalErrors() {
        return configurations.stream().mapToInt(c -> c.getValidationErrors().size()).sum();
    }

    public Optional<ConfigurationResult> findConfigurationResult(String configId) {
        return configurations.stream().filter(c -> c.getId().equals(configId)).findFirst();
    }

    public Optional<ValidationError> findValidationError(String configId, String fieldId, ValidationResultType resultType) {
        return findConfigurationResult(configId).
            flatMap(f -> f.findValidationErrorByIdAndType(fieldId, resultType));
    }
}
