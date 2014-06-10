package com.quandl.api.java;

import org.testng.annotations.Test;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.quandl.api.java.util.LocalHttpController;

public class QuandlConnectionTest {
    private static final Function<String,String> lookups = new Function<String,String>() {
        @Override
        public String apply(String fullUrl) {
            return fullUrl.replace("http://www.quandl.com/api/v1/","");
        }};
    
    //@Test
    public void tryConnect() {
        new QuandlConnection(null, LocalHttpController.supplier(getClass().getSimpleName(), lookups)).getDataset("Hello");
    }
    
    // Needed for deprecated package to construct test QuandlConnection instances
    @VisibleForTesting
    public static QuandlConnection getConnection(String token, Supplier<LocalHttpController> httpSup) {
        return new QuandlConnection(token, httpSup);
    }
}
