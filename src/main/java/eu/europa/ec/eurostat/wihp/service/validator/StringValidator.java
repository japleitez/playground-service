package eu.europa.ec.eurostat.wihp.service.validator;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.eurostat.wihp.domain.ValidationResultType.INVALID;

@Component
public class StringValidator implements FilterValidator {

    public Boolean apply(final JsonNode fieldSchema) {
        JsonNode type = Optional.of(fieldSchema)
            .map(f -> f.get(TYPE))
            .orElseThrow();
        return TYPE_STRING.equals(type.textValue());
    }

    @Override
    public List<ValidationError> validate(final JsonNode fieldValue, final String fieldName, final JsonNode fieldSchema) {

        final String value = fieldValue.asText();
        final List<ValidationError> validationErrors = new ArrayList<>();
        final Boolean required = JsonUtils.parseBoolean(fieldSchema, REQUIRED, false);
        if (Boolean.TRUE.equals(required)) {
            if (fieldValue.isNull() || StringUtils.isEmpty(value)) {
                validationErrors.add(ValidationError.buildComposite(fieldName, REQUIRED, INVALID, value));
            } else {
                validationErrors.addAll(CommonValidator.validateStringValue(value, fieldName, fieldSchema));
            }
        }
        return validationErrors;
    }

}
