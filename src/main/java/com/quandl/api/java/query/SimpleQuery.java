package com.quandl.api.java.query;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

public class SimpleQuery extends BaseQuery {
    private final String qCode;
    
    /*package*/ SimpleQuery(String qCode) {
        super();
        this.qCode = checkNotNull(qCode);
    }

    /*package*/ SimpleQuery(String qCode, BaseQuery from) {
        super(from);
        this.qCode = checkNotNull(qCode);
    }

    @Override
    /*package*/ SimpleQuery copy() {
        return new SimpleQuery(qCode, this);
    }
    
    public String getQCode() {
        return qCode;
    }

    @Override
    protected Map<String, String> getSubParamMap() {
        return ImmutableMap.of();
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof SimpleQuery)) {
            return false;
        }
        SimpleQuery sq = (SimpleQuery)o;
        return super.equals(sq) &&
               Objects.equal(qCode, sq.qCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), qCode);
    }
}
