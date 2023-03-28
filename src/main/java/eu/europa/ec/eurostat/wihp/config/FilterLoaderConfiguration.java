package eu.europa.ec.eurostat.wihp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.service.navigation.NavigationFilterLoaderService;
import eu.europa.ec.eurostat.wihp.service.parse.ParseFilterLoaderService;
import eu.europa.ec.eurostat.wihp.service.url.UrlFilterLoaderService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class FilterLoaderConfiguration {

    private final ApplicationProperties applicationProperties;
    private final ObjectMapper mapper;

    public FilterLoaderConfiguration(ApplicationProperties applicationProperties, ObjectMapper mapper) {
        this.applicationProperties = applicationProperties;
        this.mapper = mapper;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UrlFilterLoaderService instantiateFilterConfigurationService() {
        return new UrlFilterLoaderService(applicationProperties, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ParseFilterLoaderService parseFilterLoaderService() {
        return new ParseFilterLoaderService(applicationProperties, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public NavigationFilterLoaderService navigationFilterLoaderService() {
        return new NavigationFilterLoaderService(applicationProperties, mapper);
    }
}
