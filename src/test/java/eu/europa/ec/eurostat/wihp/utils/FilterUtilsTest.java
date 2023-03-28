package eu.europa.ec.eurostat.wihp.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FilterUtilsTest {

    @Test
    public void validPackageNames() {
        assertTrue(FilterUtils.isPackageNameValid("eu.europa.ec.eurostat.wihp"));
    }

    @Test
    public void invalidPackageNames() {
        assertFalse(FilterUtils.isPackageNameValid("eu+europa+ec+eurostat+wihp"));
        assertFalse(FilterUtils.isPackageNameValid("eu@europa@ec@eurostat@wihp"));
        assertFalse(FilterUtils.isPackageNameValid("eu/europa/ec/eurostat/wihp"));
    }
}
