package eu.europa.ec.eurostat.wihp.service.protocols;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class HttpClientProtocol implements ProtocolService {

    private static final String USER_AGENT_HEADER = "User-Agent";
    private final ApplicationProperties applicationProperties;

    public HttpClientProtocol(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.STATIC;
    }

    @Override
    public ProtocolResponse getProtocolOutput(final String url, final WIHPMetadata md) {
        Assert.notNull(md, "Metadata should not be null");
        Assert.hasText(url, "URL should not be empty");
        try {
            return Request
                .get(url)
                .setHeaders(createHeaders(md))
                .addHeader(agentDetails())
                .execute()
                .handleResponse(r -> handleResponse(r, md));
        } catch (IOException e) {
            return new ProtocolResponse(null, HttpStatus.SC_INTERNAL_SERVER_ERROR, md);
        }
    }

    protected Header[] createHeaders(WIHPMetadata md) {
        return md
            .asMap()
            .entrySet()
            .stream()
            .filter(this::isHeaderAllowed)
            .map(stringEntry -> new BasicHeader(stringEntry.getKey(), stringEntry.getValue()[0]))
            .toArray(BasicHeader[]::new);
    }

    protected Header agentDetails() {
        return new BasicHeader(USER_AGENT_HEADER, applicationProperties.getUserAgentDetails());
    }

    private boolean isHeaderAllowed(final Map.Entry<String, String[]> stringEntry) {
        return Arrays.stream(ProtocolHeader.values()).anyMatch(protocolHeader -> protocolHeader.getValue().equals(stringEntry.getKey()));
    }

    private ProtocolResponse handleResponse(final ClassicHttpResponse response, final WIHPMetadata md) throws IOException, ParseException {
        return new ProtocolResponse(EntityUtils.toString(response.getEntity()), response.getCode(), md);
    }
}
