package com.quandl.api.java;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Primary entry-point, this class manages connections to Quandl
 * and allows you to make requests for QDatasets.
 */
public class QuandlConnection {
    private static final String GOOD_TOKEN_URL = "http://www.quandl.com/api/v1/current_user/collections/datasets/favourites.json?auth_token=%s";
    private static final String BASE_URL = "http://www.quandl.com/api/v1/datasets/";
    
    private String token = null;

    // TODO provide factory methods .getLimitedConnection() and .getFullConnection(String token) instead of public constructors
    // TODO remove System.out calls
    public QuandlConnection() {
        System.out.println("No token... you are connected through the public api and will be rate limited accordingly.");
    }

    public QuandlConnection(String token) {

        if (connectedWithGoodToken(token)) {
            this.token = token;
        } else {
            System.out.println("Bad token... you are connected through the public api and will be rate limited accordingly.");
        }
    }
    
    private String withAuthToken(String url) {
        if(token != null) {
            return url + (url.contains("?") ? "&" : "?") + "auth_token=" + token;
        }
        return url;
    }

    public QDataset getDataset(String qCode) {
        return new QDataset(curl(withAuthToken(BASE_URL + qCode + ".json")));
    }

    public QDataset getDatasetBetweenDates(String qCode, String start, String end) {
        return new QDataset(curl(withAuthToken(BASE_URL + qCode + ".json?trim_start=" + start + "&trim_end=" + end)));
    }
    
    public QDataset getDatasetWithParams(String qCode, Map<String, String> params) {
        // A Guava MapJoiner would make this less painful
        StringBuilder paramSB = new StringBuilder("?");
        for (Entry<String,String> param : params.entrySet()) {
            paramSB.append(param.getKey()+"="+param.getValue()+"&");
        }
        paramSB.deleteCharAt(paramSB.length()-1);
        
        return new QDataset(curl(withAuthToken(BASE_URL + qCode + ".json" + paramSB)));
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
     */
    private static boolean connectedWithGoodToken(String token) {
        String output = curl(String.format(GOOD_TOKEN_URL, token));

        if (output.contains("Unauthorized")) {
            // TODO raise an exception; or even just let the API calls raise exceptions and remove this check entirely
            System.out.println("BAD TOKEN!!! Check your token under http://www.quandl.com/users/edit Click \"API\" and use the token specified");
            return false;
        }
        return true;
    }

    /**
     * This method just executes HTTP requests... putting the boilerplate code in one place.
     *
     * @param url this is the url for the http request... it assumes "http://" is already included.
     * @return it returns the response from the url in string form... or the message of the exception if one is thrown.
     */
    private static String curl(String url) {
        System.out.println("Executing Request: " + url);
        
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return httpclient.execute(httpget, responseHandler);
        } catch (IOException e) {
            // TODO raise exception, don't return message
            e.printStackTrace();
            return e.getMessage();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }
}
