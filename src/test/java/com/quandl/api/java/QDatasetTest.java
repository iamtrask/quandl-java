package com.quandl.api.java;
/**
 * Basic behavior unit test, parses a JSON dataset and confirms
 * contents are as expected.
 */

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

public class QDatasetTest {
    /** Simple resource reader - ideally we'd use Guava's Resources class instead */
    private static String readResource(InputStream stream) throws IOException {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            return sb.toString();
        }
    }
    
    @Test
    public void parseDataset() throws IOException {
        QDataset dataset = new QDataset(readResource(getClass().getClassLoader().getResourceAsStream("PRAGUESE-PX.json")));
        assertEquals("2422996", dataset.getId());
        assertEquals("PRAGUESE", dataset.getSourceCode());
        assertEquals("PX", dataset.getCode());
        assertEquals("Prague Stock Index: PX", dataset.getName());
        assertEquals("Prague-Stock-Index-PX", dataset.getUrlizeName());
        assertEquals("Price index of blue chip issues. Base value: 1000 points. Start date: April 5, 1994. Number of base issues: variable.", dataset.getDescription());
        assertEquals("2013-07-22T17:12:06Z", dataset.getUpdatedAt());
        assertEquals("daily", dataset.getFrequency());
        assertEquals("1993-09-07", dataset.getFromDate());
        assertEquals("2013-07-22", dataset.getToDate());
        assertEquals(Arrays.asList("Date", "Index", "% Change"), dataset.getColumnNames());
        assertFalse(dataset.isPrivate());
        assertEquals("{}", dataset.getErrors());
        assertEquals(44, dataset.getDataset().size());
    }
}
