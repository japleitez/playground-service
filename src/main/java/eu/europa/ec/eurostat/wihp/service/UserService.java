package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.config.Constants;
import eu.europa.ec.eurostat.wihp.service.dto.AdminUserDTO;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private static final String DETAILS_SUB = "sub";
    private static final String DETAILS_PREFERRED_USERNAME = "preferred_username";
    private static final String DETAILS_UID = "uid";
    private static final String DETAILS_GIVEN_NAME = "given_name";
    private static final String DETAILS_NAME = "name";
    private static final String DETAILS_FAMILY_NAME = "family_name";
    private static final String DETAILS_EMAIL_VERIFIED = "email_verified";
    private static final String DETAILS_EMAIL = "email";
    private static final String DETAILS_LANG_KEY = "langKey";
    private static final String DETAILS_LOCALE = "locale";
    private static final String DETAILS_PICTURE = "picture";

    /**
     * Returns the user from an OAuth 2.0 login or resource server with JWT.
     *
     * @param authToken the authentication token.
     * @return the user from the authentication.
     */
    public AdminUserDTO getUserFromAuthentication(AbstractAuthenticationToken authToken) {
        Map<String, Object> attributes;
        if (authToken instanceof OAuth2AuthenticationToken) {
            attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();
        } else if (authToken instanceof JwtAuthenticationToken) {
            attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
        } else {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
        }
        AdminUserDTO user = getUser(attributes);
        user.setAuthorities(authToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));

        return user;
    }

    private static AdminUserDTO getUser(Map<String, Object> details) {
        AdminUserDTO user = new AdminUserDTO();
        Boolean activated = Boolean.TRUE;
        String sub = String.valueOf(details.get(DETAILS_SUB));
        String username = null;
        if (details.get(DETAILS_PREFERRED_USERNAME) != null) {
            username = ((String) details.get(DETAILS_PREFERRED_USERNAME)).toLowerCase();
        }
        // handle resource server JWT, where sub claim is email and uid is ID
        if (details.get(DETAILS_UID) != null) {
            user.setId((String) details.get(DETAILS_UID));
            user.setLogin(sub);
        } else {
            user.setId(sub);
        }
        if (username != null) {
            user.setLogin(username);
        } else if (user.getLogin() == null) {
            user.setLogin(user.getId());
        }
        if (details.get(DETAILS_GIVEN_NAME) != null) {
            user.setFirstName((String) details.get(DETAILS_GIVEN_NAME));
        } else if (details.get(DETAILS_NAME) != null) {
            user.setFirstName((String) details.get(DETAILS_NAME));
        }
        if (details.get(DETAILS_FAMILY_NAME) != null) {
            user.setLastName((String) details.get(DETAILS_FAMILY_NAME));
        }
        if (details.get(DETAILS_EMAIL_VERIFIED) != null) {
            activated = (Boolean) details.get(DETAILS_EMAIL_VERIFIED);
        }
        email(details, user, sub, username);
        language(details, user);
        if (details.get(DETAILS_PICTURE) != null) {
            user.setImageUrl((String) details.get(DETAILS_PICTURE));
        }
        user.setActivated(activated);
        return user;
    }

    private static void email(final Map<String, Object> details, final AdminUserDTO user, final String sub, final String username) {
        if (details.get(DETAILS_EMAIL) != null) {
            user.setEmail(((String) details.get(DETAILS_EMAIL)).toLowerCase());
        } else if (sub.contains("|") && (username != null && username.contains("@"))) {
            // special handling for Auth0
            user.setEmail(username);
        } else {
            user.setEmail(sub);
        }
    }

    private static void language(final Map<String, Object> details, final AdminUserDTO user) {
        if (details.get(DETAILS_LANG_KEY) != null) {
            user.setLangKey((String) details.get(DETAILS_LANG_KEY));
        } else if (details.get(DETAILS_LOCALE) != null) {
            // trim off country code if it exists
            String locale = (String) details.get(DETAILS_LOCALE);
            if (locale.contains("_")) {
                locale = locale.substring(0, locale.indexOf('_'));
            } else if (locale.contains("-")) {
                locale = locale.substring(0, locale.indexOf('-'));
            }
            user.setLangKey(locale.toLowerCase());
        } else {
            // set langKey to default if not specified by IdP
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
        }
    }
}
