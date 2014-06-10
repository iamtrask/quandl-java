package com.quandl.api.java.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;

import org.apache.http.client.HttpResponseException;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.quandl.api.java.util.HttpController;

public class LocalHttpController implements HttpController {
    private final String root;
    private final Function<String,String> lookup;
    
    public LocalHttpController(String rootPath) {
        this(rootPath, Functions.<String>identity());
    }
    
    public LocalHttpController(String rootPath, Function<String,String> lookupFunc) {
        root = checkNotNull(rootPath);
        lookup = checkNotNull(lookupFunc);
    }
    
    public static Supplier<LocalHttpController> supplier(final String rootPath, final Function<String,String> lookupFunc) {
        return new Supplier<LocalHttpController>() {
            @Override
            public LocalHttpController get() {
                return new LocalHttpController(rootPath, lookupFunc);
            }
        };
    }
    
    @Override
    public String getContents(String url) throws HttpResponseException {
        try {
            String path = root+"/"+lookup.apply(url);
            URL resource = getClass().getClassLoader().getResource(path);
            if(resource == null) {
                throw new HttpResponseException(404, String.format("%s: No local resource %s found for URL %s", getClass().getName(), path, url));
            }
            return Resources.toString(resource, Charsets.UTF_8);
        } catch (HttpResponseException e) {
            throw e; // Since HRE is an IOE
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void close() { /* no-op */ }

}
