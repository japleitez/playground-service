package eu.europa.ec.eurostat.wihp.security.oauth2;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.jhipster.config.JHipsterProperties;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class TokenValidatorFactory {

    private static final Logger log = LoggerFactory.getLogger(TokenValidatorFactory.class);

    private final JHipsterProperties jHipsterProperties;

    private final List<String> allowedGroupList;

    private final List<String> allowedScopeList;

    @Value("${spring.security.oauth2.client.provider.oidc.issuer-uri}")
    String issuerUri;

    @Autowired
    public TokenValidatorFactory(JHipsterProperties jHipsterProperties, ApplicationProperties applicationProperties) {
        this.jHipsterProperties = jHipsterProperties;
        this.allowedGroupList = applicationProperties.getOauth2Groups();
        this.allowedScopeList = applicationProperties.getOauth2Scopes();
    }

    /**
     * Keycloak has audience and AudienceValidator will be used to validate token.
     * Cognito doesn't support audience but has groups in the token. In this case the OAuth2CustomGroupsValidator will be used, and AudienceValidator will not be involved.
     * audience or allowedGroupList should be defined in the configuration (see audience and oauth2-groups). If audience is defined, then AudienceValidator will be used and
     * OAuth2CustomGroupsValidator will be ignored.
     * <p>
     * If the both lists audience and oauth2-groups are empty then runtime exception BeanInitializationException will be thrown.
     *
     * @return DelegatingOAuth2TokenValidator  validators
     */
    public OAuth2TokenValidator<Jwt> buildTokenValidators() {
        List<String> audience = jHipsterProperties.getSecurity().getOauth2().getAudience();
        if (isEmpty(audience) && isEmpty(allowedGroupList) && isEmpty(allowedScopeList)) {
            log.error(
                "audience, oauth2-groups and scope are empty or null in the configuration. ( see oauth2-groups or audience or scope) "
            );
            throw new BeanInitializationException(
                "audience, oauth2-groups and scope are empty or null in the configuration. ( see oauth2-groups or audience or scope ) "
            );
        }
        if (!CollectionUtils.isEmpty(audience)) {
            return new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuerUri), new AudienceValidator(audience));
        } else if (!CollectionUtils.isEmpty(allowedGroupList) && !CollectionUtils.isEmpty(allowedScopeList)) {
            return new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuerUri),
                new OAuth2CognitoValidator(allowedGroupList, allowedScopeList)
            );
        }
        throw new BeanInitializationException(
            "audience, oauth2-groups and scope are empty or null in the configuration. ( see oauth2-groups or audience or scope) "
        );
    }
}
