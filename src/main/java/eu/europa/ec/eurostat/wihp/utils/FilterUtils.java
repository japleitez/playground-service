package eu.europa.ec.eurostat.wihp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.util.Strings;

public class FilterUtils {

    private static final String PACKAGE_PATTERN = "([a-zA-Z_$][a-zA-Z\\d_$]*+\\.)*+[a-zA-Z_$][a-zA-Z\\d_$]*+";

    private FilterUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isPackageNameValid(String packageName) {
        if (Strings.isBlank(packageName)) {
            return false;
        }
        Pattern pattern = Pattern.compile(PACKAGE_PATTERN);
        Matcher matcher = pattern.matcher(packageName);
        return matcher.matches();
    }
}
