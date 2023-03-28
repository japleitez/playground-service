package eu.europa.ec.eurostat.wihp.service.protocols;

public enum PrefCapabilities {
    LOAD_IMAGES("profile.managed_default_content_settings.images", 1),
    COOKIES("profile.managed_default_content_settings.cookies", 2),
    GEOLOCOATION("profile.managed_default_content_settings.geolocation", 2);

    private final String metadataKey;
    private final int capability;

    PrefCapabilities(final String metadataKey, final int capability) {
        this.metadataKey = metadataKey;
        this.capability = capability;
    }

    public String getMetadataKey() {
        return metadataKey;
    }

    public int getCapability() {
        return capability;
    }
}
