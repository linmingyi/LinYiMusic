package cn.linyi.music;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import cn.linyi.music.util.HttpUtil;

/**
 * Created by linyi on 2016/4/17.
 */
public class LoginActivity extends PopupWindow implements View.OnClickListener{
    private View view;
    private Activity activity;
    private LayoutInflater inflater;
    private EditText uname,password;
    private Button loginbtn;
    private TextView serUname,serPassword;
    private HttpURLConnection conn;
    public LoginActivity(Activity a){
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_login,null);
        uname = (EditText) view.findViewById(R.id.uname);
        password = (EditText) view.findViewById(R.id.password);
        loginbtn = (Button) view.findViewById(R.id.login);
        serUname = (TextView) view.findViewById(R.id.ser_uname);
        serPassword = (TextView) view.findViewById(R.id.ser_password);
        loginbtn.setOnClickListener(this);
        //musicListView.setOnItemClickListener(this);
        this.setFocusable(true);
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(800);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login :
                login();
                break;
            default:
                break;
        }
    }

    private void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil httpUtil = new HttpUtil("http://172.22.51.2:8080/musicServer/login");
                    conn = httpUtil.getConn();
                    //设置输入输出流
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    //设置请求方法为POST
                    conn.setRequestMethod("POST");
                    //Post方式无法缓存数据，手动设置使用缓存为False
                    conn.setUseCaches(false);
                    //连接服务器
                    conn.connect();
                    OutputStream os = conn.getOutputStream();

                    DataOutputStream dos = new DataOutputStream(os);

                    dos.writeBytes("name="+ URLEncoder.encode(uname.getText().toString(),"UTF-8"));
                    dos.writeBytes("&password=" + URLEncoder.encode(password.getText().toString(), "UTF-8"));
                    dos.close();

                    BufferedReader bfr = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String line = null;
                    StringBuffer  sb = new StringBuffer();
                    while((line = bfr.readLine()) != null) {
                        sb.append(line);
                    }
                   // serUname.setText(sb.toString());
                    Log.i("YI",sb.toString()+"_________________");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
