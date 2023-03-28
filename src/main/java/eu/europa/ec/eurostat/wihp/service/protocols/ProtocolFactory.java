package eu.europa.ec.eurostat.wihp.service.protocols;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProtocolFactory {

    private final List<ProtocolService> protocolServices;

    public ProtocolFactory(final List<ProtocolService> protocolServices) {
        this.protocolServices = protocolServices;
    }

    public ProtocolService getProtocolService(final Protocol protocol) {
        return protocolServices.stream().filter(p -> protocol.equals(p.getProtocol())).findFirst().orElseThrow();
    }
}
