package com.quandl.api.deprecated;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.quandl.api.java.QuandlConnection;
import com.quandl.api.util.LocalHttpController;

public class QuandlConnectionTest {
    private static final Function<String,String> lookups = new Function<String,String>() {
        @Override
        public String apply(String fullUrl) {
            return fullUrl.replace("http://www.quandl.com/api/v1/","");
        }};
    
    @Test
    public void checkConstructor() throws Exception {
        String out, err;
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            new QuandlConnection();
            out = tor.getCapturedOut();
            err = tor.getCapturedErr();
        }
        assertEquals(out.length(), 152);
        assertTrue(out.matches("(?s).*Warning.*deprecated.*No token.*"));
        assertEquals(err.length(), 0, "Was: "+err);
    }
    
    @Test(groups="remote")
    public void checkTokenConstructor() throws Exception {
        String out, err;
        try(TerminalOutRedirector tor = new TerminalOutRedirector()) {
            new QuandlConnection("dummy-key");
            out = tor.getCapturedOut();
            err = tor.getCapturedErr();
        }
        
        assertEquals(out.length(), 383);
        assertTrue(out.matches("(?s).*Warning.*deprecated.*Executing Request.*BAD TOKEN.*"));
        // err contains a stacktrace, so we can't easily confirm its length
        assertTrue(err.startsWith("org.apache.http.client.HttpResponseException: Unauthorized"));
    }
    
    // TODO add normal tests to this class, adjusted for deprecation behavior
}
