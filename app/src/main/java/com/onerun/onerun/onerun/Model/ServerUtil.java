package com.onerun.onerun.onerun.Model;

import com.onerun.onerun.onerun.ToastMessage;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Tyler on 27/03/2015.
 */


public class ServerUtil {

    private static String serverURL = "http://morning-citadel-9755.herokuapp.com";
    private static Person pers;

    public static void startRun(String name, String macAddr) throws UnsupportedEncodingException {
        final String n = URLEncoder.encode(name, "utf-8");
        final String m = macAddr;
        new Thread(new Runnable() {
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpParams httpParameters = httpClient.getParams();
                HttpConnectionParams.setTcpNoDelay(httpParameters, true);
                HttpContext localContext = new BasicHttpContext();
                String req = serverURL + "/startRun?name=" + n + "&mac=" + m;
                try {
                    HttpGet httpGet = new HttpGet(req);
                    HttpResponse response = httpClient.execute(httpGet, localContext);
                    BufferedReader reader = new BufferedReader(new  InputStreamReader(response.getEntity().getContent()));
                } catch (Exception e) {
                    // writing exception to log
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /*
            RUN IN A THREAD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    public static Person getRunner(String macAddr) throws InterruptedException {
        final String m = macAddr;

                HttpClient httpClient = new DefaultHttpClient();
                HttpParams httpParameters = httpClient.getParams();
                HttpConnectionParams.setTcpNoDelay(httpParameters, true);
                HttpContext localContext = new BasicHttpContext();
                String req = serverURL + "/getRunner?userid=" + m;
                BufferedReader br;
                try {
                    HttpGet httpGet = new HttpGet(req);
                    HttpResponse response = httpClient.execute(httpGet, localContext);
                    br = new BufferedReader(new  InputStreamReader(response.getEntity().getContent()));

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    JSONObject json = new JSONObject(sb.toString());
                    return new Person(0, macAddr, json.getString("username"), 0, 0.0, 0.0);

                } catch (Exception e) {
                    // writing exception to log
                    e.printStackTrace();
                }

        return new Person(-1, "fail", "fail", 0, 0.0, 0.0);
    }

    private static void setPerson(Person p){
        pers = p;
    }

    public static void endRun(String macAddr){
        final String m = macAddr;
        new Thread(new Runnable() {
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpParams httpParameters = httpClient.getParams();
                HttpConnectionParams.setTcpNoDelay(httpParameters, true);
                HttpContext localContext = new BasicHttpContext();
                String req = serverURL + "/endRun?userid=" + m;
                try {
                    HttpGet httpGet = new HttpGet(req);
                    HttpResponse response = httpClient.execute(httpGet, localContext);
                    BufferedReader reader = new BufferedReader(new  InputStreamReader(response.getEntity().getContent()));
                } catch (Exception e) {
                    // writing exception to log
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
