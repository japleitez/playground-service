package eu.europa.ec.eurostat.wihp.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;

public class JsonUtils {

    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Integer parseInt(final JsonNode node, final String fieldName) {
        return parseInt(node, fieldName, null);
    }

    public static Integer parseInt(final JsonNode node, final String fieldName, final Integer defaultValue) {
        validateInput(node, fieldName);
        return Optional.of(node).map(n -> n.get(fieldName)).map(JsonNode::asInt).orElse(defaultValue);
    }

    public static Double parseDouble(final JsonNode node, final String fieldName) {
        return parseDouble(node, fieldName, null);
    }

    public static Double parseDouble(final JsonNode node, final String fieldName, final Double defaultValue) {
        validateInput(node, fieldName);
        return Optional.of(node).map(n -> n.get(fieldName)).map(JsonNode::asDouble).orElse(defaultValue);
    }

    public static Boolean parseBoolean(final JsonNode node, final String fieldName) {
        return parseBoolean(node, fieldName, false);
    }

    public static Boolean parseBoolean(final JsonNode node, final String fieldName, final Boolean defaultValue) {
        validateInput(node, fieldName);
        return Optional.of(node).map(n -> n.get(fieldName)).map(JsonNode::asBoolean).orElse(defaultValue);
    }

    public static String parseString(final JsonNode node, final String fieldName) {
        return parseString(node, fieldName, null);
    }

    public static String parseString(final JsonNode node, final String fieldName, final String defaultValue) {
        validateInput(node, fieldName);
        return Optional.of(node).map(n -> n.get(fieldName)).map(JsonNode::asText).orElse(defaultValue);
    }

    public static List<String> parseStringArray(final JsonNode node, final String fieldName) {
        validateInput(node, fieldName);
        return Optional.of(node).map(n -> n.get(fieldName)).map(JsonUtils::getListOfString).orElse(Collections.emptyList());
    }

    public static List<Integer> parseIntegerArray(final JsonNode node, final String fieldName) {
        validateInput(node, fieldName);
        return Optional.of(node).map(n -> n.get(fieldName)).map(JsonUtils::getListOfInteger).orElse(Collections.emptyList());
    }

    public static List<Double> parseDoubleArray(final JsonNode node, String fieldName) {
        validateInput(node, fieldName);
        return Optional.of(node).map(n -> n.get(fieldName)).map(JsonUtils::getListOfDouble).orElse(Collections.emptyList());
    }

    public static List<Boolean> parseBooleanArray(final JsonNode node, String fieldName) {
        validateInput(node, fieldName);

        return Optional.of(node).map(n -> n.get(fieldName)).map(JsonUtils::getListOfBoolean).orElse(Collections.emptyList());
    }

    private static List<String> getListOfString(final JsonNode fieldNode) {
        return StreamUtils.asStream(fieldNode.elements(), false).map(JsonUtils::getString).collect(Collectors.toList());
    }

    private static List<Double> getListOfDouble(final JsonNode fieldNode) {
        return StreamUtils.asStream(fieldNode.elements(), false).map(JsonUtils::getDouble).collect(Collectors.toList());
    }

    private static List<Integer> getListOfInteger(final JsonNode fieldNode) {
        return StreamUtils.asStream(fieldNode.elements(), false).map(JsonUtils::getInteger).collect(Collectors.toList());
    }

    private static List<Boolean> getListOfBoolean(final JsonNode fieldNode) {
        return StreamUtils.asStream(fieldNode.elements(), false).map(JsonUtils::getBoolean).collect(Collectors.toList());
    }

    private static String getString(final JsonNode node) {
        if (isNodeNull(node)) {
            return null;
        } else {
            return node.asText();
        }
    }

    private static Integer getInteger(final JsonNode node) {
        if (isNodeNull(node)) {
            return null;
        } else {
            return node.asInt();
        }
    }

    private static Double getDouble(final JsonNode node) {
        if (isNodeNull(node)) {
            return null;
        } else {
            return node.asDouble();
        }
    }

    private static Boolean getBoolean(final JsonNode node) {
        if (isNodeNull(node)) {
            return false;
        } else {
            return node.asBoolean();
        }
    }

    private static boolean isNodeNull(final JsonNode node) {
        return Objects.isNull(node) || (node instanceof NullNode);
    }

    private static void validateInput(final JsonNode node, String fieldName) {
        Validate.notNull(node);
        Validate.notNull(fieldName);
    }
}
