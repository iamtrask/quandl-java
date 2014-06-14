package com.quandl.api.java.deprecated;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.quandl.api.java.QuandlConnection;
import com.quandl.api.java.util.LocalHttpController;

/**
 * Series of regression tests confirming the existing (1.1) behavior of QuandlConnection
 */
@Test(singleThreaded=true) // to try to avoid erroneously capturing each other's output
public class QuandlConnectionTest {
    private static final Function<String,String> LOOKUPS = new Function<String,String>() {
        @Override
        public String apply(String fullUrl) {
            return fullUrl.replace("http://www.quandl.com/api/v1/","");
        }};
    private static final Supplier<LocalHttpController> HTTP_SUP = LocalHttpController.supplier("deprecated."+QuandlConnectionTest.class.getSimpleName(), LOOKUPS);
    
    @Test
    public void checkConstructor() {
        String out, err;
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            new QuandlConnection();
            out = tor.getCapturedOut();
            err = tor.getCapturedErr();
        }
        assertTrue(out.matches("(?s).*Warning.*deprecated.*No token.*"));
        assertTrue(err.isEmpty(), "Was: "+err);
    }
    
    @Test(groups="remote")
    public void checkTokenConstructor() {
        String out, err;
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            new QuandlConnection("dummy-key");
            out = tor.getCapturedOut();
            err = tor.getCapturedErr();
        }
        
        assertTrue(out.matches("(?s).*Warning.*deprecated.*Executing Request.*BAD TOKEN.*"));
        assertTrue(err.startsWith("org.apache.http.client.HttpResponseException: Unauthorized"));
    }
    
    @DataProvider
    protected Object[][] datasets() {
        return new Object[][] {
            {"TEST",   "Executing Request: http://www.quandl.com/api/v1/datasets/TEST.json",   "^$"}
        };
    }
    
    @Test(dataProvider="datasets")
    // Stderr can have stack traces, which can change, so we just look for a pattern
    public void testGetDataset(String dataset, String outMatch, String errPat) {
        String out, err;
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDataset(dataset);
            out = tor.getCapturedOut();
            err = tor.getCapturedErr();
        }
        assertEquals(out.trim(), outMatch);
        assertTrue(err.matches(errPat));
    }
    
    @DataProvider
    protected Object[][] npeDatasets() {
        return new Object[][] {
            {"MISSING"}
        };
    }
    
    @Test(dataProvider="npeDatasets",expectedExceptions=NullPointerException.class)
    public void testNpeDatasets(String dataset) {
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDataset(dataset);
        }
    }
    
    @DataProvider
    protected Object[][] datasetsBetweenDates() {
        return new Object[][] {
            {"TEST", "Executing Request: http://www.quandl.com/api/v1/datasets/TEST.json?trim_start=2014-05-12&trim_end=2014-06-10", "^$"}
        };
    }
    
    @Test(dataProvider="datasetsBetweenDates")
    // Stderr can have stack traces, which can change, so we just look for a pattern
    public void testGetDatasetBetweenDates(String dataset, String outMatch, String errPat) {
        String out, err;
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetBetweenDates(dataset, "2014-05-12", "2014-06-10");
            out = tor.getCapturedOut();
            err = tor.getCapturedErr();
        }
        assertEquals(out.trim(), outMatch);
        assertTrue(err.matches(errPat));
    }

    @DataProvider
    protected Object[][] datasetsWithCodeAndParams() {
        return new Object[][] {
            {"TEST",ImmutableMap.of("column","1"),"Executing Request: http://www.quandl.com/api/v1/datasets/TEST.json?column=1","^$"},
            {"TEST",ImmutableMap.of("invalid","1"),"Executing Request: http://www.quandl.com/api/v1/datasets/TEST.json?invalid=1","^$"}
        };
    }
    
    @Test(dataProvider="datasetsWithCodeAndParams")
    public void testGetDatasetWithCodeAndParams(String dataset, Map<String,String> args, String outMatch, String errPat) {
        String out, err;
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetWithParams(dataset, args);
            out = tor.getCapturedOut();
            err = tor.getCapturedErr();
        }
        assertEquals(out.trim(), outMatch);
        assertTrue(err.matches(errPat), "Was: "+err);
    }
    
    @DataProvider
    protected Object[][] datasetsWithParams() {
        return new Object[][] {
            {ImmutableMap.of("source_code","TEST", "code","TEST"), "Executing Request: http://www.quandl.com/api/v1/datasets/TEST/TEST.json", ""},
            {ImmutableMap.of("source_code","TEST", "code","TEST", "column","1"), "Executing Request: http://www.quandl.com/api/v1/datasets/TEST/TEST.json?column=1", ""}
        };
    }
    
    @Test(dataProvider="datasetsWithParams")
    public void testGetDatasetWithParams(Map<String,String> args, String outMatch, String errPat) {
        String out, err;
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetWithParams(args);
            out = tor.getCapturedOut();
            err = tor.getCapturedErr();
        }
        assertEquals(out.trim(), outMatch);
        assertTrue(err.matches(errPat), "Was: "+err);
    }
    
    @DataProvider
    protected Object[][] iaeDatasetsWithParams() {
        return new Object[][] {
            {ImmutableMap.of()}
        };
    }
    
    @Test(dataProvider="iaeDatasetsWithParams",expectedExceptions=NullPointerException.class)
    public void testIaeDatasetsWithParams(Map<String,String> args) {
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetWithParams(args);
        }
    }
}
