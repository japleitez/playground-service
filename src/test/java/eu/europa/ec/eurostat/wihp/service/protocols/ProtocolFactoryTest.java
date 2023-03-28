package eu.europa.ec.eurostat.wihp.service.protocols;

import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ProtocolFactoryTest {

    private final ApplicationProperties applicationProperties = new ApplicationProperties();
    private final RemoteDriverProtocol remoteDriverProtocol = new RemoteDriverProtocol(new RemoteDriverFactory(applicationProperties));
    private final HttpClientProtocol httpClientProtocol = new HttpClientProtocol(applicationProperties);
    private ProtocolFactory unit;

    @ParameterizedTest
    @EnumSource(Protocol.class)
    public void whenEnum_thenReturnClient(Protocol protocol) {
        //GIVEN
        unit = new ProtocolFactory(List.of(remoteDriverProtocol, httpClientProtocol));

        //WHEN
        ProtocolService protocolService = unit.getProtocolService(protocol);

        //THEN
        assertEquals(protocol, protocolService.getProtocol());
    }
}
