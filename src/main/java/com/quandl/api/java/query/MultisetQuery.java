package com.quandl.api.java.query;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MultisetQuery extends BaseQuery {
    private static final Joiner COMMA_JOINER = Joiner.on(',');
    private final List<SourceColumn> sources;
    
    /*package*/ MultisetQuery(String qCode1, int column1, String qCode2, int column2) {
        super();
        sources = ImmutableList.of(new SourceColumn(qCode1, column1),
                                   new SourceColumn(qCode2, column2));
    }
    
    /*package*/ MultisetQuery(String qCode1, int column1, String qCode2, int column2, BaseQuery from) {
        super(from);
        sources = ImmutableList.of(new SourceColumn(qCode1, column1),
                                   new SourceColumn(qCode2, column2));
    }

    /*package*/ MultisetQuery(List<SourceColumn> sources, BaseQuery from) {
        super(from);
        this.sources = ImmutableList.copyOf(sources);
    }

    @Override
    /*package*/ MultisetQuery copy() {
        return new MultisetQuery(sources, this);
    }
    
    public MultisetQuery sourceColumn(String qCode, int column) {
        return new MultisetQuery(
            new ImmutableList.Builder<SourceColumn>().addAll(sources).add(new SourceColumn(qCode, column)).build(),
            this);
    }
    
    public MultisetQuery withSourceColumns(String qCode1, int column1, String qCode2, int column2) {
        return new MultisetQuery(ImmutableList.of(new SourceColumn(qCode1, column1),
                                                  new SourceColumn(qCode2, column2)),
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
