package cn.linyi.music.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
    /* Reads an InputStream and converts it to a String.
    将字节流转换成String
    */
    public static String inputStreamToString(InputStream stream) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer stringBuffer = new StringBuffer();
        byte[] buffer = new byte[1024];
        while ((stream.read(buffer)) != -1) {
            stringBuffer.append(new String(buffer));

        }
        return new String(stringBuffer);
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
