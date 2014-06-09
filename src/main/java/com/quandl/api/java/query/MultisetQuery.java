package com.quandl.api.java.query;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Represents a multiset query, for specific columns
 * from one or more Quandl datasets.
 * 
 *  You can build a MultisetQuery with as many code/columns as
 *  you'd like, however at a minimum you must always specify at
 *  least two.  To specify only one, simply use a SimpleQuery
 *  and set a column filter.
 * 
 * FIXME the API indicates column numbers are optional on Multiset queries
 * however currently they're required here
 * FIXME BaseQuery allows a column field, which MultisetQuery perhaps shouldn't
 * 
 * @author Michael Diamond
 * @since  2014-6-6
 */
public class MultisetQuery extends BaseQuery<MultisetQuery> {
    private static final Joiner COMMA_JOINER = Joiner.on(',');
    private final List<SourceColumn> sources;
    
    /*package*/ MultisetQuery(String qCode1, int column1, String qCode2, int column2) {
        super();
        sources = ImmutableList.of(new SourceColumn(qCode1, column1),
                                   new SourceColumn(qCode2, column2));
    }
    
    /*package*/ MultisetQuery(String qCode1, int column1, String qCode2, int column2, BaseQuery<?> from) {
        super(from);
        sources = ImmutableList.of(new SourceColumn(qCode1, column1),
                                   new SourceColumn(qCode2, column2));
    }

    /*package*/ MultisetQuery(List<SourceColumn> sources, BaseQuery<?> from) {
        super(from);
        this.sources = ImmutableList.copyOf(sources);
    }

    @Override
    /*package*/ MultisetQuery copy() {
        return new MultisetQuery(sources, this);
    }
    
    /**
     * Adds an additional source column to the query.
     * 
     * Use Queries.createMultisetFrom() to create a new MultisetQuery
     * without the current set of columns.
     */
    public MultisetQuery sourceColumn(String qCode, int column) {
        return new MultisetQuery(
            new ImmutableList.Builder<SourceColumn>().addAll(sources).add(new SourceColumn(qCode, column)).build(),
            this);
    }

    @Override
    protected Map<String, String> getSubParamMap() {
        return ImmutableMap.of("columns", COMMA_JOINER.join(sources));
    }
    
    private static class SourceColumn {
        private final String qCode;
        private final int column;
        
        public SourceColumn(String qCode, int column) {
            this.qCode = checkNotNull(qCode);
            checkArgument(column > 0);
            this.column = column;
        }
        
        @Override
        public String toString() {
            return String.format("%s.%d", qCode.replace('/', '.'), column);
        }
    }
}
