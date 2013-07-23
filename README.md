quandl-java
===========

This is a java api for the quandl dataservice. 

        //open connection with key
        QuandlConnection q = new QuandlConnection("my key");

        //open connection without key
        QuandlConnection r = new QuandlConnection();

        //get dataset from keyrange
        QDataset data1 = q.getDataset("PRAGUESE/PX");

        //get dataset between two sets of dates
        QDataset data2 = q.getDatasetBetweenDates("PRAGUESE/PX","2012-01-01","2012-11-26");

        //get dataset with custom parameters
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("trim_start","2012-09-30");
        params.put("code","PX");
        params.put("source_code","PRAGUESE");
        QDataset data3 = q.getDatasetWithParams(params);

        //get Dataset as array matrix
        ArrayList<ArrayList<String>> data3Matrix = data3.getArrayMatrix();

        //get Dataset as String Matrix
        String[][] data3StringMatrix = data3.getStringMatrix();
