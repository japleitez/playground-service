package eu.europa.ec.eurostat.wihp.service.validator;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class IntegerValidator implements FilterValidator {

    public Boolean apply(final JsonNode fieldSchema) {
        JsonNode type = Optional.of(fieldSchema)
            .map(f -> f.get(TYPE))
            .orElseThrow();
        return TYPE_INTEGER.equals(type.textValue());
    }

    @Override
    public List<ValidationError> validate(final JsonNode fieldValue, final String fieldName, final JsonNode fieldSchema) {

        if (!fieldValue.isNull()) {
            final Integer value = fieldValue.asInt();
            return CommonValidator.validateIntegerValue(value, fieldName, fieldSchema);
        } else {
            return CommonValidator.validateIntegerValue(null, fieldName, fieldSchema);
        }
    }
}
