package eu.europa.ec.eurostat.wihp.service;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.domain.ValidationResultType;
import eu.europa.ec.eurostat.wihp.utils.FieldSchemaUtil;
import eu.europa.ec.eurostat.wihp.utils.StreamUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MissingFieldValidationService {

    protected List<ValidationError> validateFieldsRecursively(JsonNode config, JsonNode filterSchema) {
        return StreamUtils
            .asStream(filterSchema.get(ValidationService.PARAMETERS).elements(), false)
            .map(paramSchema -> getMissingFieldErrors(config.get(FieldSchemaUtil.getFieldId(paramSchema)), paramSchema))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    protected List<ValidationError> getMissingFieldErrors(JsonNode field, JsonNode paramSchema) {
        List<ValidationError> errors = new ArrayList<>();
        if (Objects.isNull(field)) {
            String paramId = FieldSchemaUtil.getFieldId(paramSchema);
            errors.add(ValidationError.buildNoValue(paramId, ValidationResultType.REQUIRED));
        } else {
            JsonNode fieldSchema = paramSchema.get("type");
            if (FieldSchemaUtil.isObjectField(fieldSchema)) {
                errors.addAll(validateFieldsRecursively(field, fieldSchema));
            } else if (FieldSchemaUtil.isObjectArrayField(fieldSchema)) {
                field.forEach(item -> errors.addAll(validateFieldsRecursively(item, fieldSchema)));
            }
        }
        return errors;
    }
}
