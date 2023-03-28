package eu.europa.ec.eurostat.wihp.service.protocols;

public enum ArgCapabilities {
    LANG("lang", "--lang=%s"),
    MAXIMIZED_WINDOW("start-maximized", "--start-maximized"),
    WINDOW_SIZE("window-size", "--window-size=%s");

    private final String metadataKey;
    private final String capability;

    ArgCapabilities(final String metadataKey, final String capability) {
        this.metadataKey = metadataKey;
        this.capability = capability;
    }

    public String getMetadataKey() {
        return metadataKey;
    }

    public String getCapability() {
        return capability;
    }
}
