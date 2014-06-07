package com.quandl.api.java.query;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

/**
 * Represents a standard Quandl query for a single dataset,
 * essentially a BaseQuery for a specific QuandlCode.
 * 
 * @author Michael Diamond
 * @since  2014-6-6
 */
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
    
    /**
     * Returns the QuandlCode for this query
     */
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
