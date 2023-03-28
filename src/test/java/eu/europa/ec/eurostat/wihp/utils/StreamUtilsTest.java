package eu.europa.ec.eurostat.wihp.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StreamUtilsTest {

    private static ObjectMapper MAPPER = new ObjectMapper();
    private static JsonNode JSON_NODE;

    @BeforeAll
    public static void setup() throws IOException {
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("json_data/null_sample.json");
        JSON_NODE = MAPPER.readTree(inputStream);
    }

    @Test
    void nodeStreamingTest() {
        List<String> results = StreamUtils
            .asStream(JSON_NODE.elements(), false)
            .filter(JsonNode::isNull)
            .map(JsonNode::asText)
            .collect(Collectors.toList());
        assertNotNull(results);
        assertEquals(2, results.size());
    }
}
