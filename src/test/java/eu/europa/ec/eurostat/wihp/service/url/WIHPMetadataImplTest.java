package eu.europa.ec.eurostat.wihp.service.url;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WIHPMetadataImplTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String METADATA_CONTENT = "{ \"test\": [\"test1\", \"test2\" ], \"hola\": [\"hola1\", \"hola2\"] }";
    private static final String METADATA_CONTENT_2 = "{ \"core\": [\"core1\", \"core2\" ], \"ciao\": [\"ciao1\", \"ciao2\"] }";

    @Test
    void putAll() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        assertEquals(2, md.size());
        WIHPMetadata md2 = createWIHPMetadata(METADATA_CONTENT_2);
        assertEquals(2, md2.size());
        md.putAll(md2);
        assertEquals(4, md.size());
    }

    @Test
    void testPutAll() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        WIHPMetadata md2 = createWIHPMetadata(METADATA_CONTENT_2);
        md.putAll(md2, "pro");
        assertEquals("core1", md.getFirstValue("core", "pro"));
        assertEquals("core1", md.getFirstValue("procore"));
    }

    @Test
    void getFirstValue() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        String value = md.getFirstValue("test");
        assertEquals("test1", value);
    }

    @Test
    void testGetFirstValue() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        String value = md.getFirstValue("test", "");
        assertEquals("test1", value);
    }

    @Test
    void getValues() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        String[] values = md.getValues("test");
        assertNotNull(values);
        assertEquals(2, values.length);
        assertEquals("test1", values[0]);
        assertEquals("test2", values[1]);
    }

    @Test
    void testGetValues() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        String[] values = md.getValues("test", "");
        assertNotNull(values);
        assertEquals(2, values.length);
        assertEquals("test1", values[0]);
        assertEquals("test2", values[1]);
    }

    @Test
    void setValue() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        md.setValue("test", "reset");
        String[] values = md.getValues("test");
        assertNotNull(values);
        assertEquals(1, values.length);
        assertEquals("reset", values[0]);
    }

    @Test
    void setValues() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        md.setValues("test", new String[] { "reset1", "reset2" });
        String[] values = md.getValues("test");
        assertNotNull(values);
        assertEquals(2, values.length);
        assertEquals("reset1", values[0]);
        assertEquals("reset2", values[1]);
    }

    @Test
    void addValue() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        md.addValue("test", "test3");
        String[] values = md.getValues("test");
        assertNotNull(values);
        assertEquals(3, values.length);
        assertEquals("test1", values[0]);
        assertEquals("test2", values[1]);
        assertEquals("test3", values[2]);
    }

    @Test
    void addValues() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        md.addValues("test", Arrays.asList("test3", "test4"));
        String[] values = md.getValues("test");
        assertNotNull(values);
        assertEquals(4, values.length);
        assertEquals("test1", values[0]);
        assertEquals("test2", values[1]);
        assertEquals("test3", values[2]);
        assertEquals("test4", values[3]);
    }

    @Test
    void remove() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        md.remove("hola");
        String value = md.getFirstValue("hola");
        assertNull(value);
        assertEquals(1, md.size());
    }

    @Test
    void testToString() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        String str = md.toString();
        assertNotNull(str);
        assertEquals("test: test1\n" + "test: test2\n" + "hola: hola1\n" + "hola: hola2\n", str);
    }

    @Test
    void keySet() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        Set<String> keys = md.keySet();
        assertNotNull(keys);
        assertEquals(2, keys.size());
    }

    @Test
    void asMap() {
        WIHPMetadata md = createWIHPMetadata(METADATA_CONTENT);
        Map<String, String[]> map = md.asMap();
        assertNotNull(map);
        assertEquals(2, map.size());
    }

    private WIHPMetadata createWIHPMetadata(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = MAPPER.readTree(content);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("cannot parse content " + content);
        }
        Map<String, String[]> metadata = MAPPER.convertValue(jsonNode, new TypeReference<Map<String, String[]>>() {});
        return new UrlWIHPMetadata(metadata);
    }
}
