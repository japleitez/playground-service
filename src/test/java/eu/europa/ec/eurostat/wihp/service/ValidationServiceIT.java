package eu.europa.ec.eurostat.wihp.service;

import static eu.europa.ec.eurostat.wihp.domain.ValidationResultType.INVALID;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.ConfigurationResult;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.domain.ValidationResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class ValidationServiceIT {

    private final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TEST_URL_FILTER_ID = "eu.europa.ec.eurostat.wihp.urlfilters.examples.TestUrlFilter";

    @Autowired
    private ValidationService unit;

    @Autowired
    FilterConfigFactory filterConfigFactory;

    @Test
    void validConfigurationsTest() throws IOException {
        ValidationResult validationResult = loadValidationResult("validation_simple/valid_test_url_config.json");
        assertNotNull(validationResult);
        assertFalse(validationResult.hasValidationErrors());
    }

    @Test
    void invalidMissingFieldsConfigurationsTest() throws IOException {
        ValidationResult validationResult = loadValidationResult("validation_simple/invalid_missing_fields_config.json");
        assertNotNull(validationResult);
        assertEquals(2, validationResult.totalErrors());
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "extraCharacters.maxArrayLength",
            "character.required",
            "character.maxLength",
            "character.pattern",
            "minReoccurrence.minimum",
            "minReoccurrence.maximum",
        }
    )
    void when_fieldId_hasViolations_thenViolationsAreFound(String param) throws IOException {
        ValidationResult validationResult = loadValidationResult(getConfig(param));

        assertNotNull(validationResult);
        assertTrue(validationResult.hasValidationErrors());

        Optional<ValidationError> optError = validationResult.findValidationError(TEST_URL_FILTER_ID, param, INVALID);
        assertTrue(optError.isPresent());
    }

    private String getConfig(String param) {
        switch (param) {
            case "extraCharacters.maxArrayLength":
                return "validation_simple/invalid_array_size_config.json";
            case "character.required":
                return "validation_simple/invalid_string_empty_config.json";
            case "character.maxLength":
                return "validation_simple/invalid_string_max_length_config.json";
            case "character.pattern":
                return "validation_simple/invalid_reg_exp_config.json";
            case "minReoccurrence.minimum":
                return "validation_simple/invalid_min_values_config.json";
            case "minReoccurrence.maximum":
                return "validation_simple/invalid_max_values_config.json";
        }
        return null;
    }

    @ParameterizedTest
    @ValueSource(strings = { "extraCharacters.a", "character.b", "character.c", "character.d", "minReoccurrence.e", "minReoccurrence.f" })
    void whenValidConfigurations_butNotExistingFieldId_thenEmptyOptional(String param) throws IOException {
        ValidationResult validationResult = loadValidationResult("validation_simple/valid_test_url_config.json");

        assertNotNull(validationResult);

        Optional<ValidationError> optError = validationResult.findValidationError(TEST_URL_FILTER_ID, param, INVALID);
        assertTrue(optError.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "traCharacters.maxArrayLength",
            "character.required",
            "character.maxLength",
            "character.pattern",
            "minReoccurrence.minimum",
            "minReoccurrence.maximum",
        }
    )
    void whenValidConfigurations_butNotExistingConfigId_thenEmptyOptional(String param) throws IOException {
        ValidationResult validationResult = loadValidationResult("validation_simple/valid_test_url_config.json");

        assertNotNull(validationResult);

        Optional<ValidationError> optError = validationResult.findValidationError("well", param, INVALID);
        assertTrue(optError.isEmpty());
    }

    private ValidationResult loadValidationResult(String s) throws IOException {
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(s);
        JsonNode root = MAPPER.readTree(inputStream);
        return unit.validate(root, FilterType.URL_FILTER);
    }

    @Test
    void when_validConfigurations_then_validateConfiguration_hasValidationErrorsFalse() throws IOException {
        //Given a valid configuration
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("validation_simple/valid_test_url_config.json");
        //and it's mapped to jsonNOde
        JsonNode root = MAPPER.readTree(inputStream);
        JsonNode array = root.get(ValidationService.CONFIGURATION_LIST);

        //WHen parameter Exists
        ConfigurationResult errors = unit.validateConfiguration(array.get(0), FilterType.URL_FILTER);
        //then empty Json node is returned
        assertTrue(errors.getValidationErrors().isEmpty());
    }

    @Test
    void when_validParseConfigurations_then_NoErrorFound() throws IOException {
        //GIVEN
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("validation_simple/valid_test_parse_config.json");
        JsonNode root = MAPPER.readTree(inputStream);
        JsonNode array = root.get(ValidationService.CONFIGURATION_LIST);

        //WHEN
        ConfigurationResult errors = unit.validateConfiguration(array.get(0), FilterType.PARSE_FILTER);

        //THEN
        assertTrue(errors.getValidationErrors().isEmpty());
    }

    @Test
    void when_invalidParseConfigurations_then_ErrorFound() throws IOException {
        //GIVEN
        InputStream inputStream = ClassLoader
            .getSystemClassLoader()
            .getResourceAsStream("validation_simple/invalid_test_parse_config.json");
        JsonNode root = MAPPER.readTree(inputStream);
        JsonNode array = root.get(ValidationService.CONFIGURATION_LIST);

        //WHEN
        ConfigurationResult errors = unit.validateConfiguration(array.get(0), FilterType.PARSE_FILTER);

        //THEN
        assertFalse(errors.getValidationErrors().isEmpty());
        assertEquals("property", errors.getValidationErrors().get(0).getId());
        assertEquals("REQUIRED", errors.getValidationErrors().get(0).getType().name());
        assertEquals("property_wrong", errors.getValidationErrors().get(1).getId());
        assertEquals("UNEXPECTED", errors.getValidationErrors().get(1).getType().name());
    }

    @Test
    void when_invalidMinValuesConfigurations_then_validateConfiguration_hasValidationErrorsTrue() throws IOException {
        //Given a valid configuration
        InputStream inputStream = ClassLoader
            .getSystemClassLoader()
            .getResourceAsStream("validation_simple/invalid_min_values_config.json");
        //and it's mapped to jsonNOde
        JsonNode root = MAPPER.readTree(inputStream);
        JsonNode array = root.get(ValidationService.CONFIGURATION_LIST);

        //WHen parameter Exists
        ConfigurationResult errors = unit.validateConfiguration(array.get(0), FilterType.URL_FILTER);
        //then empty Json node is returned
        assertFalse(errors.getValidationErrors().isEmpty());
    }
}
