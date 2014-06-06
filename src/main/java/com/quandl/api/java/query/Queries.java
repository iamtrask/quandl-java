package com.quandl.api.java.query;

/**
 * Factory class for constructing Query objects.
 * 
 * @author Michael Diamond
 * @since  2014-6-6
 */
public class Queries {
    /**
     * Creates a new Query that can be used to get a full dataset.
     */
    public static SimpleQuery create(String qCode) {
        return new SimpleQuery(qCode);
    }
    
    /**
     * Create a new Query for the specified Quandl Code using
     * the settings defined in the existing BaseQuery instance.
     * 
     * For example, with an existing MultisetQuery selecting data
     * over a defined date range, a new SimpleQuery can be constructed
     * querying the specified Qaundl Code and sharing the original
     * query's filtering behavior.
     */
    public static SimpleQuery createFrom(String qCode, BaseQuery from) {
        return new SimpleQuery(qCode, from);
    }
    
    /**
     * Creates a new Query that can be used to get a Multiset dataset.
     */
    public static MultisetQuery createMultiset(String qCode1, int column1, String qCode2, int column2) {
        return new MultisetQuery(qCode1, column1, qCode2, column2);
    }
    
    /**
     * Creates a new Query for the specified Multiset codes and columns
     * using the settings defined in the existing BaseQuery instance.
     */
    public static MultisetQuery createMultisetFrom(String qCode1, int column1, String qCode2, int column2, BaseQuery from) {
        return new MultisetQuery(qCode1, column1, qCode2, column2, from);
    }
    
    /**
     * Creates a new Query that can be used to get the metadata of a dataset.
     */
    public static MetadataQuery createMetadata(String qCode) {
        return new MetadataQuery(qCode);
    }
}
