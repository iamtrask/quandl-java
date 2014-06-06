package com.quandl.api.java;

import com.quandl.api.java.query.MultisetQuery;
import com.quandl.api.java.query.Queries;
import com.quandl.api.java.query.BaseQuery;

public class Examples {
    @SuppressWarnings("unused")
    public final static void main(String[] args) throws InvalidTokenException {
        // Open a connection with or without an API key
        QuandlConnection qc = args.length >= 1 ? QuandlConnection.getFullConnection(args[0]) : QuandlConnection.getLimitedConnection();
        
        // Get a full dataset using its Quandl Code
        // http://www.quandl.com/WIKI/AAPL-Apple-Inc-AAPL-Prices-Dividends-Splits-and-Trading-Volume
        QDataset full = qc.getDataset("WIKI/AAPL");

        // Construct more complex queries with Query objects
        BaseQuery datedQuery = Queries.create("WIKI/AAPL").dateRange("2013-1-1", "2013-12-31");
        QDataset filtered = qc.getDataset(datedQuery);

        // And expand on those queries later
        QDataset sorted = qc.getDataset(datedQuery.ascending());

        // Or even reuse a query's settings in a new type of query
        MultisetQuery mq = Queries.createFrom("WIKI/GOOG", 4, "WIKI/FB", 4, datedQuery);
        
        // TODO Get dataset
    }
}
