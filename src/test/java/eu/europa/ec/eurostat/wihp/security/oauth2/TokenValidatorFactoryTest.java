package eu.europa.ec.eurostat.wihp.security.oauth2;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanInitializationException;
import tech.jhipster.config.JHipsterProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenValidatorFactoryTest {

    @Test
    public void buildTokenValidators_test() {
        TokenValidatorFactory factory = buildValidatorFactory(
            Arrays.asList("aud_1", "aud_2"),
            Arrays.asList("gr_1", "gr_2"),
            Arrays.asList("sc_1", "sc_2")
        );
        assertNotNull(factory.buildTokenValidators());
    }

    @Test
    public void buildTokenValidators_testEmptyAud() {
        TokenValidatorFactory factory = buildValidatorFactory(
            Collections.emptyList(),
            Arrays.asList("gr_1", "gr_2"),
            Arrays.asList("sc_1", "sc_2")
        );
        assertNotNull(factory.buildTokenValidators());
    }

    @Test
    public void buildTokenValidators_testOauthGroups() {
        TokenValidatorFactory factory = buildValidatorFactory(
            Arrays.asList("aud_1", "aud_2"),
            Collections.emptyList(),
            Collections.emptyList()
        );
        assertNotNull(factory.buildTokenValidators());
    }

    @Test
    public void buildTokenValidators_allEmpty() {
        TokenValidatorFactory factory = buildValidatorFactory(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        assertThrows(BeanInitializationException.class, factory::buildTokenValidators);
    }

    @Test
    public void buildTokenValidators_emptyAudAndOauthGroups() {
        TokenValidatorFactory factory = buildValidatorFactory(
            Collections.emptyList(),
            Collections.emptyList(),
            Arrays.asList("scope1", "scope2")
        );
        assertThrows(BeanInitializationException.class, factory::buildTokenValidators);
    }

    @Test
    public void buildTokenValidators_emptyAudAndOauthScopes() {
        TokenValidatorFactory factory = buildValidatorFactory(
            Collections.emptyList(),
            Arrays.asList("gr1", "gr2"),
            Collections.emptyList()
        );

        assertThrows(BeanInitializationException.class, factory::buildTokenValidators);
    }

    public TokenValidatorFactory buildValidatorFactory(
        final List<String> audiences,
        final List<String> oauthGroups,
        final List<String> scopes
    ) {
        TokenValidatorFactory factory;
        JHipsterProperties hipsterProp = mock(JHipsterProperties.class);
        JHipsterProperties.Security security = mock(JHipsterProperties.Security.class);
        JHipsterProperties.Security.OAuth2 oauth = new JHipsterProperties.Security.OAuth2();
        oauth.setAudience(audiences);
        when(security.getOauth2()).thenReturn(oauth);
        when(hipsterProp.getSecurity()).thenReturn(security);
        factory = new TokenValidatorFactory(hipsterProp, getApplicationProperties(oauthGroups, scopes));
        factory.issuerUri = "issuer_uri";
        return factory;
    }

    private ApplicationProperties getApplicationProperties(final List<String> oauthGroups, final List<String> scopes) {
        ApplicationProperties appProp = new ApplicationProperties();
        appProp.setOauth2Groups(oauthGroups);
        appProp.setOauth2Scopes(scopes);
        return appProp;
    }
}
