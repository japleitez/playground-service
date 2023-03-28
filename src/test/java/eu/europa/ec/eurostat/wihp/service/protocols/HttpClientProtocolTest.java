package eu.europa.ec.eurostat.wihp.service.protocols;

import static eu.europa.ec.eurostat.wihp.service.protocols.Protocol.STATIC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.service.url.UrlWIHPMetadata;
import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;
import org.apache.hc.core5.http.Header;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpClientProtocolTest {

    private HttpClientProtocol unit;

    @Mock
    private ApplicationProperties applicationProperties;

    @Test
    public void testProtocol() {
        unit = new HttpClientProtocol(applicationProperties);
        assertEquals(STATIC, unit.getProtocol());
    }

    @Test
    public void whenGetProtocolOutput_thenProtocolResponse() {
        //GIVEN
        unit = new HttpClientProtocol(applicationProperties);
        String url = "https://www.google.com";

        //WHEN
        ProtocolResponse protocolResponse = unit.getProtocolOutput(url, new UrlWIHPMetadata());

        //THEN
        assertNotNull(protocolResponse);
        assertEquals(200, protocolResponse.getStatusCode());
    }

    @Test
    public void whenIOException_theStatusCode500() {
        //GIVEN
        unit = new HttpClientProtocol(applicationProperties);
        String url = "https://urldoesnotexist";

        //WHEN
        ProtocolResponse protocolResponse = unit.getProtocolOutput(url, new UrlWIHPMetadata());

        //THEN
        assertNotNull(protocolResponse);
        assertEquals(500, protocolResponse.getStatusCode());
        assertNull(protocolResponse.getContent());
    }

    @Test
    public void whenMetadataMissing_thenIllegalArgumentException() {
        //GIVEN
        unit = new HttpClientProtocol(applicationProperties);
        String url = "https://www.google.com";
        WIHPMetadata metadata = null;

        //WHEN - THEN
        assertThrows(IllegalArgumentException.class, () -> unit.getProtocolOutput(url, metadata));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    public void whenUrl_thenIllegalArgumentException(String url) {
        //GIVEN
        unit = new HttpClientProtocol(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();

        //WHEN - THEN
        assertThrows(IllegalArgumentException.class, () -> unit.getProtocolOutput(url, metadata));
    }

    @Test
    public void whenStatusCodeNotOk_theStatusCodeReturnedAndContentEmpty() {
        //GIVEN
        unit = new HttpClientProtocol(applicationProperties);
        String url = "https://www.googleapis.com/fitness/v1/users/me/dataSources";

        //WHEN
        ProtocolResponse protocolResponse = unit.getProtocolOutput(url, new UrlWIHPMetadata());

        //THEN
        assertNotNull(protocolResponse);
        assertTrue(protocolResponse.getStatusCode() >= 400);
        assertNotNull(protocolResponse.getContent());
    }

    @Test
    public void whenMetadataContainsHeader_thenHeaderSet() {
        //GIVEN
        unit = new HttpClientProtocol(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();
        metadata.setValue("Accept", "text/html");

        //WHEN
        Header[] headers = unit.createHeaders(metadata);

        //THEN
        assertNotNull(headers);
        assertEquals("text/html", headers[0].getValue());
    }

    @Test
    public void whenMetadataContainsNotAllowedHeader_thenHeaderNotIncluded() {
        //GIVEN
        unit = new HttpClientProtocol(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();
        metadata.setValue("Content-type", "json");

        //WHEN
        Header[] headers = unit.createHeaders(metadata);

        //THEN
        assertNotNull(headers);
        assertEquals(0, headers.length);
    }

    @Test
    public void whenGet_thenSetUserAgent() {
        //GIVEN
        final String userAgent = "user agent";
        when(applicationProperties.getUserAgentDetails()).thenReturn(userAgent);
        unit = new HttpClientProtocol(applicationProperties);

        //WHEN
        Header result = unit.agentDetails();

        //THEN
        assertEquals("User-Agent", result.getName());
        assertEquals(userAgent, result.getValue());
    }
}
