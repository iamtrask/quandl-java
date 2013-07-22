package com.quandl.api;

import org.json.simple.JSONArray;

import java.util.ArrayList;

/**
 * Description of file content.
 *
 * @author atrask
 *         7/21/13
 */
public class QEntry {

    private String date;


    private ArrayList<String> row;

    public QEntry(JSONArray entry) {

        row = new ArrayList<String>();

        this.date = entry.get(0).toString();


        for(Object eachValue : entry) {
            row.add(eachValue.toString());
        }


    }

    public ArrayList<String> getRow() {
        return row;
    }

    public String getDate() {
        return date;
    }



}
