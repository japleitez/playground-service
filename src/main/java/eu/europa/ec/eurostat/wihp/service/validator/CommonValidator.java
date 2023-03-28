package eu.europa.ec.eurostat.wihp.service.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static eu.europa.ec.eurostat.wihp.domain.ValidationResultType.INVALID;
import static eu.europa.ec.eurostat.wihp.service.validator.FilterValidator.*;

public class CommonValidator {

    /* Static Class, private constructor is to prevent instantiation of the class*/
    private CommonValidator() {
    }

    public static List<ValidationError> validateBooleanValue(final Boolean value, final String fieldName, final JsonNode fieldSettings) {

        Validate.notNull(fieldName);
        Validate.notNull(fieldSettings);
        List<ValidationError> validationErrors = new ArrayList<>();
        Boolean required = JsonUtils.parseBoolean(fieldSettings, REQUIRED );
        if (Objects.isNull(value) && Boolean.TRUE.equals(required)) {
            validationErrors.add(ValidationError.buildComposite(fieldName, REQUIRED, INVALID, required));
        }
        return validationErrors;
    }

    public static List<ValidationError> validateStringValue(final String value, final String fieldName, final JsonNode fieldSettings) {
        Validate.notNull(fieldName);
        Validate.notNull(fieldSettings);
        final List<ValidationError> validationErrors = new ArrayList<>();
        final String regexp = JsonUtils.parseString(fieldSettings, PATTERN);
        if (StringUtils.isNotEmpty(regexp) && !Pattern.matches(regexp, value)) {
            validationErrors.add(ValidationError.buildComposite(fieldName, PATTERN, INVALID, regexp));
        }
        final Integer minLength = JsonUtils.parseInt(fieldSettings, MIN_LENGTH);
        if (Objects.nonNull(minLength) && value.length() < minLength) {
            validationErrors.add(ValidationError.buildComposite(fieldName, MIN_LENGTH, INVALID, minLength));
        }
        final Integer maxLength = JsonUtils.parseInt(fieldSettings, MAX_LENGTH);
        if (Objects.nonNull(maxLength) && value.length() > maxLength) {
            validationErrors.add(ValidationError.buildComposite(fieldName, MAX_LENGTH, INVALID, maxLength));
        }
        return validationErrors;
    }

    public static List<ValidationError> validateIntegerValue(final Integer value, final String fieldName, final JsonNode fieldSettings) {
        Validate.notNull(fieldName);
        Validate.notNull(fieldSettings);
        List<ValidationError> validationErrors = new ArrayList<>();
        Boolean required = JsonUtils.parseBoolean(fieldSettings, REQUIRED);
        if (Objects.isNull(value)) {
            if (Boolean.TRUE.equals(required)) {
                validationErrors.add(ValidationError.buildComposite(fieldName, REQUIRED, INVALID, value));
            }
        } else {
            final Integer minimum = JsonUtils.parseInt(fieldSettings, MINIMUM );
            if (Objects.nonNull(minimum) && value < minimum) {
                validationErrors.add(ValidationError.buildComposite(fieldName, MINIMUM, INVALID, minimum));
            }
            final Integer maximum = JsonUtils.parseInt(fieldSettings, MAXIMUM);
            if (Objects.nonNull(maximum) && value > maximum) {
                validationErrors.add(ValidationError.buildComposite(fieldName, MAXIMUM, INVALID, maximum));
            }
        }
        return validationErrors;
    }

    public static List<ValidationError> validateDoubleValue(final Double value, final String fieldName, final JsonNode fieldSettings) {
        Validate.notNull(fieldName);
        Validate.notNull(fieldSettings);
        List<ValidationError> validationErrors = new ArrayList<>();
        Boolean required = JsonUtils.parseBoolean(fieldSettings, REQUIRED);
        if (Objects.isNull(value)) {
            if (Boolean.TRUE.equals(required)) {
                validationErrors.add(ValidationError.buildComposite(fieldName, REQUIRED, INVALID, value));
            }
        } else {
            final Double minimum = JsonUtils.parseDouble(fieldSettings, MINIMUM);
            if (Objects.nonNull(minimum) && value < minimum) {
                validationErrors.add(ValidationError.buildComposite(fieldName, MINIMUM, INVALID, minimum));
            }
            final Double maximum = JsonUtils.parseDouble(fieldSettings, MAXIMUM);
            if (Objects.nonNull(maximum) && value > maximum) {
                validationErrors.add(ValidationError.buildComposite(fieldName, MAXIMUM, INVALID, maximum));
            }
        }
        return validationErrors;
    }

    public static List<ValidationError> validateRequiredAndArraySize(final List<?> values, final String fieldName, final JsonNode fieldSettings) {
        Validate.notNull(fieldName);
        Validate.notNull(fieldSettings);
        final Boolean required = JsonUtils.parseBoolean(fieldSettings, REQUIRED);
        List<ValidationError> validationErrors = new ArrayList<>();
        if (CollectionUtils.isEmpty(values)) {
            if (Boolean.TRUE.equals(required)) {
                validationErrors.add(ValidationError.buildComposite(fieldName, REQUIRED, INVALID, null));
            }
        } else {
            final Integer minArrayLength = JsonUtils.parseInt(fieldSettings, MIN_ARRAY_LENGTH);
            if (Objects.nonNull(minArrayLength) && values.size() < minArrayLength) {
                validationErrors.add(ValidationError.buildComposite(fieldName, MIN_ARRAY_LENGTH, INVALID, minArrayLength));
            }
            final Integer maxArrayLength = JsonUtils.parseInt(fieldSettings, MAX_ARRAY_LENGTH);
            if (Objects.nonNull(maxArrayLength) && values.size() > maxArrayLength) {
                validationErrors.add(ValidationError.buildComposite(fieldName, MAX_ARRAY_LENGTH, INVALID, maxArrayLength));
            }
        }
        return validationErrors;
    }

    public static boolean hasValue(final JsonNode n) {
        return n != null && !n.isNull() && !(n instanceof NullNode);
    }
}
