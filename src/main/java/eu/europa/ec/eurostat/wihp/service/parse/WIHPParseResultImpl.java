package eu.europa.ec.eurostat.wihp.service.parse;

import eu.europa.ec.eurostat.wihp.parsefilters.WIHPMetadata;
import eu.europa.ec.eurostat.wihp.parsefilters.WIHPOutlink;
import eu.europa.ec.eurostat.wihp.parsefilters.WIHPParseData;
import eu.europa.ec.eurostat.wihp.parsefilters.WIHPParseResult;
import java.util.*;

public class WIHPParseResultImpl implements WIHPParseResult {

    private List<WIHPOutlink> outLinks;
    private final Map<String, WIHPParseData> parseMap;

    public WIHPParseResultImpl() {
        this(new HashMap<String, WIHPParseData>(), new ArrayList<>());
    }

    public WIHPParseResultImpl(List<WIHPOutlink> links) {
        this(new HashMap<String, WIHPParseData>(), links);
    }

    public WIHPParseResultImpl(Map<String, WIHPParseData> map) {
        this(map, new ArrayList<>());
    }

    public WIHPParseResultImpl(Map<String, WIHPParseData> parseMap, List<WIHPOutlink> outLinks) {
        if (parseMap == null) {
            throw new NullPointerException();
        }
        this.parseMap = parseMap;
        this.outLinks = outLinks;
    }

    @Override
    public boolean isEmpty() {
        return this.parseMap.isEmpty();
    }

    @Override
    public int size() {
        return this.parseMap.size();
    }

    @Override
    public List<WIHPOutlink> getOutlinks() {
        return this.outLinks;
    }

    @Override
    public void setOutlinks(List<WIHPOutlink> outLinks) {
        this.outLinks = outLinks;
    }

    @Override
    public WIHPParseData get(String URL) {
        WIHPParseData parse = this.parseMap.get(URL);
        if (Objects.isNull(parse)) {
            parse = new WIHPParseDataImpl();
            this.parseMap.put(URL, parse);
            return parse;
        }
        return parse;
    }

    @Override
    public String[] getValues(String URL, String key) {
        WIHPParseData parse = this.parseMap.get(URL);
        if (Objects.isNull(parse)) {
            return null;
        }
        return parse.getValues(key);
    }

    @Override
    public void put(String URL, String key, String value) {
        get(URL).getMetadata().addValue(key, value);
    }

    @Override
    public void set(String URL, WIHPMetadata metadata) {
        get(URL).setMetadata(metadata);
    }

    @Override
    public Map<String, WIHPParseData> getParseMap() {
        return this.parseMap;
    }

    @Override
    public Iterator<Map.Entry<String, WIHPParseData>> iterator() {
        return this.parseMap.entrySet().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("METADATA\n");
        parseMap.forEach((k, v) -> sb.append(k).append(": ").append(v.getMetadata().toString()).append("\n"));
        sb.append("\nOUTLINKS\n");
        outLinks.forEach(k -> sb.append(k.toString()).append("\n"));
        return sb.toString();
    }
}
