package cn.linyi.music.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.linyi.music.util.Global;
import cn.linyi.music.PlayService;
import cn.linyi.music.R;
import cn.linyi.music.bean.Music;

/**
 * Created by linyi on 2016/3/30.
 */
public class Fragment_songs_list extends Fragment  implements AdapterView.OnItemClickListener {
    private Activity activity;
    private List<Music> onlineMusicList;
    private ListView listView;
    private Intent service;
    public static final String stringUrl ="http://linyinuo.site/music/list.html";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs_list,container,false);
        Log.i("LIN", view.getId() + "   "+R.layout.fragment_songs_list);
        listView = (ListView)view.findViewById(R.id.onlineMusicList);
        listView.setOnItemClickListener(this);
        new JsonText().execute(stringUrl);
        Log.i("LIN", stringUrl);
        activity = getActivity();
        service = ((Global)activity.getApplicationContext()).getPlayService();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        service.putExtra("current", position);
        Log.i("LIN", "ITEMCLICK" + position);
        service.putExtra("action", PlayService.MUSICLIST_PLAY);
        service.putExtra("musicType", PlayService.ONLINE_MUSIC);//歌曲类型在点击是要记得修改 OK！！！
        activity.startService(service);
    }


 /*   service.putExtra("current",position);
    activity.startService(service);*/
    private class JsonText extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String result =  downloadUrl(params[0]);
                Log.i("LIN","result:  "+result);
                return result;
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        protected void onPostExecute(String result) {
            onlineMusicList = parseJsonMulti(result);
            ((Global)getActivity().getApplicationContext()).setOnlineMusicList(onlineMusicList);//网络歌单更新
            List<String> strs = new ArrayList<String>();
            for(Music m : onlineMusicList) {
                strs.add(m.getTitle());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,strs);
            listView.setAdapter(adapter);
        }
    }

    //如果获取网页内容过程中抛出异常则返回 URL invalid！！
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;
        try {
            URL url = new URL(myurl);//地址面前务必加上http:// 否则会报协议的错误
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Fragment_songs", "The response is: " + response);
            Log.i("LIN", response+"jhkjh1");
            is = conn.getInputStream();
            String fileName = myurl.substring(myurl.lastIndexOf("/")+1);

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len,fileName);
            Log.i("LIN",contentAsString);
            return contentAsString;
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len,String fileName) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer stringBuffer = new StringBuffer();
        OutputStream out = new FileOutputStream(getActivity().getFilesDir()+ File.separator+fileName);
        Log.i("LIN",getActivity().getFilesDir()+"");
        byte[] buffer = new byte[1024];
        while ((stream.read(buffer)) != -1) {
            stringBuffer.append(new String(buffer));
            out.write(buffer);
        }
        out.close();
        return new String(stringBuffer);
    }

    private List<Music>  parseJsonMulti(String strResult) {
        String s = "";
        List<Music> musics = new ArrayList<Music>();
        try {
            JSONArray jsonObjs = new JSONObject(strResult).getJSONArray("music");
            for (int i = 0; i < jsonObjs.length(); i++) {
                JSONObject jsonObject = (JSONObject)jsonObjs.get(i);
                String name = jsonObject.getString("name");
                String path = jsonObject.getString("path");
                s += "name: "+ name + "path: " + path+"\n";
                Music musicInf = new Music();
                musicInf.setTitle(name);
                musicInf.setPath(path);
                musics.add(musicInf);
            }
            Log.i("LIN","JSON           "+s);
            return musics;
        } catch (JSONException e) {
            Log.i("LIN","Json parse error");
            e.printStackTrace();
        }
        return musics;
    }

}
