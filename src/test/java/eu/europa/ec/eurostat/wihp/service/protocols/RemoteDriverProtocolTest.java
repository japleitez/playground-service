package eu.europa.ec.eurostat.wihp.service.protocols;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.service.url.UrlWIHPMetadata;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoteDriverProtocolTest {

    private RemoteDriverProtocol unit;

    @Mock
    private ApplicationProperties applicationProperties;

    // @Test This test needs an active selenium server
    public void test() {
        //GIVEN
        when(applicationProperties.getSeleniumAddress()).thenReturn("http://host.docker.internal:4444");
        unit = new RemoteDriverProtocol(new RemoteDriverFactory(applicationProperties));
        String url = "https://www.google.com";

        //WHEN
        ProtocolResponse protocolResponse = unit.getProtocolOutput(url, new UrlWIHPMetadata());

        //THEN
        assertNotNull(protocolResponse);
        assertEquals(200, protocolResponse.getStatusCode());
    }
}
