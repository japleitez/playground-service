package eu.europa.ec.eurostat.wihp.service.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.service.validator.array.BooleanArrayValidator;
import eu.europa.ec.eurostat.wihp.service.validator.array.IntegerArrayValidator;
import eu.europa.ec.eurostat.wihp.service.validator.array.NumberArrayValidator;
import eu.europa.ec.eurostat.wihp.service.validator.array.StringArrayValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorCastTest {

    static JsonNode FILTER_CONFIG;
    static JsonNode FILTER_CONFIG_WRONG;
    static JsonNode FILTER_CONFIG_WRONG_ARRAY;
    static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    public static void setup() throws IOException {
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("json_data/filter_params_types.json");
        FILTER_CONFIG = MAPPER.readTree(inputStream);
        InputStream inputStream2 = ClassLoader.getSystemClassLoader().getResourceAsStream("json_data/filter_parameter_type_wrong.json");
        FILTER_CONFIG_WRONG = MAPPER.readTree(inputStream2);
        InputStream inputStream3 = ClassLoader.getSystemClassLoader().getResourceAsStream("json_data/filter_parameter_array_type_wrong.json");
        FILTER_CONFIG_WRONG_ARRAY = MAPPER.readTree(inputStream3);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "string_type",
        "integer_type",
        "number_type",
        "boolean_type",
        "string_array_type",
        "integer_array_type",
        "number_array_type",
        "boolean_array_type"})
    public void whenFilterConfigIsCorrect_thenApply_returns_cast(String param) {
            // GIVEN a correct configuration The apply method returns the expected type

        JsonNode settings = FILTER_CONFIG.get(param);
        switch(param){
            case "string_type":{
                Boolean casted = new StringValidator().apply(settings);
                assertTrue(casted);}
            break;
            case "integer_type":{
                Boolean casted = new IntegerValidator().apply(settings);
                assertTrue(casted);}
            break;
            case "number_type":{
                Boolean casted = new NumberValidator().apply(settings);
                assertTrue(casted);}
            break;
            case "boolean_type":{
                Boolean casted = new BooleanValidator().apply(settings);
                assertTrue(casted);}
            break;
            case "string_array_type": {
                Boolean casted = new StringArrayValidator().apply(settings);
                assertTrue(casted);}
            break;
            case "integer_array_type": {
                Boolean casted = new IntegerArrayValidator().apply(settings);
                assertTrue(casted);}
            break;
            case "number_array_type": {
                Boolean casted = new NumberArrayValidator().apply(settings);
                assertTrue(casted);}
            break;
            case "boolean_array_type": {
                Boolean casted = new BooleanArrayValidator().apply(settings);
                assertTrue(casted);}
            break;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "string_type",
        "integer_type",
        "number_type",
        "boolean_type",
        "string_array_type",
        "integer_array_type",
        "number_array_type",
        "boolean_array_type"})
    public void whenFilterConfigIsNotCorrect_thenApply_returns_NoCast(String param) {
        // GIVEN an  incorrect configuration The apply method returns the expected type

        JsonNode settings = FILTER_CONFIG_WRONG.get(param);
        switch(param){
            case "string_type":{
                Boolean casted = new StringValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "integer_type":{
                Boolean casted = new IntegerValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "number_type":{
                Boolean casted = new NumberValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "boolean_type":{
                Boolean casted = new BooleanValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "string_array_type": {
                Boolean casted = new StringArrayValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "integer_array_type": {
                Boolean casted = new IntegerArrayValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "number_array_type": {
                Boolean casted = new NumberArrayValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "boolean_array_type": {
                Boolean casted = new BooleanArrayValidator().apply(settings);
                assertFalse(casted);}
            break;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "string_array_type",
        "integer_array_type",
        "number_array_type",
        "boolean_array_type"})
    public void whenFilterConfigAsWrongTypeInarrayIsNotCorrect_thenApply_returns_NoCast(String param) {
        // GIVEN an  incorrect configuration The apply method returns the expected type

        JsonNode settings = FILTER_CONFIG_WRONG_ARRAY.get(param);
        switch(param){
            case "string_array_type": {
                Boolean casted = new StringArrayValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "integer_array_type": {
                Boolean casted = new IntegerArrayValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "number_array_type": {
                Boolean casted = new NumberArrayValidator().apply(settings);
                assertFalse(casted);}
            break;
            case "boolean_array_type": {
                Boolean casted = new BooleanArrayValidator().apply(settings);
                assertFalse(casted);}
            break;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "string_type",
        "integer_type",
        "number_type",
        "boolean_type",
        "string_array_type",
        "integer_array_type",
        "number_array_type",
        "boolean_array_type"})
    public void whenFilterConfigIsEmpty_thenApply_returns_NoCast(String param) {
        // GIVEN an empty configuration The apply method returns the expected type
        JsonNode node = MAPPER.createObjectNode();
        switch(param){
            case "string_type":{
                StringValidator validator = new StringValidator();
                assertThrows( NoSuchElementException.class,()-> validator.apply(node));}
            break;
            case "integer_type":{
                IntegerValidator validator = new IntegerValidator();
                assertThrows( NoSuchElementException.class,()-> validator.apply(node));}
            break;
            case "number_type":{
                NumberValidator validator = new NumberValidator();
                assertThrows( NoSuchElementException.class,()-> validator.apply(node));}
            break;
            case "boolean_type":{
                BooleanValidator validator = new BooleanValidator();
                assertThrows( NoSuchElementException.class,()-> validator.apply(node));}
            break;
            case "string_array_type": {
                StringArrayValidator validator = new StringArrayValidator();
                assertThrows( NoSuchElementException.class,()-> validator.apply(node));}
            break;
            case "integer_array_type": {
                IntegerArrayValidator validator = new IntegerArrayValidator();
                assertThrows( NoSuchElementException.class,()-> validator.apply(node));}
            break;
            case "number_array_type": {
                NumberArrayValidator validator = new NumberArrayValidator();
                assertThrows( NoSuchElementException.class,()-> validator.apply(node));}
            break;
            case "boolean_array_type": {
                BooleanArrayValidator validator = new BooleanArrayValidator();
                assertThrows( NoSuchElementException.class,()-> validator.apply(node));}
            break;
        }
    }
}
