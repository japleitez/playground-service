package eu.europa.ec.eurostat.wihp.domain;

public class ValidationError {

    private final String id;
    private final ValidationResultType type;
    private final Object value;

    private ValidationError(final String id, final ValidationResultType type, final Object value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public static ValidationError build(final String id, final ValidationResultType type, final Object value) {
        return new ValidationError(id, type, value);
    }

    public static ValidationError buildNoValue(final String id, final ValidationResultType type) {
        return new ValidationError(id, type, null);
    }

    public static ValidationError buildComposite(
        final String id,
        final String fieldName,
        final ValidationResultType type,
        final Object value
    ) {
        final String compositeId = id + "." + fieldName;
        return new ValidationError(compositeId, type, value);
    }

    public String getId() {
        return id;
    }

    public ValidationResultType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{" + "id='" + id + '\'' + ", type=" + type + ", value=" + value + '}';
    }
}
