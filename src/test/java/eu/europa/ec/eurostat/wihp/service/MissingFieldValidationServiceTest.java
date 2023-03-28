package eu.europa.ec.eurostat.wihp.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.ValidationError;
import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class MissingFieldValidationServiceTest {

    private MissingFieldValidationService unit;
    private static final String OBJECT_BASED_FILTER_SCHEMA_JSON = "validation_object/object_based_filter_schema.json";

    @BeforeEach
    public void setUp() {
        unit = new MissingFieldValidationService();
    }

    @Test
    void when_presentPropertiesInObjectBasedConfiguration_then_noErrorsFound() throws IOException {
        //GIVEN
        JsonNode filterSchema = TestUtil.getJsonNodeFromResource(OBJECT_BASED_FILTER_SCHEMA_JSON);
        JsonNode configuration = TestUtil.getJsonNodeFromResource("validation_object/object_based_filter_config_valid.json");

        //WHEN
        List<ValidationError> errors = unit.validateFieldsRecursively(configuration, filterSchema);

        //THEN
        assertTrue(errors.isEmpty());
    }

    @Test
    void when_missingPropertiesInObjectBasedConfiguration_then_errorsFound() throws IOException {
        //GIVEN
        JsonNode schema = TestUtil.getJsonNodeFromResource(OBJECT_BASED_FILTER_SCHEMA_JSON);
        JsonNode configuration = TestUtil.getJsonNodeFromResource("validation_object/object_based_filter_config_missing.json");

        //WHEN
        List<ValidationError> errors = unit.validateFieldsRecursively(configuration, schema);

        //THEN
        assertFalse(errors.isEmpty());
        assertEquals(5, errors.size());
    }
}
