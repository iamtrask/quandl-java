package com.quandl.api;

import java.util.HashMap;

public class Main {

    public final static void main(String[] args) throws Exception {

        QuandlConnection q = new QuandlConnection("MNWDqjRSwW6348frGCEo");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("trim_start","2012-09-30");
        params.put("code","PX");
        params.put("source_code","PRAGUESE");
        q.getDatasetWithParams(params);

    }

}
