package com.quandl.api.java.query;

public class Queries {
    public static SimpleQuery create(String qCode) {
        return new SimpleQuery(qCode);
    }
    
    public static SimpleQuery createFrom(String qCode, BaseQuery from) {
        return new SimpleQuery(qCode, from);
    }
    
    public static MultisetQuery create(String qCode1, int column1, String qCode2, int column2) {
        return new MultisetQuery(qCode1, column1, qCode2, column2);
    }
    
    public static MultisetQuery createFrom(String qCode1, int column1, String qCode2, int column2, BaseQuery from) {
        return new MultisetQuery(qCode1, column1, qCode2, column2, from);
    }
    
    public static MetadataQuery createMetadata(String qCode) {
        return new MetadataQuery(qCode);
    }
}
