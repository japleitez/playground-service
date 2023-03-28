package eu.europa.ec.eurostat.wihp.service.validator;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;

import java.util.List;

/**
 * This interface is used to Inject a list of validators, which implement this interface, into the validation service
 * and to stream them all.
 */
public interface FilterValidator {


    String ID = "id";
    String TYPE = "type";
    String ARRAY_TYPE = "arrayType";
    String REQUIRED = "required";
    String PATTERN = "pattern";
    String MINIMUM = "minimum";
    String MAXIMUM = "maximum";
    String MIN_LENGTH = "minLength";
    String MAX_LENGTH = "maxLength";
    String MIN_ARRAY_LENGTH = "minArrayLength";
    String MAX_ARRAY_LENGTH = "maxArrayLength";

    String TYPE_ARRAY = "array";
    String TYPE_NUMBER = "number";
    String TYPE_STRING = "string";
    String TYPE_INTEGER = "integer";
    String TYPE_BOOLEAN = "boolean";

    /**
     * The method apply() is used  to select the correct validator for the given field based on its type.
     * @param fieldSchema : The type of the field comes with the parameter: fieldSchema
     * @return returns true if the type of the field matches false otherwise, if the TYPE field is not present an exception is thrown
     */
    Boolean apply(final JsonNode fieldSchema);

    /**
     *  The method validate is the one which applies the validation schema to the incoming field and check if the rules in the schema are matched
     * @param fieldValue  : this is the candidate to be validated
     * @param fieldName  : this is the name of the field used to relate to the set of rules in the schema
     * @param fieldSchema : this is the schema of rules which, the field , needs to comply
     * @return : A list of ValidationError are returned and the list can be empty if there are no errors, and it is not empty in latter case
     */
    List<ValidationError> validate(JsonNode fieldValue, String fieldName, JsonNode fieldSchema);


}
