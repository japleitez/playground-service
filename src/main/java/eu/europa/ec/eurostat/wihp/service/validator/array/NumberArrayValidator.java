package eu.europa.ec.eurostat.wihp.service.validator.array;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.service.validator.CommonValidator;
import eu.europa.ec.eurostat.wihp.service.validator.FilterValidator;
import eu.europa.ec.eurostat.wihp.utils.StreamUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class NumberArrayValidator implements FilterValidator {

    public Boolean apply(final JsonNode fieldSchema) {
        JsonNode type = Optional.of(fieldSchema)
            .map(f -> f.get(TYPE))
            .orElseThrow();

        JsonNode arrayType = fieldSchema.get(ARRAY_TYPE);

        return TYPE_ARRAY.equals(type.textValue()) && arrayType != null && TYPE_NUMBER.equals(arrayType.textValue());
    }

    @Override
    public List<ValidationError> validate(final JsonNode fieldValue, final String fieldName, final JsonNode fieldSchema) {

        List<Double> values = getArrayNodeValues(fieldValue);
        final List<ValidationError> validationErrors = CommonValidator.validateRequiredAndArraySize(values, fieldName, fieldSchema);
        if (validationErrors.isEmpty()) {
            return values.stream()
                .filter(Objects::nonNull)
                .map(value-> CommonValidator.validateDoubleValue(value, fieldName, fieldSchema))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        }
        return validationErrors;
    }

    protected List<Double> getArrayNodeValues(final JsonNode jsonArray) {
        if (jsonArray != null && jsonArray.isArray()) {
            return StreamUtils
                .asStream(jsonArray.elements(), false)
                .filter(CommonValidator::hasValue)
                .map(JsonNode::asDouble).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
