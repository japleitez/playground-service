package eu.europa.ec.eurostat.wihp.security.oauth2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

public class OAuth2CognitoValidator implements OAuth2TokenValidator<Jwt> {

    private final List<String> allowedGroupList;

    private final List<String> allowedScopeList;

    private static final String SCOPE = "scope";

    private static final OAuth2Error COGNITO_ERROR = new OAuth2Error(
        "congnito_error",
        "The required user's group is missing in profile or the scope",
        null
    );

    public OAuth2CognitoValidator(List<String> allowedGroupList, final List<String> allowedScopeList) {
        notEmpty(allowedGroupList, "Groups should not be null");
        notEmpty(allowedScopeList, "Scopes should not be null");
        this.allowedGroupList = allowedGroupList;
        this.allowedScopeList = allowedScopeList;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        notNull(jwt, "Token is mandatory");

        Collection<String> availableGroups = getGroupsFromJwt(jwt);
        for (String customGroup : allowedGroupList) {
            if (availableGroups.contains(customGroup)) {
                return OAuth2TokenValidatorResult.success();
            }
        }
        final String scope = jwt.getClaimAsString(SCOPE);
        if (StringUtils.isEmpty(scope)) {
            return OAuth2TokenValidatorResult.failure(COGNITO_ERROR);
        }

        return this.allowedScopeList.stream()
            .filter(allowedScope -> Arrays.asList(scope.split(SPACE)).contains(allowedScope))
            .findFirst()
            .map(allowedScope -> OAuth2TokenValidatorResult.success())
            .orElse(OAuth2TokenValidatorResult.failure(COGNITO_ERROR));
    }

    /**
     * Return group list from jwt token.
     * Groups will be extracted from token by 'cognito:groups' for the access_token
     * If groups are not defined the empty list will be return.
     *
     * @param jwt - jwt in the request
     * @return collection of groups or empty collection
     */
    protected Collection<String> getGroupsFromJwt(Jwt jwt) {
        if (null != jwt.getClaims().get("cognito:groups")) {
            return (Collection<String>) jwt.getClaims().get("cognito:groups");
        }
        return Collections.emptyList();
    }
}
