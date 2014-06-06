package com.quandl.api.java.query;

import static com.google.common.base.Preconditions.*;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public abstract class BaseQuery implements Query {
    public static enum SortOrder {ASC, DESC}
    public static enum Collapse {NONE, DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUALY}
    public static enum Transform {NONE, DIFF, RDIFF, CUMUL, NORMALIZE}
    
    private SortOrder sort;
    private Optional<Integer> rows;
    private Optional<Date> start;
    private Optional<Date> end;
    private Optional<Integer> column;
    private Collapse collapse;
    private Transform transform;
    
    /*package*/ BaseQuery() {
        sort = SortOrder.DESC;
        rows = Optional.absent();
        start = Optional.absent();
        end = Optional.absent();
        column = Optional.absent();
        collapse = Collapse.NONE;
        transform = Transform.NONE;
    }
    
    /*package*/ BaseQuery(BaseQuery from) {
        sort = from.sort;
        rows = from.rows;
        start = from.start;
        end = from.end;
        column = from.column;
        collapse = from.collapse;
        transform = from.transform;
    }
    
    /*package*/ abstract BaseQuery copy(); 
    
    //
    // Sort Order
    //
    public BaseQuery sortOrderUnchecked(SortOrder so) {
        BaseQuery copy = copy();
        copy.sort = checkNotNull(so);
        return copy;
    }
    
    private BaseQuery sortOrder(SortOrder so) {
        checkArgument(sort != so, "Sort order is already %s", sort);
        return sortOrderUnchecked(so);
    }

    public BaseQuery ascending() {
        return sortOrder(SortOrder.ASC);
    }

    public BaseQuery descending() {
        return sortOrder(SortOrder.DESC);
    }
    
    //
    // Truncation
    //
    public BaseQuery numRowsUnchecked(Optional<Integer> rs) {
        checkArgument(!rs.isPresent() || rs.get() > 0, "Must specify non-negative number of results, saw %s", rs);
        BaseQuery copy = copy();
        copy.rows = rs;
        return copy;
    }
    
    private BaseQuery numRows(Optional<Integer> rs) {
        checkArgument(!rs.equals(rows), "Num rows is already %s", rows.isPresent() ? rows.get() : "all");
        return numRowsUnchecked(rs);
    }
    
    public BaseQuery allResults() {
        return numRows(Optional.<Integer>absent());
    }
    
    public BaseQuery numRows(int rs) {
        return numRows(Optional.of(rs));
    }
    
    public BaseQuery mostRecentRow() {
        return numRows(1);
    }
    
    //
    // Date Range
    //
    private BaseQuery dateRangeUC(Optional<Date> st, Optional<Date> en) {
        BaseQuery copy = copy();
        copy.start = checkNotNull(st);
        copy.end = checkNotNull(en);
        return copy;
    }
    
    public BaseQuery dateRangeUnchecked(Optional<String> st, Optional<String> en) {
        return dateRangeUC(st.transform(Date.FROM_STRING), en.transform(Date.FROM_STRING));
    }

    public BaseQuery dateRange(String st, String en) {
        checkArgument(!start.isPresent());
        checkArgument(!end.isPresent());
        return dateRangeUnchecked(Optional.of(st), Optional.of(en));
    }
    
    public BaseQuery allDates() {
        return dateRangeUC(Optional.<Date>absent(), Optional.<Date>absent());
    }
    
    public BaseQuery startDate(String st) {
        checkArgument(!start.isPresent());
        return dateRangeUC(Optional.of(st).transform(Date.FROM_STRING), end);
    }
    
    public BaseQuery noStartDate() {
        checkArgument(start.isPresent());
        return dateRangeUC(Optional.<Date>absent(), end);
    }
    
    public BaseQuery endDate(String en) {
        checkArgument(!end.isPresent());
        return dateRangeUC(start, Optional.of(en).transform(Date.FROM_STRING));
    }
    
    public BaseQuery noEndDate() {
        checkArgument(end.isPresent());
        return dateRangeUC(start, Optional.<Date>absent());
    }
    
    //
    // Column
    //
    public BaseQuery columnUnchecked(Optional<Integer> clm) {
        checkArgument(!clm.isPresent() || clm.get() >= 0, "Must specify positive row number, saw %s", clm);
        BaseQuery copy = copy();
        copy.column = clm;
        return copy;
    }
    
    private BaseQuery column(Optional<Integer> clm) {
        checkArgument(!clm.equals(column), "Column is already %s", column.isPresent() ? column.get() : "unset");
        return columnUnchecked(clm);
    }
    
    public BaseQuery column(int clm) {
        return column(Optional.of(clm));
    }
    
    public BaseQuery allColumns() {
        return column(Optional.<Integer>absent());
    }
    
    //
    // Collapse
    //
    public BaseQuery collapseUnchecked(Collapse clp) {
        BaseQuery copy = copy();
        copy.collapse = checkNotNull(clp);
        return copy;
    }
    
    public BaseQuery collapse(Collapse clp) {
        checkArgument(collapse != clp);
        return collapseUnchecked(clp);
    }
    
    //
    // Transform
    //
    public BaseQuery transformUnchecked(Transform trn) {
        BaseQuery copy = copy();
        copy.transform = checkNotNull(trn);
        return copy;
    }
    
    public BaseQuery transform(Transform trn) {
        checkArgument(transform != trn);
        return transformUnchecked(trn);
    }
    
    //
    // To Parameter Map
    //
    @Override
    public final Map<String,String> toParameterMap() {
       // thread safe, but potentially could miss cache being set
       // and compute more than once
        if(cache == null) {
            ImmutableMap.Builder<String,String> builder = ImmutableMap.builder();
            builder.putAll(getSubParamMap())
                   .put("sort_order", sort.toString().toLowerCase());
            if(rows.isPresent()) {
                builder.put("rows", String.valueOf(rows.get()));
            }
            if(start.isPresent()) {
                builder.put("trim_start", start.get().toString());
            }
            if(end.isPresent()) {
                builder.put("trim_end", end.get().toString());
            }
            if(column.isPresent()) {
                builder.put("column", column.get().toString());
            }
            builder.put("collapse", collapse.toString().toLowerCase())
                   .put("transform", transform.toString().toLowerCase());
            cache = builder.build();
        }
        return cache;
    }
    private transient Map<String,String> cache = null;
    
    protected abstract Map<String,String> getSubParamMap();
    
    private static class Date {
        private static final String DATE_FORMAT = "%04d-%02d-%02d";
        private final int year, month, day;
        
        public static Function<String,Date> FROM_STRING = new Function<String,Date>() {
            @Override
            public Date apply(String dateString) {
                String[] arr = dateString.split("-");
                checkArgument(arr.length == 3, "Invalid date string '%s'", dateString);
                return new Date(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
            }
        };
        
        public Date(int year, int month, int day) {
            checkArgument(year > 0);
            checkArgument(month >= 1 && month <= 12);
            checkArgument(day >= 1 && day <= 31);
            this.year = year;
            this.month = month;
            this.day = day;
        }
        
        @Override
        public String toString() {
            return String.format(DATE_FORMAT, year, month, day);
        }
    }
}
