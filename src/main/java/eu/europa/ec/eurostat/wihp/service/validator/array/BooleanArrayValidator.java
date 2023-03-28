package eu.europa.ec.eurostat.wihp.service.validator.array;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.service.validator.CommonValidator;
import eu.europa.ec.eurostat.wihp.service.validator.FilterValidator;
import eu.europa.ec.eurostat.wihp.utils.StreamUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BooleanArrayValidator implements FilterValidator {

    public Boolean apply(final JsonNode fieldSchema) {
        JsonNode type = Optional.of(fieldSchema)
            .map(f -> f.get(TYPE))
            .orElseThrow();

        JsonNode arrayType = fieldSchema.get(ARRAY_TYPE);

        return TYPE_ARRAY.equals(type.textValue()) && arrayType != null && TYPE_BOOLEAN.equals(arrayType.textValue());
    }

    @Override
    public List<ValidationError> validate(final JsonNode fieldValue,final String fieldName, final JsonNode fieldSchema) {

        List<Boolean> values = getArrayNodeValues(fieldValue);
        return CommonValidator.validateRequiredAndArraySize(values, fieldName, fieldSchema);
    }

    protected List<Boolean> getArrayNodeValues(final JsonNode jsonArray) {
        if (jsonArray != null && jsonArray.isArray()) {
            return StreamUtils
                .asStream(jsonArray.elements(), false)
                .filter(CommonValidator::hasValue)
                .map(JsonNode::asBoolean).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
