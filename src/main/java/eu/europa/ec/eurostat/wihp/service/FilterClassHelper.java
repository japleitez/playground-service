package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.exceptions.NotFoundAlertException;
import eu.europa.ec.eurostat.wihp.exceptions.UnprocessableEntityException;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterClassHelper<T> {

    private final Class<T> tClass;
    private static final String SERVER_ERROR = "serverError";

    public FilterClassHelper(Class<T> tClass) {
        this.tClass = tClass;
    }

    public T instantiateFilterByName(String name) {
        Class<T> clazz;
        try {
            clazz = (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new NotFoundAlertException("Filter not found: ".concat(name), "FilterLoaderService", "idInvalid");
        }
        return instantiateFilterByClass(clazz);
    }

    public Set<T> instantiateAllFilters(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes);
        return reflections.getSubTypesOf(tClass).stream().map(this::instantiateFilterByClass).collect(Collectors.toSet());
    }

    private T instantiateFilterByClass(Class<? extends T> clazz) {
        try {
            Constructor<?> cons = clazz.getConstructor();
            return (T) cons.newInstance();
        } catch (InvocationTargetException e) {
            throw new UnprocessableEntityException("Filter Not found: " + e.getMessage(), "", SERVER_ERROR);
        } catch (InstantiationException e) {
            throw new UnprocessableEntityException("Class cannot be instantiated: " + e.getMessage(), "", SERVER_ERROR);
        } catch (IllegalAccessException e) {
            throw new UnprocessableEntityException("Method Cannot be accessed: " + e.getMessage(), "", SERVER_ERROR);
        } catch (NoSuchMethodException e) {
            throw new UnprocessableEntityException("Method not present: " + e.getMessage(), "", SERVER_ERROR);
        }
    }

}
