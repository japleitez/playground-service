package eu.europa.ec.eurostat.wihp.service.protocols;

import static java.util.stream.Collectors.toList;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;

@Service
public class RemoteDriverFactory {

    private static final List<String> DEFAULT_ARGS = List.of(
        "--mute-audio",
        "--headless",
        "--lang=en",
        "--mute-audio",
        "--window-size=1080,1920",
        "--start-maximized",
        "--disable-popup-blocking",
        "--disable-audio-output",
        "--disable-dev-shm-usage"
    );
    private static final Map<String, Object> DEFAULT_PREFS = Map.of(
        "profile.managed_default_content_settings.notifications",
        2,
        "profile.managed_default_content_settings.images",
        1,
        "profile.managed_default_content_settings.media_stream",
        2,
        "profile.managed_default_content_settings.cookies",
        2,
        "profile.managed_default_content_settings.plugins",
        2,
        "profile.managed_default_content_settings.geolocation",
        2
    );
    private static final String USER_AGENT_ARG = "--user-agent=%s";

    private final ApplicationProperties applicationProperties;

    public RemoteDriverFactory(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public RemoteWebDriver createRemoteWebDriver(final WIHPMetadata md) {
        try {
            return new RemoteWebDriver(new URL(applicationProperties.getSeleniumAddress()), configure(md));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSeleniumAddress() {
        return this.applicationProperties.getSeleniumAddress();
    }

    protected DesiredCapabilities configure(final WIHPMetadata metadata) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setJavascriptEnabled(true);
        capabilities.setBrowserName("chrome");
        capabilities.setCapability("headless", "true");
        final Map<String, Object> chromeOptions = new HashMap<>();

        final List<String> args = getArgs(metadata);
        chromeOptions.put("args", args);

        final Map<String, Object> prefs = getPrefs(metadata);
        chromeOptions.put("prefs", prefs);

        capabilities.setCapability("goog:chromeOptions", chromeOptions);
        return capabilities;
    }

    private Map<String, Object> getPrefs(final WIHPMetadata metadata) {
        final Map<String, Object> prefs = metadata
            .asMap()
            .entrySet()
            .stream()
            .filter(this::isPrefAllowed)
            .map(this::mapPrefs)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        DEFAULT_PREFS.forEach(prefs::putIfAbsent);
        return prefs;
    }

    private List<String> getArgs(final WIHPMetadata metadata) {
        List<String> args = metadata.asMap().entrySet().stream().filter(this::isArgAllowed).map(this::mapArg).collect(toList());
        args.addAll(DEFAULT_ARGS);
        args.add(userAgent());
        return args;
    }

    private boolean isPrefAllowed(final Map.Entry<String, String[]> stringEntry) {
        return Arrays.stream(PrefCapabilities.values()).anyMatch(capability -> capability.getMetadataKey().equals(stringEntry.getKey()));
    }

    private boolean isArgAllowed(final Map.Entry<String, String[]> stringEntry) {
        return Arrays.stream(ArgCapabilities.values()).anyMatch(capability -> capability.getMetadataKey().equals(stringEntry.getKey()));
    }

    private String mapArg(final Map.Entry<String, String[]> stringEntry) {
        return Arrays
            .stream(ArgCapabilities.values())
            .filter(v -> v.getMetadataKey().equals(stringEntry.getKey()))
            .map(v -> String.format(v.getCapability(), getCapabilityValue(stringEntry)))
            .findFirst()
            .orElseThrow();
    }

    private Map.Entry<String, Integer> mapPrefs(final Map.Entry<String, String[]> stringEntry) {
        return Arrays
            .stream(PrefCapabilities.values())
            .filter(v -> v.getMetadataKey().equals(stringEntry.getKey()))
            .map(v -> Map.entry(stringEntry.getKey(), Integer.valueOf(getCapabilityValue(stringEntry))))
            .findFirst()
            .orElseThrow();
    }

    private String getCapabilityValue(final Map.Entry<String, String[]> stringEntry) {
        if (stringEntry != null && stringEntry.getValue() != null && stringEntry.getValue().length > 0) {
            return stringEntry.getValue()[0];
        }
        return Strings.EMPTY;
    }

    private String userAgent() {
        return String.format(USER_AGENT_ARG, applicationProperties.getUserAgentDetails());
    }
}
