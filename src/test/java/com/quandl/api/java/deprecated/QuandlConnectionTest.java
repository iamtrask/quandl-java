package com.quandl.api.java.deprecated;

import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.quandl.api.java.QDataset;
import com.quandl.api.java.QuandlConnection;
import com.quandl.api.java.util.LocalHttpController;

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
    
    // TODO implement regression tests
    @Test
    public void testGetDataset() {
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDataset("TEST");
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDataset("MISSING");
        }
    }
    
    @Test
    public void testGetDatasetBetweenDates() {
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetBetweenDates("TEST", "2000-1-1", "2000-2-1");
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetBetweenDates("MISSING", "2000-1-1", "2000-2-1");
        }
    }
    
    @Test
    public void testGetDatasetWithCodeAndParams() {
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetWithParams("TEST", ImmutableMap.of("column","1"));
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetWithParams("TEST", ImmutableMap.of("invalid","1"));
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetWithParams("MISSING", ImmutableMap.of("column","1"));
        }
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testGetDatasetWithParams() {
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetWithParams(ImmutableMap.of("column","1"));
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetWithParams(ImmutableMap.of("invalid","1"));
            com.quandl.api.java.QuandlConnectionTest.getConnection(null, HTTP_SUP).getDatasetWithParams(ImmutableMap.of("column","1"));
        }
    }
}
