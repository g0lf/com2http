package ru.connector.com2http;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by g0lf on 02.10.2016.
 */
public class HttpSender {

    private final static Logger log = LoggerFactory.getLogger(HttpSender.class);

    private final String USER_AGENT = "Mozilla/5.0";

    private final String SCAN_DATA_PARAM = "scan_data";
    private final String SESSION_ID_PARAM = "SBPROGID";
    private final String AUTH_LOGIN_PARAM = "su_email";
    private final String AUTH_PASSWORD_PARAM = "su_pass";

    private String url;
    private String url_auth;
    private String auth_login;
    private String auth_pass;

    public HttpSender() {
        this.url = Settings.getParam(Settings.HTTP_URL, "");
        this.url_auth = Settings.getParam(Settings.HTTP_AUTH_URL, "");
        this.auth_login = Settings.getParam(Settings.HTTP_AUTH_LOGIN, "");
        this.auth_pass = Settings.getParam(Settings.HTTP_AUTH_PASSWORD, "");
    }

    public void send(String data) throws Exception {
        log.info("authorization...");
        String sessionId = auth();

        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(SCAN_DATA_PARAM, data));
        nvps.add(new BasicNameValuePair(SESSION_ID_PARAM, sessionId));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        StringBuffer responseBody = sendRequest(httpPost, null);

        if (data.equals(responseBody.toString())){
            log.info("data was sent successfully");
        } else {
            log.info("got unexpected response");
            log.info("response body : {}", responseBody);
        }
    }

    public String auth() throws Exception {

        CookieStore httpCookieStore = new BasicCookieStore();

        HttpPost httpPost = new HttpPost(url_auth);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(AUTH_LOGIN_PARAM, auth_login));
        nvps.add(new BasicNameValuePair(AUTH_PASSWORD_PARAM, auth_pass));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        StringBuffer responseBody = sendRequest(httpPost, httpCookieStore);

        if (responseBody.toString().contains(AUTH_LOGIN_PARAM) && responseBody.toString().contains(AUTH_PASSWORD_PARAM)){
            log.error("authorization failed. check login, password and auth url");
            throw new AuthorizationFailedException();
        } else {
            log.info("authorizated successfully");
        }

        List<Cookie> cookieList = httpCookieStore.getCookies();
        return cookieList.stream()
                .filter(cookie -> SESSION_ID_PARAM.equals(cookie.getName()))
                .findFirst()
                .orElse(null)
                .getValue();
    }

    private StringBuffer sendRequest(HttpPost httpPost, CookieStore httpCookieStore) throws Exception {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder
                .create()
                .setRedirectStrategy(new LaxRedirectStrategy());

        if (httpCookieStore != null){
            httpClientBuilder.setDefaultCookieStore(httpCookieStore);
        }
        CloseableHttpClient httpClient = httpClientBuilder.build();

        httpPost.setHeader("User-Agent", USER_AGENT);
        httpPost.setHeader("Accept-Encoding", "");

        log.trace("Sending request to URL : " + httpPost.getURI());

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {

            int responseCode = response.getStatusLine().getStatusCode();

            log.trace("Response code: " + responseCode);
            if (responseCode / 100 != 2){
                log.error("invalid response code");
                throw new Exception();
            }

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();

            return result;

        } catch (IOException e) {
            log.error("can not execute request");
            throw e;
        }
    }
}
