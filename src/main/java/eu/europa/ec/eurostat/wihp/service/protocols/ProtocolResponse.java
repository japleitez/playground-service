package eu.europa.ec.eurostat.wihp.service.protocols;

import eu.europa.ec.eurostat.wihp.service.url.UrlWIHPMetadata;
import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;

public class ProtocolResponse {

    private final String content;
    private final int statusCode;
    private final WIHPMetadata metadata;

    public ProtocolResponse(String c, int s, WIHPMetadata md) {
        content = c;
        statusCode = s;
        metadata = md == null ? new UrlWIHPMetadata() : md;
    }

    public String getContent() {
        return content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public WIHPMetadata getMetadata() {
        return metadata;
    }
}
