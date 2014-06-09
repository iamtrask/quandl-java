package com.quandl.api.java;

import static com.google.common.base.Preconditions.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.quandl.api.java.query.MetadataQuery;
import com.quandl.api.java.query.MultisetQuery;
import com.quandl.api.java.query.Queries;
import com.quandl.api.java.query.Query;
import com.quandl.api.java.query.SimpleQuery;
import com.quandl.api.util.HttpController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Primary entry-point, this class manages connections to Quandl
 * and allows you to make requests for QDatasets.
 */
public class QuandlConnection implements AutoCloseable {
    private static final String GOOD_TOKEN_URL = "http://www.quandl.com/api/v1/current_user/collections/datasets/favourites.json?auth_token=%s";
    private static final String BASE_URL = "http://www.quandl.com/api/v1/datasets/%s.json";
    private static final MapJoiner URL_ARG_JOINER = Joiner.on('&').withKeyValueSeparator("=");
    
    private final String token;
    private final Supplier<HttpController> httpSup;
    
    /**
     * Returns a rate-limited QuandlConnection not tied to any API key. 
     */
    public static QuandlConnection getLimitedConnection() {
        return new QuandlConnection(null, HttpController.Real.SUPPLIER);
    }
    
    /**
     * Returns a QuandlConnection without rate-limiting, based on a valid API key.
     * This call triggers a request to validate the key. 
     */
    public static QuandlConnection getFullConnection(String token) throws InvalidTokenException {
        return new QuandlConnection(checkGoodToken(token, HttpController.Real.SUPPLIER), HttpController.Real.SUPPLIER);
    }

    /**
     * @deprecated use getLimitedConnection()
     */
    @Deprecated
    public QuandlConnection() {
        System.out.println("Warning, accessing deprecated QuandlConnection constructor");
        System.out.println("No token... you are connected through the public api and will be rate limited accordingly.");
        token = null;
        httpSup = HttpController.Real.SUPPLIER;
    }

    /**
     * @deprecated use getFullConnection()
     */
    @Deprecated
    public QuandlConnection(String token) {
        System.out.println("Warning, accessing deprecated QuandlConnection constructor");
        if (connectedWithGoodToken(token)) {
            this.token = token;
        } else {
            System.out.println("Bad token... you are connected through the public api and will be rate limited accordingly.");
            this.token = null;
        }
        httpSup = HttpController.Real.SUPPLIER;
    }
    
    // we use a separate, package-private constructor to avoid the printing constructors
    /*package*/ QuandlConnection(String token, Supplier<HttpController> httpSup) {
        // FIXME No checkNotNull() calls for 1.2, 1.3 should explicitly fail
        this.token = token; 
        this.httpSup = checkNotNull(httpSup);
    }
    
    private String withAuthToken(String url) {
        if(token != null) {
            return url + (url.contains("?") ? "&" : "?") + "auth_token=" + token;
        }
        return url;
    }
    
    /**
     * Returns a QDataset view of a given Quandle Dataset built from the passed query.
     */
    public QDataset getDataset(SimpleQuery sq) {
        // FIXME map is not URL-encoded
        String args = URL_ARG_JOINER.join(sq.getParameterMap());
        if(!args.isEmpty()) {
            args = "?"+args;
        }
        String url = withAuthToken(String.format(BASE_URL,sq.getQCode())+args);
        // TODO v1.3 Move httpCont to instance variable
        try (HttpController httpCont = httpSup.get()) {
            return new QDataset(httpCont.getContents(url));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
    
    /**
     * Returns a QDataset view of a given Quandle Multiset build from the passed query.
     */
    public QDataset getDataset(@SuppressWarnings("unused") MultisetQuery mq) {
        // TODO
        throw new UnsupportedOperationException("Unimplemented.");
    }
    
    /**
     * Returns a QDataset (with no data) of a given Guandle Dataset, in order to inspect the dataset's metadata.
     */
    // TODO should this return a different type, e.g. QMetadata?
    public QDataset getDataset(@SuppressWarnings("unused") MetadataQuery mdq) {
        // TODO
        throw new UnsupportedOperationException("Unimplemented.");
    }

    /**
     * Rollup method takes a given Query and returns an appropriate QDataset.
     * 
     * The idea here is to use method overloading to call the correct sub-method,
     * but at first glance this doesn't seem to work as desired.  Needs to be tested.
     */
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

    /**
     * Returns a QDataset view of a given Quandle Dataset from the passed QCode.
     */
    public QDataset getDataset(String qCode) {
        // TODO return getDataset(Queries.create(qCode));
        return new QDataset(curl(withAuthToken(String.format(BASE_URL,qCode))));
    }

    /**
     * Returns a QDataset view of a given Quandle Dataset from the passed QCode and start and end dates
     * @deprecated use the Query builder pattern to set the dates you wish to filter by
     */
    @Deprecated
    public QDataset getDatasetBetweenDates(String qCode, String start, String end) {
        return new QDataset(curl(withAuthToken(String.format(BASE_URL,qCode) + "?trim_start=" + start + "&trim_end=" + end)));
    }
    
    /**
     * Returns a QDataset view of a given Quandl Dataset from the passed QCode and arbitrary parameters
     * @deprecated use the Query builder pattern to describe the query you wish to make against the API
     */
    @Deprecated
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
    
    private static String checkGoodToken(String token, Supplier<HttpController> httpSup) throws InvalidTokenException {
        // TODO v1.3 Move httpCont to instance variable
        try (HttpController httpCont = httpSup.get()) {
            httpCont.getContents(String.format(GOOD_TOKEN_URL, token));
            return token;
        } catch (HttpResponseException e) {
            throw new InvalidTokenException(token, e);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    /** @deprecated use HttpController */
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

    @Override
    public void close() {
        // TODO empty for v1.2, pull the HttpClient construction out of getPageText() and make it a member of the class for v1.3 
    }
}
