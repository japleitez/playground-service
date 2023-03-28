package eu.europa.ec.eurostat.wihp.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FilterConfigFactory {

    private final List<FilterConfigService> serviceList;

    public FilterConfigFactory(final List<FilterConfigService> serviceList) {
        this.serviceList = serviceList;
    }

    public FilterConfigService getFilterConfigService(final FilterType type) {
        return serviceList.stream().filter(p -> type.equals(p.getType())).findFirst().orElseThrow();
    }
}
