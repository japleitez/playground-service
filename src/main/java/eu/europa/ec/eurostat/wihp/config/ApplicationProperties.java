package eu.europa.ec.eurostat.wihp.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to PlaygroundService.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private List<String> oauth2Groups;
    private List<String> oauth2Scopes;
    private String filtersPackage;
    private String filtersPropertyUrl;
    private String filtersPropertyUrls;
    private String filtersPropertyResponse;
    private String filtersPropertyMetadata;
    private String seleniumAddress;
    private String userAgentDetails;
    private String parseFilterPackage;

    private String navigationFilterPackage;

    public List<String> getOauth2Groups() {
        return oauth2Groups;
    }

    public void setOauth2Groups(List<String> oauth2Groups) {
        this.oauth2Groups = oauth2Groups;
    }

    public List<String> getOauth2Scopes() {
        return oauth2Scopes;
    }

    public void setOauth2Scopes(final List<String> oauth2Scopes) {
        this.oauth2Scopes = oauth2Scopes;
    }

    public String getFiltersPackage() {
        return filtersPackage;
    }

    public void setFiltersPackage(String filtersPackage) {
        this.filtersPackage = filtersPackage;
    }

    public String getParseFilterPackage() {
        return parseFilterPackage;
    }

    public void setParseFilterPackage(String parseFilterPackage) {
        this.parseFilterPackage = parseFilterPackage;
    }

    public String getFiltersPropertyUrl() {
        return filtersPropertyUrl;
    }

    public void setFiltersPropertyUrl(String filtersPropertyUrl) {
        this.filtersPropertyUrl = filtersPropertyUrl;
    }

    public String getFiltersPropertyUrls() {
        return filtersPropertyUrls;
    }

    public void setFiltersPropertyUrls(String filtersPropertyUrls) {
        this.filtersPropertyUrls = filtersPropertyUrls;
    }

    public String getFiltersPropertyResponse() {
        return filtersPropertyResponse;
    }

    public void setFiltersPropertyResponse(String filtersPropertyResponse) {
        this.filtersPropertyResponse = filtersPropertyResponse;
    }

    public String getFiltersPropertyMetadata() {
        return filtersPropertyMetadata;
    }

    public void setFiltersPropertyMetadata(String filtersPropertyMetadata) {
        this.filtersPropertyMetadata = filtersPropertyMetadata;
    }

    public String getSeleniumAddress() {
        return seleniumAddress;
    }

    public void setSeleniumAddress(final String seleniumAddress) {
        this.seleniumAddress = seleniumAddress;
    }

    public String getUserAgentDetails() {
        return userAgentDetails;
    }

    public void setUserAgentDetails(final String userAgentDetails) {
        this.userAgentDetails = userAgentDetails;
    }

    public String getNavigationFilterPackage() {
        return navigationFilterPackage;
    }

    public void setNavigationFilterPackage(String navigationFilterPackage) {
        this.navigationFilterPackage = navigationFilterPackage;
    }
}
