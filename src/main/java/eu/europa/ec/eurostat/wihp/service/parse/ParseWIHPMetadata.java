package eu.europa.ec.eurostat.wihp.service.parse;

import eu.europa.ec.eurostat.wihp.parsefilters.WIHPMetadata;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class ParseWIHPMetadata implements WIHPMetadata {

    private Map<String, String[]> metadata;

    private boolean locked = false;

    public ParseWIHPMetadata() {
        metadata = new HashMap<>();
    }

    public ParseWIHPMetadata(Map<String, String[]> metadata) {
        Validate.notNull(metadata, "metadata must not be null");
        this.metadata = metadata;
    }

    public Map<String, String[]> getMetadata() {
        return metadata;
    }

    @Override
    public void putAll(WIHPMetadata wihpMetadata) {
        this.metadata.putAll(wihpMetadata.asMap());
    }

    @Override
    public void putAll(WIHPMetadata m, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            putAll(m);
            return;
        }
        Map<String, String[]> ma = m.asMap();
        ma.forEach((k, v) -> setValues(prefix + k, v));
    }

    @Override
    public String getFirstValue(String key) {
        String[] values = metadata.get(key);
        if (values == null) return null;
        if (values.length == 0) return null;
        return values[0];
    }

    @Override
    public String getFirstValue(String key, String prefix) {
        if (prefix == null || prefix.length() == 0) return getFirstValue(key);
        return getFirstValue(prefix + key);
    }

    @Override
    public String[] getValues(String key, String prefix) {
        if (prefix == null || prefix.length() == 0) return getValues(key);
        return getValues(prefix + key);
    }

    @Override
    public String[] getValues(String key) {
        String[] values = metadata.get(key);
        if (values == null) return new String[0];
        if (values.length == 0) return new String[0];
        return values;
    }

    @Override
    public void setValue(String key, String value) {
        checkLockException();

        metadata.put(key, new String[] { value });
    }

    @Override
    public void setValues(String key, String[] values) {
        checkLockException();

        if (values == null || values.length == 0) return;
        metadata.put(key, values);
    }

    @Override
    public void addValue(String key, String value) {
        checkLockException();

        if (StringUtils.isBlank(value)) return;

        String[] existingValues = metadata.get(key);
        if (existingValues == null || existingValues.length == 0) {
            setValue(key, value);
            return;
        }

        int currentLength = existingValues.length;
        String[] newValues = new String[currentLength + 1];
        newValues[currentLength] = value;
        System.arraycopy(existingValues, 0, newValues, 0, currentLength);
        metadata.put(key, newValues);
    }

    @Override
    public void addValues(String key, Collection<String> values) {
        checkLockException();

        if (values.isEmpty()) return;
        String[] existingvals = metadata.get(key);
        if (existingvals == null) {
            metadata.put(key, values.toArray(new String[values.size()]));
            return;
        }

        ArrayList<String> existing = new ArrayList<>(existingvals.length + values.size());
        for (String v : existingvals) existing.add(v);

        existing.addAll(values);
        metadata.put(key, existing.toArray(new String[existing.size()]));
    }

    @Override
    public String[] remove(String key) {
        checkLockException();
        return metadata.remove(key);
    }

    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        if (prefix == null) prefix = "";
        Iterator<Map.Entry<String, String[]>> iter = metadata.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String[]> entry = iter.next();
            for (String val : entry.getValue()) {
                sb.append(prefix).append(entry.getKey()).append(": ").append(val).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public int size() {
        return metadata.size();
    }

    @Override
    public Set<String> keySet() {
        return metadata.keySet();
    }

    @Override
    public String getFirstValue(WIHPMetadata md, String... keys) {
        for (String key : keys) {
            String val = md.getFirstValue(key);
            if (StringUtils.isBlank(val)) continue;
            return val;
        }
        return null;
    }

    @Override
    public Map<String, String[]> asMap() {
        return metadata;
    }

    @Override
    public WIHPMetadata lock() {
        locked = true;
        return this;
    }

    @Override
    public WIHPMetadata unlock() {
        locked = false;
        return this;
    }

    private final void checkLockException() {
        if (locked) throw new ConcurrentModificationException("Attempt to modify a metadata after it has been sent to the serializer");
    }
}
