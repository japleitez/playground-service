package eu.europa.ec.eurostat.wihp.service;

import static eu.europa.ec.eurostat.wihp.service.validator.FilterValidator.TYPE;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.domain.ValidationResultType;
import eu.europa.ec.eurostat.wihp.service.validator.FilterValidator;
import eu.europa.ec.eurostat.wihp.utils.FieldSchemaUtil;
import eu.europa.ec.eurostat.wihp.utils.StreamUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

@Service
public class FieldValidationService {

    private final List<FilterValidator> validators;

    public FieldValidationService(final List<FilterValidator> validators) {
        this.validators = validators;
    }

    protected List<ValidationError> validateFieldsRecursively(final JsonNode parameters, final JsonNode filterSchema) {
        Validate.notNull(parameters);

        return StreamUtils
            .asStream(parameters.fieldNames(), false)
            .map(parameter -> validateField(parameter, parameters, filterSchema))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    protected List<ValidationError> validateField(final String parameter, final JsonNode config, final JsonNode filterSchema) {
        final JsonNode field = config.get(parameter);
        return findFieldSchema(parameter, filterSchema)
            .map(fieldSchema -> getInvalidFieldErrors(parameter, field, fieldSchema))
            .orElse(List.of(ValidationError.buildNoValue(parameter, ValidationResultType.UNEXPECTED)));
    }

    protected Optional<JsonNode> findFieldSchema(final String fieldName, final JsonNode filterSchema) {
        return StreamUtils
            .asStream(filterSchema.get(ValidationService.PARAMETERS).elements(), false)
            .filter(node -> node.get(ValidationService.ID).textValue().equals(fieldName))
            .map(node -> node.get(TYPE))
            .findFirst();
    }

    private List<ValidationError> getInvalidFieldErrors(final String fieldName, final JsonNode field, final JsonNode fieldSchema) {
        if (FieldSchemaUtil.isObjectField(fieldSchema)) {
            return validateFieldsRecursively(field, fieldSchema);
        } else if (FieldSchemaUtil.isObjectArrayField(fieldSchema)) {
            List<ValidationError> deepErrors = new ArrayList<>();
            field.forEach(node -> deepErrors.addAll(validateFieldsRecursively(node, fieldSchema)));
            return deepErrors;
        }
        return validators
            .stream()
            .filter(validator -> validator.apply(fieldSchema))
            .findFirst()
            .map(v -> v.validate(field, fieldName, fieldSchema))
            .filter(v -> !v.isEmpty())
            .stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
}
