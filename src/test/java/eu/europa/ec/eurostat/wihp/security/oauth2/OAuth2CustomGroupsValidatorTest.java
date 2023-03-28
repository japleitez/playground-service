package eu.europa.ec.eurostat.wihp.security.oauth2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OAuth2CustomGroupsValidatorTest {

    private OAuth2CognitoValidator validator;

    @BeforeEach
    public void setup() {
        validator = new OAuth2CognitoValidator(getTestAllowedGroupList(), getTestAllowedScopeList());
    }

    @Test
    public void getGroupsFromJwt_testCognitoGroups() {
        assertEquals(2, validator.getGroupsFromJwt(buildCognitoGroupsTestJwt()).size());
        assertEquals("[c_group_1, c_group_2]", validator.getGroupsFromJwt(buildCognitoGroupsTestJwt()).toString());
    }

    @Test
    public void getGroupsFromJwt_testEmpty() {
        assertTrue(validator.getGroupsFromJwt(buildWrongGroupsTestJwt()).isEmpty());
    }

    @Test
    public void validate_positiveTestCognitoGroup() {
        validator = new OAuth2CognitoValidator(List.of("c_group_1"), getTestAllowedScopeList());
        assertFalse(validator.validate(buildCognitoGroupsTestJwt()).hasErrors());
    }

    @Test
    public void validate_negativeTestCognitoGroup() {
        validator = new OAuth2CognitoValidator(List.of("c_qweqe"), getTestAllowedScopeList());
        assertTrue(validator.validate(buildCognitoGroupsTestJwt()).hasErrors());
        assertEquals(
            "[congnito_error] The required user's group is missing in profile or the scope",
            validator.validate(buildCognitoGroupsTestJwt()).getErrors().toArray()[0].toString()
        );
    }

    @Test
    public void validate_testWrongGroups() {
        assertTrue(validator.validate(buildWrongGroupsTestJwt()).hasErrors());
        assertEquals(
            "[congnito_error] The required user's group is missing in profile or the scope",
            validator.validate(buildWrongGroupsTestJwt()).getErrors().toArray()[0].toString()
        );
    }

    @Test
    public void shouldThrowExceptionWhenGroupsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new OAuth2CognitoValidator(null, getTestAllowedScopeList()));
    }

    @Test
    public void shouldThrowExceptionWhenScopesIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new OAuth2CognitoValidator(getTestAllowedGroupList(), null));
    }

    @Test
    public void shouldThrowExceptionWhenGroupListIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new OAuth2CognitoValidator(Collections.emptyList(), getTestAllowedScopeList()));
    }

    @Test
    public void shouldThrowExceptionWhenScopeListIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new OAuth2CognitoValidator(getTestAllowedGroupList(), Collections.emptyList()));
    }

    @Test
    public void whenScopeAllowedThenSuccess() {
        Map<String, Object> claims = Map.of("scope", "email profile scope_1", "clientId", "internal");
        Jwt jwt = JwtTestUtils.createJwtWithClaims(claims);

        final OAuth2TokenValidatorResult result = validator.validate(jwt);

        assertFalse(result.hasErrors());
    }

    @Test
    public void whenScopeAllowedNotPresentThenHasErrors() {
        Map<String, Object> claims = Map.of("scope", "email profile", "clientId", "internal");
        Jwt jwt = JwtTestUtils.createJwtWithClaims(claims);

        final OAuth2TokenValidatorResult result = validator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    @Test
    public void whenJwtIsNullThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
    }

    @Test
    public void whenNoScopesInJwtThenHasErrors() {
        Map<String, Object> claims = Map.of("scope", "", "clientId", "internal");
        Jwt jwt = JwtTestUtils.createJwtWithClaims(claims);

        final OAuth2TokenValidatorResult result = validator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    @Test
    public void whenScopesIsMissingFromJwtThenHasErrors() {
        Map<String, Object> claims = Map.of("clientId", "internal");
        Jwt jwt = JwtTestUtils.createJwtWithClaims(claims);

        final OAuth2TokenValidatorResult result = validator.validate(jwt);

        assertTrue(result.hasErrors());
    }

    private List<String> getTestAllowedGroupList() {
        return Arrays.asList("group_1", "group_2");
    }

    private List<String> getTestAllowedScopeList() {
        return Arrays.asList("scope_1", "scope_2");
    }

    private Jwt buildCognitoGroupsTestJwt() {
        Map<String, Object> claims = Map.of(
            "scope",
            "email profile",
            "clientId",
            "internal",
            "cognito:groups",
            Arrays.asList("c_group_1", "c_group_2")
        );
        return JwtTestUtils.createJwtWithClaims(claims);
    }

    private Jwt buildWrongGroupsTestJwt() {
        Map<String, Object> claims = Map.of(
            "scope",
            "email profile",
            "clientId",
            "internal",
            "some:groups",
            Arrays.asList("c_group_1", "c_group_2")
        );
        return JwtTestUtils.createJwtWithClaims(claims);
    }
}
