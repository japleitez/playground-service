package eu.europa.ec.eurostat.wihp.service.protocols;

public enum ProtocolHeader {
    ACCEPT("Accept"),
    ACCEPT_LANGUAGE("Accept-Language");

    private final String value;

    ProtocolHeader(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
