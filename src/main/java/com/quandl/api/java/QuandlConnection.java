package com.quandl.api.java;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Throwables;
import com.quandl.api.java.query.MetadataQuery;
import com.quandl.api.java.query.MultisetQuery;
import com.quandl.api.java.query.Query;
import com.quandl.api.java.query.SimpleQuery;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Primary entry-point, this class manages connections to Quandl
 * and allows you to make requests for QDatasets.
 */
public class QuandlConnection implements AutoCloseable {
    private static final ResponseHandler<String> RESPONSE_HANDLER = new BasicResponseHandler();
    private static final String GOOD_TOKEN_URL = "http://www.quandl.com/api/v1/current_user/collections/datasets/favourites.json?auth_token=%s";
    private static final String BASE_URL = "http://www.quandl.com/api/v1/datasets/%s.json";
    private static final MapJoiner URL_ARG_JOINER = Joiner.on('&').withKeyValueSeparator("=");
    
    private String token = null;
    
    public static QuandlConnection getLimitedConnection() {
        return new QuandlConnection(null, false);
    }
    
    public static QuandlConnection getFullConnection(String token) throws InvalidTokenException {
        return new QuandlConnection(checkGoodToken(token), false);
    }

    @Deprecated
    public QuandlConnection() {
        System.out.println("Warning, accessing deprecated QuandlConnection constructor");
        System.out.println("No token... you are connected through the public api and will be rate limited accordingly.");
    }

    @Deprecated
    public QuandlConnection(String token) {
        System.out.println("Warning, accessing deprecated QuandlConnection constructor");
        if (connectedWithGoodToken(token)) {
            this.token = token;
        } else {
            System.out.println("Bad token... you are connected through the public api and will be rate limited accordingly.");
        }
    }
    
    // we use a separate, private constructor to avoid the printing constructors
    private QuandlConnection(String token, @SuppressWarnings("unused") boolean dummy) {
        this.token = token; 
    }
    
    private String withAuthToken(String url) {
        if(token != null) {
            return url + (url.contains("?") ? "&" : "?") + "auth_token=" + token;
        }
        return url;
    }
    
    public QDataset getDataset(SimpleQuery sq) {
        // FIXME map is not URL-encoded
        String args = URL_ARG_JOINER.join(sq.getParameterMap());
        if(!args.isEmpty()) {
            args = "?"+args;
        }
        String url = withAuthToken(String.format(BASE_URL,sq.getQCode())+args);
        try {
            return new QDataset(getPageText(url));
        } catch (HttpResponseException e) {
            throw Throwables.propagate(e);
        }
    }
    
    public QDataset getDataset(@SuppressWarnings("unused") MultisetQuery mq) {
        // TODO
        throw new UnsupportedOperationException("Unimplemented.");
    }
    
    public QDataset getDataset(@SuppressWarnings("unused") MetadataQuery mdq) {
        // TODO
        throw new UnsupportedOperationException("Unimplemented.");
    }

    public QDataset getDataset(Query unknownQuery) {
        // Shouldn't need these instance of checks...
        if(unknownQuery instanceof SimpleQuery) {
            return getDataset((SimpleQuery)unknownQuery);
        }
        if(unknownQuery instanceof MultisetQuery) {
            return getDataset((MultisetQuery)unknownQuery);
        }
        if(unknownQuery instanceof MetadataQuery) {
            return getDataset((MetadataQuery)unknownQuery);
        }
        throw new UnsupportedOperationException("Unable to process "+unknownQuery.getClass().getName()+" - incomplete API?");
    }

    public QDataset getDataset(String qCode) {
        return new QDataset(curl(withAuthToken(String.format(BASE_URL,qCode))));
    }

    public QDataset getDatasetBetweenDates(String qCode, String start, String end) {
        return new QDataset(curl(withAuthToken(String.format(BASE_URL,qCode) + "?trim_start=" + start + "&trim_end=" + end)));
    }
    
    public QDataset getDatasetWithParams(String qCode, Map<String, String> params) {
        // A Guava MapJoiner would make this less painful
        StringBuilder paramSB = new StringBuilder("?");
        for (Entry<String,String> param : params.entrySet()) {
            paramSB.append(param.getKey()+"="+param.getValue()+"&");
        }
        paramSB.deleteCharAt(paramSB.length()-1);
        
        return new QDataset(curl(withAuthToken(String.format(BASE_URL,qCode) + paramSB)));
    }
    
    @Deprecated
    public QDataset getDatasetWithParams(Map<String, String> params) {
        Map<String,String> paramsCopy = new HashMap<>(params);
        String sourceCode = paramsCopy.remove("source_code");
        String code = paramsCopy.remove("code");
        if(sourceCode == null || code == null) {
            throw new IllegalArgumentException("getDatasetWithParams(Map) requires both source_code and code entries");
        }
        return getDatasetWithParams(sourceCode+"/"+code, paramsCopy);
    }

    /**
     * This method uses the "favorites" url to check that the provided token is valid.
     *
     * @param token this is the security token for your quandl account.
     * @return true or false... depending on whether or not the token is valid.
     * @deprecated prints on failure
     */
    @Deprecated
    private static boolean connectedWithGoodToken(String token) {
        String output = curl(String.format(GOOD_TOKEN_URL, token));

        if (output.contains("Unauthorized")) {
            // TODO raise an exception; or even just let the API calls raise exceptions and remove this check entirely
            System.out.println("BAD TOKEN!!! Check your token under http://www.quandl.com/users/edit Click \"API\" and use the token specified");
            return false;
        }
        return true;
    }
    
    private static String checkGoodToken(String token) throws InvalidTokenException {
        try {
            getPageText(String.format(GOOD_TOKEN_URL, token));
            return token;
        } catch (HttpResponseException e) {
            throw new InvalidTokenException(token, e);
        }
    }

    /** @deprecated use getPageText */
    @Deprecated
    private static String curl(String url) {
        System.out.println("Executing Request: " + url);
        
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return httpclient.execute(httpget, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }
    
    /**
     * Execute HTTP requests, raising exceptions on bad response codes.  Returns
     * the page contents as a String.
     */
    // TODO do we need to load the page into memory?  Can we stream through it instead?
    private static String getPageText(String url) throws HttpResponseException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            return client.execute(get, RESPONSE_HANDLER);
        } catch (HttpResponseException e) {
            throw e;
        } catch (IOException e) {
            // It may be worth forcing callers to properly handle IOExceptions as well
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void close() {
        // TODO empty for 1.2, pull the HttpClient construction out of getPageText() and make it a member of the class for 1.3 
    }
}
