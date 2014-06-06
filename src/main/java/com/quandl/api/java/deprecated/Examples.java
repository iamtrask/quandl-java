package com.quandl.api.java.deprecated;

import java.util.HashMap;
import java.util.List;

import com.quandl.api.java.QDataset;
import com.quandl.api.java.QuandlConnection;

@SuppressWarnings("deprecation")
public class Examples {
    @SuppressWarnings("unused")
    public final static void main(String[] args) {

        //open connection with key
        QuandlConnection q = new QuandlConnection("mykey");

        //open connection without key
        QuandlConnection r = new QuandlConnection();

        //get dataset from keyrange
        QDataset data1 = q.getDataset("PRAGUESE/PX");

        //get dataset between two sets of dates
        QDataset data2 = q.getDatasetBetweenDates("PRAGUESE/PX","2012-01-01","2012-11-26");

        //get dataset with custom parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("trim_start","2012-09-30");
        QDataset data3 = q.getDatasetWithParams("PRAGUESE/PX",params);

        //get Dataset as array matrix
        List<List<String>> data3Matrix = data3.getMatrix();

        //get Dataset as String Matrix
        String[][] data3StringMatrix = data3.getStringMatrix();
    }
}
