package com.quandl.api.java.query;

import static com.google.common.base.Preconditions.*;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * BaseQuery represents the shared configuration options most
 * queries (notably simple and multiset) rely on.
 * 
 * @author Michael Diamond
 * @since  2014-6-6
 */
/*
 * Implementation Notes:
 *   Each instance variable in this class have a series of
 *   associated methods which follow a similar pattern:
 *     1. A public with*() style method.  This allows the caller
 *        to set any (valid) value they want, including the currently
 *        set value.  This is the actual workhorse method constructing
 *        new BaseQuery objects and making the necessary updates.
 *     2. Public helper functions that more clearly let the user specify
 *        their intent.  For instance, instead of making .set(true) public
 *        we define .enable() and .disable() methods.  Users should be 
 *        encouraged to use the helper functions, rather than the general setters.
 *   
 *   This design means adding new parameters is more verbose than it needs
 *   to be (essentially, there's multiple setters), however it should be more
 *   readable, letting users write lines like:
 *   
 *     query.startDate("2014-1-1").allResults()
 *     
 *   Rather than, say:
 *   
 *     query.dateRange("2014-1-1", null).numRows(0)
 *   
 *   Or something similarly more verbose or confusing for the caller.
 */
public abstract class BaseQuery implements Query {
    public static enum SortOrder {ASC, DESC}
    public static enum Collapse {NONE, DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUALLY}
    public static enum Transform {NONE, DIFF, RDIFF, CUMUL, NORMALIZE}
    
    private SortOrder sort;
    private Optional<Integer> rows;
    private Optional<Date> start;
    private Optional<Date> end;
    private Optional<Integer> column;
    private Collapse collapse;
    private Transform transform;
    
    /** Construct a default BaseQuery */
    /*package*/ BaseQuery() {
        sort = SortOrder.DESC;
        rows = Optional.absent();
        start = Optional.absent();
        end = Optional.absent();
        column = Optional.absent();
        collapse = Collapse.NONE;
        transform = Transform.NONE;
    }
    
    /** Copy constructor duplicates from's values */
    /*package*/ BaseQuery(BaseQuery from) {
        sort = from.sort;
        rows = from.rows;
        start = from.start;
        end = from.end;
        column = from.column;
        collapse = from.collapse;
        transform = from.transform;
    }
    
    /**
     * Copy method used to enable subclasses to be
     * properly copied by actions in this class.
     * 
     * Returns a duplicate instance of the current object.
     */
    /*package*/ abstract BaseQuery copy();
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof BaseQuery)) {
            return false;
        }
        BaseQuery bq = (BaseQuery)o;
        return Objects.equals(sort, bq.sort) &&
               Objects.equals(rows, bq.rows) &&
               Objects.equals(start, bq.start) &&
               Objects.equals(end, bq.end) &&
               Objects.equals(column, bq.column) &&
               Objects.equals(collapse, bq.collapse) &&
               Objects.equals(transform, bq.transform);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sort, rows, start, end, column, collapse, transform);
    }
    
    //
    // Sort Order
    //
    /** Sets the query's sort order without checking the current value */
    public BaseQuery sortOrder(SortOrder so) {
        BaseQuery copy = copy();
        copy.sort = checkNotNull(so);
        return copy;
    }

    /** Sets the query's sort order to ascending, if currently descending */
    public BaseQuery ascending() {
        return sortOrder(SortOrder.ASC);
    }

    /** Sets the query's sort order to descending, if currently ascending */
    public BaseQuery descending() {
        return sortOrder(SortOrder.DESC);
    }
    
    //
    // Truncation
    //
    /** Sets the maximum number of rows to expect, or all if argument is absent. */
    public BaseQuery numRows(Optional<Integer> rs) {
        checkArgument(!rs.isPresent() || rs.get() > 0, "Must specify non-negative number of results, saw %s", rs);
        BaseQuery copy = copy();
        copy.rows = rs;
        return copy;
    }
    
    /** Clears the maximum row field, if previously set. */
    public BaseQuery allResults() {
        return numRows(Optional.<Integer>absent());
    }
    
    /** Sets the maximum number of rows the API should return. */
    public BaseQuery numRows(int rs) {
        return numRows(Optional.of(rs));
    }
    
    /** Limits the API to returning one row, the most recent. */
    public BaseQuery mostRecentRow() {
        return numRows(1);
    }
    
    //
    // Date Range
    //
    private BaseQuery dateRange(Optional<Date> st, Optional<Date> en) {
        BaseQuery copy = copy();
        copy.start = checkNotNull(st);
        copy.end = checkNotNull(en);
        return copy;
    }
    
    /**
     * Limits the result to values within the specified date range, which
     * cannot be null, or an invalid date.
     */
    public BaseQuery dateRange(String st, String en) {
        checkArgument(!start.isPresent());
        checkArgument(!end.isPresent());
        return dateRange(Optional.of(st).transform(Date.FROM_STRING), Optional.of(en).transform(Date.FROM_STRING));
    }
    
    /** Clears date range filtering */
    public BaseQuery allDates() {
        return dateRange(Optional.<Date>absent(), Optional.<Date>absent());
    }
    
    /** Sets just the start date as a lower bound */
    public BaseQuery startDate(String st) {
        checkArgument(!start.isPresent());
        return dateRange(Optional.of(st).transform(Date.FROM_STRING), end);
    }
    
    /** Clears start date filtering */
    public BaseQuery noStartDate() {
        checkArgument(start.isPresent());
        return dateRange(Optional.<Date>absent(), end);
    }
    
    /** Sets just the end date as an upper bound */
    public BaseQuery endDate(String en) {
        checkArgument(!end.isPresent());
        return dateRange(start, Optional.of(en).transform(Date.FROM_STRING));
    }
    
    /** Clears end date filtering */
    public BaseQuery noEndDate() {
        checkArgument(end.isPresent());
        return dateRange(start, Optional.<Date>absent());
    }
    
    //
    // Column
    //
    /** Limits the result to only the specified column, or all if absent. */
    public BaseQuery column(Optional<Integer> clm) {
        checkArgument(!clm.isPresent() || clm.get() >= 0, "Must specify positive row number, saw %s", clm);
        BaseQuery copy = copy();
        copy.column = clm;
        return copy;
    }
    
    /** Set the column to retrieve */
    public BaseQuery column(int clm) {
        return column(Optional.of(clm));
    }
    
    /** Clear the column filter, query now retrieves all columns */
    public BaseQuery allColumns() {
        return column(Optional.<Integer>absent());
    }
    
    //
    // Collapse
    //    
    /** Sets the collapse behavior */
    public BaseQuery collapse(Collapse clp) {
        checkArgument(collapse != clp);
        BaseQuery copy = copy();
        copy.collapse = checkNotNull(clp);
        return copy;
    }
    
    //
    // Transform
    //    
    /** Sets the transform behavior */
    public BaseQuery transform(Transform trn) {
        checkArgument(transform != trn);
        BaseQuery copy = copy();
        copy.transform = checkNotNull(trn);
        return copy;
    }
    
    //
    // To Parameter Map
    //
    @Override
    public final Map<String,String> getParameterMap() {
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
    
    /**
     * Lets implementing classes specify their own parameters, which
     * BaseQuery will include in its parameter map.
     */
    protected abstract Map<String,String> getSubParamMap();
    
    /**
     * Internal class representing a local date.  Ideally, we'd use
     * JodaTime or Java 8, however for the moment we're simply using
     * this class to validate the string is date-like.
     */
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
        
        @Override
        public boolean equals(Object o) {
            if(!(o instanceof BaseQuery)) {
                return false;
            }
            Date d = (Date)o;
            return Objects.equals(year, d.year) &&
                   Objects.equals(month, d.month) &&
                   Objects.equals(day, d.day);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(year, month, day);
        }
    }
}
