package eu.europa.ec.eurostat.wihp.service.protocols;

import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;

public interface ProtocolService {
    Protocol getProtocol();

    ProtocolResponse getProtocolOutput(String url, WIHPMetadata md);
}
