package eu.europa.ec.eurostat.wihp.service.url;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UrlFilterServiceValidation {

    private final Logger log = LoggerFactory.getLogger(UrlFilterServiceValidation.class);

    public boolean isUrlListEmpty(JsonNode node) {
        JsonNode urls = node.get("urls");
        return urls == null || urls.isEmpty();
    }

    public boolean isUrlValid(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException ignored) {
            log.warn("THe URL provided is not valid: ".concat(url));
        }
        return false;
    }
}
