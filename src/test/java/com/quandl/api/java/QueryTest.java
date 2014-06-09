package com.quandl.api.java;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.quandl.api.java.query.MultisetQuery;
import com.quandl.api.java.query.Queries;
import com.quandl.api.java.query.SimpleQuery;
import com.quandl.api.java.query.BaseQuery.Collapse;
import com.quandl.api.java.query.BaseQuery.SortOrder;
import com.quandl.api.java.query.BaseQuery.Transform;

public class QueryTest {
    @Test
    public void immutableTest() {
        SimpleQuery q1 = Queries.create("code");
        SimpleQuery q2 = Queries.create("code");
        assertEquals(q1, q2);
        SimpleQuery q3 = q1.mostRecentRow();
        assertEquals(q1, q2);
        assertNotEquals(q1, q3);
    }
    
    @Test
    public void setTest() {
        SimpleQuery q1 = Queries.create("code");
        assertEquals(q1.getQCode(),"code");
        assertEquals(q1.getParameterMap(),ImmutableMap.of());
        
        SimpleQuery q2 = q1.sortOrder(SortOrder.ASC)
                          .numRows(10)
                          .startDate("2000-1-1")
                          .endDate("2001-1-1")
                          .column(1)
                          .collapse(Collapse.ANNUALLY)
                          .transform(Transform.CUMUL);
        assertEquals(q2.getQCode(),"code");
        assertEquals(q2.getParameterMap(),
                     new ImmutableMap.Builder<String,String>()
                         .put("sort_order","asc")
                         .put("rows","10")
                         .put("trim_start","2000-01-01")
                         .put("trim_end","2001-01-01")
                         .put("column","1")
                         .put("collapse","annually")
                         .put("transform","cumul")
                         .build());
        
        MultisetQuery mq1 = Queries.createMultisetFrom("code1", 1, "code2", 2, q1);
        assertEquals(mq1.getParameterMap(),ImmutableMap.of("columns","code1.1,code2.2"));
        
        MultisetQuery mq2 = Queries.createMultisetFrom("code1", 1, "code2", 2, q2);
        assertEquals(mq2.getParameterMap(),
                new ImmutableMap.Builder<String,String>()
                    .put("columns","code1.1,code2.2")
                    .put("sort_order","asc")
                    .put("rows","10")
                    .put("trim_start","2000-01-01")
                    .put("trim_end","2001-01-01")
                    .put("column","1") // Possibly a bug
                    .put("collapse","annually")
                    .put("transform","cumul")
                    .build());
    }
}
