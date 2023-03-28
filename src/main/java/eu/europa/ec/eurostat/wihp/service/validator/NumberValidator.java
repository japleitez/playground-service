package eu.europa.ec.eurostat.wihp.service.validator;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NumberValidator implements FilterValidator {

    public Boolean apply(final JsonNode fieldSchema) {
        JsonNode type = Optional.of(fieldSchema)
            .map(f -> f.get(TYPE))
            .orElseThrow();
        return TYPE_NUMBER.equals(type.textValue());
    }

    @Override
    public List<ValidationError> validate(final JsonNode fieldValue, final String fieldName, final JsonNode fieldSchema) {

        if (!fieldValue.isNull()) {
            final Double value = fieldValue.asDouble();
            return CommonValidator.validateDoubleValue(value, fieldName, fieldSchema);
        } else {
            return CommonValidator.validateDoubleValue(null, fieldName, fieldSchema);
        }
    }
}
