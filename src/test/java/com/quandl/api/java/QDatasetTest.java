package com.quandl.api.java;
/**
 * Basic behavior unit test, construct a QDataset and confirm
 * contents are as expected.
 */

import static org.testng.Assert.*;

import java.io.IOException;

import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public class QDatasetTest {
    // Good enough for now, should really use a date parsing library
    // http://www.regular-expressions.info/dates.html
    private static final String DATE_REGEX = "(19|20)\\d{2}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";
    private static final String TIMESTAMP_REGEX=DATE_REGEX+"T(0?\\d|1\\d|2[0-4]):(0?\\d|[1-5]\\d|60):(0?\\d|[1-5]\\d|60)Z";
    
    @Test
    public void parseDataset() throws IOException {
        QDataset dataset = new QDataset(Resources.toString(getClass().getClassLoader().getResource("PRAGUESE-PX.json"), Charsets.UTF_8));
        assertEquals(dataset.getId(), "2422996");
        assertEquals(dataset.getSourceCode(), "PRAGUESE");
        assertEquals(dataset.getCode(), "PX");
        assertEquals(dataset.getName(), "Prague Stock Index: PX");
        assertEquals(dataset.getUrlizeName(), "Prague-Stock-Index-PX");
        assertEquals(dataset.getDescription(), "Price index of blue chip issues. Base value: 1000 points. Start date: April 5, 1994. Number of base issues: variable.");
        assertEquals(dataset.getUpdatedAt(), "2013-07-22T17:12:06Z");
        assertPattern(dataset.getUpdatedAt(), TIMESTAMP_REGEX);
        assertEquals(dataset.getFrequency(), "daily");
        assertEquals(dataset.getFromDate(), "1993-09-07");
        assertEquals(dataset.getToDate(), "2013-07-22");
        assertPattern(dataset.getToDate(), DATE_REGEX);
        assertEquals(dataset.getColumnNames(), ImmutableList.of("Date", "Index", "% Change"));
        assertFalse(dataset.isPrivate());
        assertEquals(dataset.getErrors(), "{}");
        assertEquals(dataset.getDataset().size(), 44);
    }
    
    /*
     * Note this test is overly-brittle; it checks for specific
     * Quandl data, when really all we're trying to confirm is
     * that Quandl responded with data we could parse.
     * 
     * It would likely be good enough to simply get the QDataset
     * object back and confirm no errors, however presently
     * QDataset is too fault-tolerant.  As a first pass, I've
     * tidied up the test to limit it to data that "ought" to be
     * permanent.
     */
    @Test(groups="remote")
    public void testIntegration() {
        QDataset dataset = new QuandlConnection().getDatasetBetweenDates("PRAGUESE/PX","2012-09-30","2012-11-29");
        assertEquals(dataset.getId(), "2422996");
        assertEquals(dataset.getSourceCode(), "PRAGUESE");
        assertEquals(dataset.getCode(), "PX");
        assertFalse(dataset.getName().isEmpty());
        assertFalse(dataset.getUrlizeName().isEmpty());
        assertFalse(dataset.getDescription().isEmpty());
        assertPattern(dataset.getUpdatedAt(), TIMESTAMP_REGEX);
        assertFalse(dataset.getFrequency().isEmpty());
        assertPattern(dataset.getFromDate(), DATE_REGEX);
        assertPattern(dataset.getToDate(), DATE_REGEX);
        assertFalse(dataset.getColumnNames().isEmpty());
        assertFalse(dataset.isPrivate());
        assertEquals(dataset.getErrors(), "{}");
        assertFalse(dataset.getDataset().isEmpty());
    }
    
    private static void assertPattern(String actual, String pattern) {
        assertTrue(actual.matches(pattern), actual+" did not match pattern "+pattern);
    }
}
