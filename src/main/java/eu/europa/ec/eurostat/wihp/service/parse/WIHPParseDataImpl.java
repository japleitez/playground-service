package eu.europa.ec.eurostat.wihp.service.parse;

import eu.europa.ec.eurostat.wihp.parsefilters.WIHPMetadata;
import eu.europa.ec.eurostat.wihp.parsefilters.WIHPParseData;

public class WIHPParseDataImpl implements WIHPParseData {

    private byte[] content;
    private String text;
    private WIHPMetadata metadata;

    public WIHPParseDataImpl() {
        this.metadata = new ParseWIHPMetadata();
    }

    public WIHPParseDataImpl(String text, WIHPMetadata metadata) {
        this.text = text;
        this.metadata = metadata;
        this.content = new byte[0];
    }

    public WIHPParseDataImpl(WIHPMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public WIHPMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public void setMetadata(WIHPMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public byte[] getContent() {
        return this.content;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public void put(String key, String value) {
        this.metadata.addValue(key, value);
    }

    @Override
    public String get(String key) {
        return this.metadata.getFirstValue(key);
    }

    @Override
    public String[] getValues(String key) {
        return this.metadata.getValues(key);
    }
}
