package eu.europa.ec.eurostat.wihp.service;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ConfigurationResult;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.domain.ValidationResult;
import eu.europa.ec.eurostat.wihp.domain.ValidationResultType;
import eu.europa.ec.eurostat.wihp.service.validator.FilterValidator;
import eu.europa.ec.eurostat.wihp.utils.JsonUtils;
import eu.europa.ec.eurostat.wihp.utils.StreamUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public static final String CONFIGURATION_LIST = "configurations";
    public static final String CONFIGURATION = "configuration";
    public static final String PARAMETERS = "parameters";
    protected static final String ID = "id";
    private final FilterConfigFactory filterConfigFactory;
    private final FieldValidationService fieldValidationService;
    private final MissingFieldValidationService missingFieldValidationService;

    public ValidationService(
        final FilterConfigFactory filterConfigFactory,
        final FieldValidationService fieldValidationService,
        final MissingFieldValidationService missingFieldValidationService
    ) {
        this.filterConfigFactory = filterConfigFactory;
        this.fieldValidationService = fieldValidationService;
        this.missingFieldValidationService = missingFieldValidationService;
    }

    public ValidationResult validate(final JsonNode root, FilterType filterType) {
        JsonNode array = root.get(CONFIGURATION_LIST);

        List<ConfigurationResult> results = StreamUtils
            .asStream(array.elements(), false)
            .map(config -> this.validateConfiguration(config, filterType))
            .collect(Collectors.toList());

        return new ValidationResult(results);
    }

    protected ConfigurationResult validateConfiguration(final JsonNode config, FilterType filterType) {
        String id = validateAndGetId(config);
        Optional<JsonNode> optionalFilterSchema = filterConfigFactory.getFilterConfigService(filterType).getFilterConfigurationById(id);
        ConfigurationResult configurationResult = new ConfigurationResult(id);
        if (optionalFilterSchema.isEmpty()) {
            configurationResult.addValidationError(ValidationError.buildNoValue(FilterValidator.ID, ValidationResultType.INVALID));
        } else {
            JsonNode filterSchema = optionalFilterSchema.get();
            final JsonNode configurationParameters = config.get(CONFIGURATION);
            configurationResult.addValidationErrors(
                missingFieldValidationService.validateFieldsRecursively(configurationParameters, filterSchema)
            );
            configurationResult.addValidationErrors(
                fieldValidationService.validateFieldsRecursively(configurationParameters, filterSchema)
            );
        }
        return configurationResult;
    }

    private String validateAndGetId(final JsonNode config) {
        String id = JsonUtils.parseString(config, FilterValidator.ID);
        Validate.notNull(id);
        return id;
    }
}
