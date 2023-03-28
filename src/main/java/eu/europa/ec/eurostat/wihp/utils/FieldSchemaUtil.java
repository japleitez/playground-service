package eu.europa.ec.eurostat.wihp.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public class FieldSchemaUtil {

    private FieldSchemaUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isRecursiveField(final JsonNode fieldSchema) {
        return FieldSchemaUtil.isObjectField(fieldSchema) || FieldSchemaUtil.isObjectArrayField(fieldSchema);
    }

    public static boolean isObjectField(JsonNode typeNode) {
        return isObjectField(textValue(typeNode, "/type"));
    }

    public static boolean isObjectArrayField(JsonNode typeNode) {
        return isArrayType(textValue(typeNode, "/type")) && isObjectField(textValue(typeNode, "/arrayType"));
    }

    public static boolean isObjectField(String typeName) {
        return "object".equals(typeName);
    }

    public static boolean isArrayType(String typeName) {
        return "array".equals(typeName);
    }

    private static String textValue(JsonNode node, String path) {
        Validate.notNull(node);
        JsonNode foundNode = node.at(path);
        if (foundNode instanceof MissingNode) {
            throw new IllegalArgumentException("Node misses path at " + path);
        }
        return foundNode.textValue();
    }

    public static String getFieldId(JsonNode fieldSchema) {
        Validate.notNull(fieldSchema);
        JsonNode nodeId = fieldSchema.get("id");
        if (Objects.isNull(nodeId)) {
            throw new IllegalArgumentException("Cannot read fieldSchema id " + fieldSchema.toPrettyString());
        }
        return nodeId.textValue();
    }
}
