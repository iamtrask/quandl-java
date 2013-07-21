
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
    private ArrayList<QEntry> dataset =  new ArrayList<QEntry>();


    public QDataset(String data, String type) {

        try {

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(data);

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


        } catch (ParseException e) {
            e.printStackTrace();
        }

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


}
