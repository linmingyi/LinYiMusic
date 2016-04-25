package cn.linyi.music.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by linyi on 2016/4/3.
 */
public class DownloadService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadService(String name) {
        super(name);
    }
    public DownloadService(){
        this("download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            URL url = new URL("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white_fe6da1ec.png");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6*1000);
            if(conn.getResponseCode() !=200) {
                Toast.makeText(getApplicationContext(),"连接失败",Toast.LENGTH_SHORT).show();
            }
            InputStream in = conn.getInputStream();
            byte[] result = conn.getResponseMessage().getBytes();
            File file = Environment.getExternalStorageDirectory();
            File out_file = new File(file,"abc.png");
            OutputStream out = new FileOutputStream(out_file);
            out.write(result);
            in.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
