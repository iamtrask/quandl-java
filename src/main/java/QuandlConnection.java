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

        if (connectedWithGoodToken(token)) {
            this.token = token;
        } else {
            System.out.println("Bad token... you are connected through the public api and will be rate limited accordingly.");
        }
    }



    /**
     * This method uses the "favorites" url to check that the provided token is valid.
     * @param token this is the security token for your quandl account.
     * @return true or false... depending on whether or not the token is valid.
     */
    private boolean connectedWithGoodToken(String token) {
        String output = this.curl("http://www.quandl.com/api/v1/current_user/collections/datasets/favourites.json?auth_token=" + token);
//        System.out.println("OUTPUT:" + output);
        if (output.contains("Unauthorized")) {
            System.out.println("BAD TOKEN!!! Check your token under http://www.quandl.com/users/edit Click \"API\" and use the token specified");
            return false;
        }
        return true;

    }

    /**
     * This method just executes HTTP requests... putting the boilerplate code in one place.
     * @param url this is the url for the http request... it assumes "http://" is already included.
     * @return it returns the response from the url in string form... or the message of the exception if one is thrown.
     */
    private String curl(String url) {

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
            return e.getMessage();


        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return output;

    }


}
