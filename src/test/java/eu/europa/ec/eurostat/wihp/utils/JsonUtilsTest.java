package eu.europa.ec.eurostat.wihp.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonNode JSON_NODE;

    @BeforeAll
    public static void setup() throws IOException {
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("json_data/valid_sample.json");
        JSON_NODE = MAPPER.readTree(inputStream);
    }

    @Test
    void validIntegerFieldTest() {
        Integer result = JsonUtils.parseInt(JSON_NODE, "int", null);
        assertNotNull(result);
        assertEquals(1, result);
    }

    @Test
    void validDefaultIntegerFieldTest() {
        Integer result = JsonUtils.parseInt(JSON_NODE, "$int", -1);
        assertNotNull(result);
        assertEquals(-1, result);
    }

    @Test
    void validDoubleFieldTest() {
        Double result = JsonUtils.parseDouble(JSON_NODE, "double", null);
        assertNotNull(result);
        assertEquals(0.5d, result);
    }

    @Test
    void validDefaultDoubleFieldTest() {
        Double result = JsonUtils.parseDouble(JSON_NODE, "$double", 10d);
        assertNotNull(result);
        assertEquals(10d, result);
    }

    @Test
    void validBooleanFieldTest() {
        Boolean result = JsonUtils.parseBoolean(JSON_NODE, "boolean", null);
        assertNotNull(result);
        assertEquals(true, result);
    }

    @Test
    void validDefaultBooleanFieldTest() {
        Boolean result = JsonUtils.parseBoolean(JSON_NODE, "$boolean", null);
        assertNull(result);
    }

    @Test
    void validStringFieldTest() {
        String result = JsonUtils.parseString(JSON_NODE, "string", null);
        assertNotNull(result);
        assertEquals("found", result);
    }

    @Test
    void validDefaultStringFieldTest() {
        String result = JsonUtils.parseString(JSON_NODE, "$string", "notfound");
        assertNotNull(result);
        assertEquals("notfound", result);
    }

    @Test
    void validStringArrayTest() {
        List<String> result = JsonUtils.parseStringArray(JSON_NODE, "stringArray");
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void validMissingStringArrayTest() {
        List<String> result = JsonUtils.parseStringArray(JSON_NODE, "$stringArray");
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void validIntegerArrayTest() {
        List<Integer> result = JsonUtils.parseIntegerArray(JSON_NODE, "integerArray");
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void validMissingIntegerArrayTest() {
        List<Integer> result = JsonUtils.parseIntegerArray(JSON_NODE, "$integerArray");
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void validDoubleArrayTest() {
        List<Double> result = JsonUtils.parseDoubleArray(JSON_NODE, "doubleArray");
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void validMissingDoubleArrayTest() {
        List<Double> result = JsonUtils.parseDoubleArray(JSON_NODE, "$doubleArray");
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void validBooleanArrayTest() {
        List<Boolean> result = JsonUtils.parseBooleanArray(JSON_NODE, "booleanArray");
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void validMissingBooleanArrayTest() {
        List<Boolean> result = JsonUtils.parseBooleanArray(JSON_NODE, "$booleanArray");
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
