package com.quandl.api.java.query;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class MetadataQuery implements Query {
    private final String qCode;
    
    /*package*/ public MetadataQuery(String qCode) {
        this.qCode = checkNotNull(qCode);
    }
    
    public String getQCode() {
        return qCode;
    }

    @Override
    public Map<String, String> getParameterMap() {
        return ImmutableMap.of();
    }
}
