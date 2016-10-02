package ru.connector.com2http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by g0lf on 02.10.2016.
 */
public class HttpSender {

    private final static Logger log = LoggerFactory.getLogger(HttpSender.class);

    private String url;
    private final String USER_AGENT = "Mozilla/5.0";

    public HttpSender(String url) {
        this.url = url;
    }

    public void send(String data) throws Exception{
        URL obj = new URL(url +"?q="+data);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

       /* String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();*/

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        //System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);
    }
}
