package com.quandl.api.java;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Represents a Quantl Dataset.
 */
public class QDataset {
    // TODO these should be final
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
    private ArrayList<String> columnNames = new ArrayList<>();
    private boolean isPrivate;
    private String errors;
    private String rawData;
    private ArrayList<QEntry> dataset = new ArrayList<>();

    JSONParser parser = new JSONParser();

    @Deprecated
    public QDataset(String input, @SuppressWarnings("unused") String type) {
        this(input);
    }
    
    public QDataset(String input) {
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

            JSONArray tempColNames = (JSONArray) parser.parse(json.get("column_names").toString());

            for(Object eachCol : tempColNames) {
                columnNames.add(eachCol.toString());
            }

            isPrivate = json.get("private").toString().contains("true");

            errors = json.get("errors").toString();
            rawData = json.get("data").toString();

            JSONArray tempDataset = (JSONArray) parser.parse(rawData);
            for (Object eachRow : tempDataset) {
                this.addJsonRow(eachRow.toString());
            }
        } catch (ParseException e) {
            // TODO raise exception on bad parse
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;

    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getUrlizeName() {
        return urlize_name;
    }

    public String getDescription() {
        return description;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getErrors() {
        // TODO provide more structured Error object
        return errors;
    }

    public void addJsonRow(String row) throws ParseException {
        JSONArray tmp = (JSONArray) parser.parse(row);
        dataset.add(new QEntry(tmp));
    }


    public String getRawJSON() {
        return rawData;
    }

    public List<QEntry> getDataset() {
        return dataset;
    }

    public List<List<String>> getMatrix() {
        ArrayList<List<String>> arrayMatrix = new ArrayList<>();

        for (QEntry eachEntry : dataset) {
            arrayMatrix.add(eachEntry.getRow());
        }

        return arrayMatrix;
    }
    
    @Deprecated
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ArrayList<ArrayList<String>> getArrayMatrix() {
        return (ArrayList)getMatrix();
    }

    public String[][] getStringMatrix() {
        String stringMatrix[][] = new String[dataset.size()][columnNames.size()];

        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.get(i).getRow().size(); j++) {
                stringMatrix[i][j] = dataset.get(i).getRow().get(j);
            }
        }
        return stringMatrix;
    }

    @Deprecated
    public void print() {
        System.out.println(this);
    }
    
    @Override
    public String toString() {
        // Guava's ToStringHelper
        return "ID:" + id +
               "\nSOURCE CODE:" + sourceCode +
               "\nCODE:" + code +
               "\nNAME:" + name +
               "\nURLIZE NAME:" + urlize_name +
               "\nDESCRIPTION:" + description +
               "\nUPDATED AT:" + updatedAt +
               "\nFREQUENCY:" + frequency +
               "\nFROM DATE:" + fromDate +
               "\nTO DATE:" + toDate +
               "\nIS PRIVATE:" + isPrivate +
               "\nERRORS:" + errors +
               "\nRAW DATA:" + rawData;
    }
}
