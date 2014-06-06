package com.quandl.api.java;

import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single row of a QDataset result.
 */
public class QEntry {
    private String date;
    private ArrayList<String> row = new ArrayList<>();

    public QEntry(JSONArray entry) {
        this.date = entry.get(0).toString();
        
        for(Object eachValue : entry) {
            row.add(eachValue == null ? "null" : eachValue.toString());
        }
    }

    public String getDate() {
        return date;
    }

    public List<String> getRow() {
        return row;
    }
    
    @Override
    public String toString() {
        return row.toString();
    }
}
