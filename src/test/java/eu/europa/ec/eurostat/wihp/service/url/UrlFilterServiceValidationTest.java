package eu.europa.ec.eurostat.wihp.service.url;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.config.ApplicationPropertiesIT;
import java.util.Iterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UrlFilterServiceValidationTest {

    UrlFilterServiceValidation unit;

    @BeforeEach
    public void setUp() {
        unit = new UrlFilterServiceValidation();
    }

    @Test
    void whenNodeNotCorrect_thenApplyFilterReturnsEmptyObjectNode() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        JsonNode validNode = mapper.readTree(UrlFilterServiceTest.NODE_STRING);

        JsonNode urls = validNode.get(ApplicationPropertiesIT.FILTER_PROPERTY_URLS);

        Iterator<JsonNode> urlIterator = urls.elements();
        while (urlIterator.hasNext()) {
            JsonNode urlNode = urlIterator.next();
            if (urlNode.asText().contains("invalid")) {
                Assertions.assertFalse(unit.isUrlValid(urlNode.textValue()));
            } else {
                Assertions.assertTrue(unit.isUrlValid(urlNode.textValue()));
            }
        }
    }
}
