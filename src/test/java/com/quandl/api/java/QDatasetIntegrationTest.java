package com.quandl.api.java;
/**
 * Basic connection integration test, hits Quandl's public
 * API and confirms response is structured as expected.
 * 
 * Note this test can trigger false failures, particularly
 * if you're attempting to build the project while offline.
 * It's safe to disable this test if it's failing and you
 * have no reason to believe you've caused the failure.
 */

import static org.junit.Assert.*;

import org.junit.Test;

public class QDatasetIntegrationTest {
    /*
     * Note this test is overly-brittle; it checks for specific
     * Quandl data, when really all we're trying to confirm is
     * that Quandl responded with data we could parse.
     * 
     * It would likely be good enough to simply get the QDataset
     * object back and confirm no errors.  As a first pass, I've
     * tidied up the test to limit it to data that "ought" to be
     * permanent.
     */
    @Test
    public void testIntegration() {
        QDataset dataset = new QuandlConnection().getDatasetBetweenDates("PRAGUESE/PX","2012-09-30","2012-11-29");
        assertEquals("2422996", dataset.getId());
        assertEquals("PRAGUESE", dataset.getSourceCode());
        assertEquals("PX", dataset.getCode());
        assertFalse(dataset.getName().isEmpty());
        assertFalse(dataset.getUrlizeName().isEmpty());
        assertFalse(dataset.getDescription().isEmpty());
        assertFalse(dataset.getUpdatedAt().isEmpty()); // could validate string is parse-able as a time
        assertFalse(dataset.getFrequency().isEmpty());
        assertFalse(dataset.getFromDate().isEmpty()); // could validate string is parse-able as a date
        assertFalse(dataset.getToDate().isEmpty()); // could validate string is parse-able as a date
        assertFalse(dataset.getColumnNames().isEmpty());
        assertFalse(dataset.isPrivate());
        assertEquals("{}", dataset.getErrors());
        assertFalse(dataset.getDataset().isEmpty());
    }
}
