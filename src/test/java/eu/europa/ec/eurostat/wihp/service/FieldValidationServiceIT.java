package eu.europa.ec.eurostat.wihp.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.service.validator.FilterValidator;
import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class FieldValidationServiceIT {

    private FieldValidationService unit;
    private static final String TEST_URL_FILTER_SCHEMA_JSON = "validation_simple/test_url_filter_schema.json";
    private static final String OBJECT_BASED_FILTER_SCHEMA_JSON = "validation_object/object_based_filter_schema.json";

    @Autowired
    private List<FilterValidator> validators;

    @BeforeEach
    public void setUp() {
        unit = new FieldValidationService(validators);
    }

    @Test
    void when_fieldNameExists_Then_FindFieldSetting_returnsNonEmptyOptional() throws IOException {
        //GIVEN
        JsonNode filterSchema = TestUtil.getJsonNodeFromResource(TEST_URL_FILTER_SCHEMA_JSON);

        //WHEN
        Optional<JsonNode> node = unit.findFieldSchema("minReoccurrence", filterSchema);
        Optional<JsonNode> node2 = unit.findFieldSchema("notPresent", filterSchema);

        //THEN
        assertTrue(node.isPresent());
        assertTrue(node2.isEmpty());
    }

    @Test
    void when_fieldNameNotExists_then_validateIncomingFields_returnsNonEmptyOptional() throws IOException {
        //GIVEN
        JsonNode configuration = TestUtil.getJsonNodeFromResource(TEST_URL_FILTER_SCHEMA_JSON);
        JsonNode root = TestUtil.getJsonNodeFromResource("validation_simple/valid_test_url_config.json");
        JsonNode array = root.get(ValidationService.CONFIGURATION_LIST).get(0);

        //WHEN
        List<ValidationError> node2 = unit.validateField("nonPresent", array.get(ValidationService.CONFIGURATION), configuration);

        //THEN
        Assertions.assertFalse(node2.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "character",
            "minReoccurrence",
            "maxReoccurrence",
            "percentage",
            "minPercentage",
            "maxPercentage",
            "extraCharacters",
            "extraCharactersMinReoccurrence",
            "extraCharactersMinReoccurrencePercentage",
            "extraCharactersExistInDomain",
        }
    )
    void when_fieldNameExists_And_ParameterValid_then_validateIncomingFields_returnsEmptyOptional(String param) throws IOException {
        //GIVEN
        JsonNode configuration = TestUtil.getJsonNodeFromResource(TEST_URL_FILTER_SCHEMA_JSON);
        JsonNode root = TestUtil.getJsonNodeFromResource("validation_simple/valid_test_url_config.json");
        JsonNode array = root.get(ValidationService.CONFIGURATION_LIST).get(0);
        JsonNode parameters = array.get(ValidationService.CONFIGURATION);

        //WHEN
        List<ValidationError> node = unit.validateField(param, parameters, configuration);

        //THEN
        assertTrue(node.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "minReoccurrence", "percentage", "minPercentage", "maxPercentage" })
    void when_fieldNameExists_And_paramNotValid_then_validateIncomingFields_returnsNonEmptyOptional(String Param) throws IOException {
        //GIVEN
        JsonNode configuration = TestUtil.getJsonNodeFromResource(TEST_URL_FILTER_SCHEMA_JSON);
        JsonNode root = TestUtil.getJsonNodeFromResource("validation_simple/invalid_max_values_config.json");
        JsonNode array = root.get(ValidationService.CONFIGURATION_LIST).get(0);
        JsonNode parameters = array.get(ValidationService.CONFIGURATION);

        //WHEN
        List<ValidationError> node = unit.validateField(Param, parameters, configuration);

        //THEN
        Assertions.assertFalse(node.isEmpty());
    }

    @Test
    void when_validConfigurations_then_validateIncomingFields_returnsNoValidationErrors() throws IOException {
        //GIVEN
        JsonNode configuration = TestUtil.getJsonNodeFromResource(TEST_URL_FILTER_SCHEMA_JSON);
        JsonNode root = TestUtil.getJsonNodeFromResource("validation_simple/valid_test_url_config.json");
        JsonNode array = root.get(ValidationService.CONFIGURATION_LIST).get(0);

        //WHEN
        List<ValidationError> errors = unit.validateFieldsRecursively(array.get(ValidationService.CONFIGURATION), configuration);

        //THEN
        Assertions.assertEquals(0, errors.size());
    }

    @Test
    void when_invalidMaxValuesConfigurations_then_validateIncomingFields_returnsNoValidationErrors() throws IOException {
        //GIVEN
        JsonNode configuration = TestUtil.getJsonNodeFromResource(TEST_URL_FILTER_SCHEMA_JSON);
        JsonNode root = TestUtil.getJsonNodeFromResource("validation_simple/invalid_max_values_config.json");
        JsonNode array = root.get(ValidationService.CONFIGURATION_LIST).get(0);

        //WHEN
        List<ValidationError> errors = unit.validateFieldsRecursively(array.get(ValidationService.CONFIGURATION), configuration);

        //THEN
        Assertions.assertEquals(4, errors.size());
    }

    @Test
    void when_validPropertiesInObjectBasedConfiguration_then_noErrorsFound() throws IOException {
        //GIVEN
        JsonNode schema = TestUtil.getJsonNodeFromResource(OBJECT_BASED_FILTER_SCHEMA_JSON);
        JsonNode configuration = TestUtil.getJsonNodeFromResource("validation_object/object_based_filter_config_valid.json");

        //WHEN
        List<ValidationError> errors = unit.validateFieldsRecursively(configuration, schema);

        //THEN
        assertTrue(errors.isEmpty());
    }

    @Test
    void when_invalidPropertiesInObjectBasedConfiguration_then_errorsFound() throws IOException {
        //GIVEN
        JsonNode schema = TestUtil.getJsonNodeFromResource(OBJECT_BASED_FILTER_SCHEMA_JSON);
        JsonNode configuration = TestUtil.getJsonNodeFromResource("validation_object/object_based_filter_config_invalid.json");

        //WHEN
        List<ValidationError> errors = unit.validateFieldsRecursively(configuration, schema);

        //THEN
        assertFalse(errors.isEmpty());
        assertEquals(15, errors.size());
    }
}
