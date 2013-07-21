import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.IOException;

/**
 * Description of file content.
 *
 * @author atrask
 *         7/21/13
 */
public class QuandlConnection {

    String token;

    public QuandlConnection(String token) {
        this.token = token;
        System.out.println(this.curl("http://www.quandl.com/api/v1/datasets/PRAGUESE/PX.csv"));
    }

    public boolean connectedWithGoodToken() {

        return true;

    }




    public String curl(String url) {

        String output = "";

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(url);

            System.out.println("executing request " + httpget.getURI());

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            output = httpclient.execute(httpget, responseHandler);
//            System.out.println("----------------------------------------");
//            System.out.println(output);
//            System.out.println("----------------------------------------");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return output;

    }


}
