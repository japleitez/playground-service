package eu.europa.ec.eurostat.wihp.service.protocols;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.apache.hc.core5.http.HttpStatus.SC_SERVER_ERROR;

import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;

@Service
public class RemoteDriverProtocol implements ProtocolService {

    private final RemoteDriverFactory remoteDriverFactory;

    public RemoteDriverProtocol(RemoteDriverFactory remoteDriverFactory) {
        this.remoteDriverFactory = remoteDriverFactory;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.DYNAMIC;
    }

    @Override
    public synchronized ProtocolResponse getProtocolOutput(final String url, final WIHPMetadata md) {
        try {
            RemoteWebDriver driver = remoteDriverFactory.createRemoteWebDriver(md);
            driver.get(url);
            String html = driver.getPageSource();
            driver.close();
            return new ProtocolResponse(html, SC_OK, md);
        } catch (Exception e) {
            return new ProtocolResponse(null, SC_SERVER_ERROR, md);
        }
    }
}
