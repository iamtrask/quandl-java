package com.quandl.api;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Description of file content.
 *
 * @author atrask
 *         7/21/13
 */
public class QDataset {

    private String id;
    private String sourceCode;
    private String code;
    private String name;
    private String urlize_name;
    private String description;
    private String updatedAt;
    private String frequency;
    private String fromDate;
    private String toDate;
    private ArrayList<String> columnNames;
    private boolean isPrivate;
    private String errors;
    private String rawData;
    private ArrayList<QEntry> dataset = new ArrayList<QEntry>();


    JSONParser parser = new JSONParser();

    public QDataset(String input, String type) {

        try {


            JSONObject json = (JSONObject) parser.parse(input);

            id = json.get("id").toString();
            sourceCode = json.get("source_code").toString();
            code = json.get("code").toString();
            name = json.get("name").toString();
            urlize_name = json.get("urlize_name").toString();
            description = json.get("description").toString();
            updatedAt = json.get("updated_at").toString();
            frequency = json.get("frequency").toString();
            fromDate = json.get("from_date").toString();
            toDate = json.get("to_date").toString();

//            columnNames = json.get("column_name");

            if (json.get("private").toString().contains("true")) {
                isPrivate = true;
            } else {
                isPrivate = false;
            }

            errors = json.get("errors").toString();
            rawData = json.get("data").toString();

            JSONArray tempDataset = (JSONArray) parser.parse(rawData);
            for (Object eachRow : tempDataset) {
                this.addJsonRow(eachRow.toString());
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void addJsonRow(String row) throws ParseException {
        System.out.println("ADDING ROW:" + row);
        JSONArray tmp = (JSONArray) parser.parse(row);
        dataset.add(new QEntry(tmp));
    }

    public void print() {

        println("ID:" + id);
        println("SOURCE CODE:" + sourceCode);
        println("CODE:" + code);
        println("NAME:" + name);
        println("URLIZE NAME:" + urlize_name);
        println("DESCRIPTION:" + description);
        println("UPDATED AT:" + updatedAt);
        println("FREQUENCY:" + frequency);
        println("FROM DATE:" + fromDate);
        println("TO DATE:" + toDate);
        println("IS PRIVATE:" + isPrivate);
        println("ERRORS:" + errors);
        println("RAW DATA:" + rawData);


    }

    public void println(String string) {
        System.out.println(string);
    }

    public String getData() {
        return rawData;
    }

    public ArrayList<QEntry> getDataset() {
        return dataset;
    }

    public ArrayList<ArrayList<String>> getArrayMatrix() {
        ArrayList<ArrayList<String>> arrayMatrix = new ArrayList<ArrayList<String>>();

        for (QEntry eachEntry : dataset) {
            arrayMatrix.add(eachEntry.getRow());
        }

        return arrayMatrix;
    }

    public String[][] getStringMatrix() {
        String stringMatrix[][] = new String[dataset.size()][dataset.get(0).getRow().size()];

        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.get(i).getRow().size(); j++) {
                stringMatrix[i][j] = dataset.get(i).getRow().get(j);
            }
        }
        return stringMatrix;
    }


}
