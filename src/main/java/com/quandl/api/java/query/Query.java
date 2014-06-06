package com.quandl.api.java.query;

import java.util.Map;

public interface Query {
    public Map<String,String> toParameterMap();
}
