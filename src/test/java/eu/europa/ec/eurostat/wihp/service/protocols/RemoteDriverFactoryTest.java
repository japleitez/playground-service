package eu.europa.ec.eurostat.wihp.service.protocols;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.service.url.UrlWIHPMetadata;
import eu.europa.ec.eurostat.wihp.urlfilters.WIHPMetadata;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.remote.DesiredCapabilities;

@ExtendWith(MockitoExtension.class)
class RemoteDriverFactoryTest {

    private RemoteDriverFactory unit;

    @Mock
    private ApplicationProperties applicationProperties;

    @Test
    public void whenMetadataIsSet_thenSetDriverConfiguration() {
        //GIVEN
        unit = new RemoteDriverFactory(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();
        metadata.addValue("lang", "en");

        //WHEN
        DesiredCapabilities capabilities = unit.configure(metadata);

        //THEN
        Map<String, Object> chromeOptions = (Map<String, Object>) capabilities.getCapability("goog:chromeOptions");
        assertNotNull(chromeOptions);
        List<String> args = (List<String>) chromeOptions.get("args");
        assertNotNull(args);
        assertTrue(args.contains("--lang=en"));
    }

    @Test
    public void whenMetadataIsSetWithNullValue_thenSetDriverConfiguration() {
        //GIVEN
        unit = new RemoteDriverFactory(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();
        metadata.setValue("start-maximized", null);

        //WHEN
        DesiredCapabilities capabilities = unit.configure(metadata);

        //THEN
        Map<String, Object> chromeOptions = (Map<String, Object>) capabilities.getCapability("goog:chromeOptions");
        assertNotNull(chromeOptions);
        List<String> args = (List<String>) chromeOptions.get("args");
        assertNotNull(args);
        assertTrue(args.contains("--start-maximized"));
    }

    @Test
    public void whenMetadataIsSetWithEmptyValue_thenSetDriverConfiguration() {
        //GIVEN
        unit = new RemoteDriverFactory(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();
        metadata.setValue("start-maximized", "");

        //WHEN
        DesiredCapabilities capabilities = unit.configure(metadata);

        //THEN
        Map<String, Object> chromeOptions = (Map<String, Object>) capabilities.getCapability("goog:chromeOptions");
        assertNotNull(chromeOptions);
        List<String> args = (List<String>) chromeOptions.get("args");
        assertNotNull(args);
        assertTrue(args.contains("--start-maximized"));
    }

    @Test
    public void whenMetadataIsNotAllowedSet_thenNotChromeOption() {
        //GIVEN
        unit = new RemoteDriverFactory(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();
        final String value = "value";
        metadata.addValue("another", value);

        //WHEN
        DesiredCapabilities capabilities = unit.configure(metadata);

        //THEN
        Map<String, Object> chromeOptions = (Map<String, Object>) capabilities.getCapability("goog:chromeOptions");
        assertNotNull(chromeOptions);
        List<String> args = (List<String>) chromeOptions.get("args");
        assertNotNull(args);
        assertFalse(args.stream().anyMatch(s -> s.contains(value)));
    }

    @Test
    public void whenConfigure_thenDefaultsSet() {
        //GIVEN
        unit = new RemoteDriverFactory(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();

        //WHEN
        DesiredCapabilities capabilities = unit.configure(metadata);

        //THEN
        assertEquals("chrome", capabilities.getBrowserName());
        assertEquals("true", capabilities.getCapability("headless"));
        assertTrue((Boolean) capabilities.getCapability("javascriptEnabled"));

        Map<String, Object> chromeOptions = (Map<String, Object>) capabilities.getCapability("goog:chromeOptions");
        assertNotNull(chromeOptions);
        List<String> args = (List<String>) chromeOptions.get("args");
        assertNotNull(args);
        assertTrue(args.contains("--headless"));
        assertTrue(args.contains("--lang=en"));
        assertTrue(args.contains("--start-maximized"));
        assertTrue(args.contains("--mute-audio"));
        assertTrue(args.contains("--disable-popup-blocking"));
        assertTrue(args.contains("--disable-audio-output"));
        assertTrue(args.contains("--disable-dev-shm-usage"));

        Map<String, Object> prefs = (Map<String, Object>) chromeOptions.get("prefs");
        assertNotNull(prefs);
        assertTrue(prefs.containsKey("profile.managed_default_content_settings.notifications"));
        assertEquals(prefs.get("profile.managed_default_content_settings.notifications"), 2);

        assertTrue(prefs.containsKey("profile.managed_default_content_settings.images"));
        assertEquals(prefs.get("profile.managed_default_content_settings.images"), 1);

        assertTrue(prefs.containsKey("profile.managed_default_content_settings.media_stream"));
        assertEquals(prefs.get("profile.managed_default_content_settings.media_stream"), 2);

        assertTrue(prefs.containsKey("profile.managed_default_content_settings.cookies"));
        assertEquals(prefs.get("profile.managed_default_content_settings.cookies"), 2);

        assertTrue(prefs.containsKey("profile.managed_default_content_settings.plugins"));
        assertEquals(prefs.get("profile.managed_default_content_settings.plugins"), 2);

        assertTrue(prefs.containsKey("profile.managed_default_content_settings.geolocation"));
        assertEquals(prefs.get("profile.managed_default_content_settings.geolocation"), 2);
    }

    @Test
    public void whenPrefSet_thenDefaultOvewritten() {
        //GIVEN
        unit = new RemoteDriverFactory(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();
        metadata.addValue(PrefCapabilities.LOAD_IMAGES.getMetadataKey(), "3");

        //WHEN
        DesiredCapabilities capabilities = unit.configure(metadata);

        //THEN
        Map<String, Object> chromeOptions = (Map<String, Object>) capabilities.getCapability("goog:chromeOptions");
        assertNotNull(chromeOptions);

        Map<String, Object> prefs = (Map<String, Object>) chromeOptions.get("prefs");
        assertNotNull(prefs);

        assertTrue(prefs.containsKey("profile.managed_default_content_settings.images"));
        assertEquals(prefs.get("profile.managed_default_content_settings.images"), 3);
    }

    @Test
    public void whenArgSet_thenDefaultOvewritten() {
        //GIVEN
        unit = new RemoteDriverFactory(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();
        metadata.addValue(ArgCapabilities.LANG.getMetadataKey(), "gr");

        //WHEN
        DesiredCapabilities capabilities = unit.configure(metadata);

        //THEN
        Map<String, Object> chromeOptions = (Map<String, Object>) capabilities.getCapability("goog:chromeOptions");
        assertNotNull(chromeOptions);
        List<String> args = (List<String>) chromeOptions.get("args");
        assertNotNull(args);
        assertTrue(args.contains("--lang=en"));
    }

    @Test
    public void setSeleniumAddress() {
        //GIVEN
        String seleniumAddress = "seleniumAddress";
        when(applicationProperties.getSeleniumAddress()).thenReturn("seleniumAddress");
        unit = new RemoteDriverFactory(applicationProperties);
        //WHEN
        String result = unit.getSeleniumAddress();

        //THEN
        assertEquals(seleniumAddress, result);
    }

    @Test
    public void whenConfiguration_thenSetAgentDetails() {
        //GIVEN
        final String userAgent = "user agent";
        when(applicationProperties.getUserAgentDetails()).thenReturn(userAgent);
        unit = new RemoteDriverFactory(applicationProperties);
        WIHPMetadata metadata = new UrlWIHPMetadata();

        //WHEN
        DesiredCapabilities capabilities = unit.configure(metadata);

        //THEN
        Map<String, Object> chromeOptions = (Map<String, Object>) capabilities.getCapability("goog:chromeOptions");
        assertNotNull(chromeOptions);
        List<String> args = (List<String>) chromeOptions.get("args");
        assertNotNull(args);
        assertTrue(args.contains("--user-agent=" + userAgent));
    }
}
