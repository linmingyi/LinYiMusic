package cn.linyi.music.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by linyi on 2016/4/17.
 */
public class HttpUtil  {
    private HttpURLConnection conn;
    private URL url;

    //包装流
    private BufferedReader inbfr;
    private BufferedWriter outbfr;
    public HttpUtil (String urlStr) throws IOException {
        url = new URL(urlStr);
        conn =(HttpURLConnection)url.openConnection();
       // inbfr = new BufferedReader(new InputStreamReader(conn.getInputStream()));
       // outbfr = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
    }
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public HttpURLConnection getConn() {
        return conn;
    }

    public BufferedReader getInbfr() {
        return inbfr;
    }

    public BufferedWriter getOutbfr() {
        return outbfr;
    }
}
