package com.quandl.api.java.query;

import java.util.Map;

/**
 * A Query is a structured representation of the information to
 * be sent to Quandl to request data.  Query objects are immutable,
 * and provide builder-style methods to construct modified Queries.
 * 
 * This means Queries can be safely cached over time and used to
 * create new Queries in a threadsafe manner.
 * 
 * See the implementing classes, along with the
 * <a href="http://www.quandl.com/help/api">Quandl API Docs</a>
 * for more details.
 * 
 * @author Michael Diamond
 * @since  2014-6-6
 */
public interface Query {
    /**
     * Returns the optional arguments that have been set on this Query,
     * for instance sort order might appear in the resulting map, if
     * the user specifies.  However required arguments, which are generally
     * provided to the factory methods in Queries, are stored as dedicated
     * members of the implementing classes.  For instance, SimpleQuery has
     * a .getQCode() method to return the Quandl Code for that query, as
     * the Quandl Code is necessary to execute a simple API request.
     */
    public Map<String,String> getParameterMap();
}
