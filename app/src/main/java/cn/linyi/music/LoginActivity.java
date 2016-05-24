package cn.linyi.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import android.os.Handler;

import cn.linyi.music.bean.User;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.HttpUtil;
import cn.linyi.music.util.JSONUtil;
import cn.linyi.music.util.MD5Util;

/**
 * Created by linyi on 2016/4/17.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private View view;
    private Activity activity;
    private LayoutInflater inflater;
    private EditText uname,password;
    private Button loginbtn;
    private TextView serUname,serPassword;
    private HttpURLConnection conn;
    private EditText ipAdress;
    private Handler handler;
    private User user;
    private  SharedPreferences sp;
    public static final String NICKNAME = "nickName";
    public static final String UID = "userId";
    public static final String USER_COLLECT_PLAYLIST = "collectPlaylist";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_login);
        init();
        sp = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
    }

    private void init(){
        user = new User();
        uname = (EditText) findViewById(R.id.uname);
        password = (EditText) findViewById(R.id.password);
        loginbtn = (Button) findViewById(R.id.login);
        serUname = (TextView) findViewById(R.id.ser_uname);
        serPassword = (TextView) findViewById(R.id.ser_password);
        ipAdress = (EditText) findViewById(R.id.ip_adress);
        loginbtn.setOnClickListener(this);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    serUname.setText(user.getNickname());
                   // Log.i("NUO",user.getNickname());
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(NICKNAME,user.getNickname());
                    editor.putInt(UID,user.getId());
                    editor.putString(USER_COLLECT_PLAYLIST,user.getCollectPlaylist());
                    editor.commit();
                  /*  Intent intent = new Intent(LoginActivity.this,MainsActivity.class);
                    startActivity(intent);*/
                } else {
                    if(msg.what == 0) {
                        Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };
        //musicListView.setOnItemClickListener(this);
        //this.set
      /*  this.setFocusable(true);
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(Global.SCREEN_WIDTH*2/3);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.FILL_PARENT);*/
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
        final String ip = ipAdress.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil httpUtil = new HttpUtil("http://"+ip+"/music/login");
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
                    dos.writeBytes("&password=" + URLEncoder.encode(MD5Util.getMD5String(password.getText().toString()), "UTF-8"));
                    dos.close();
                    //接受服务器端发送过来的数据（用json传递数据）

                    BufferedReader bfr = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    String line = null;
                    StringBuffer  sb = new StringBuffer();
                    while((line = bfr.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.i("YI",sb.toString()+"result");
                    try {
                        JSONObject jsonObject = new JSONObject(new String(sb.toString().getBytes(),"UTF-8"));
                        if(jsonObject.getInt("resultCode") == 1) {
                            Log.i("YI",sb.toString());
                            Log.i("YI",jsonObject.toString());
                            JSONObject j1 = jsonObject.getJSONObject("user");
                            Log.i("YI", j1.toString());
                            user = JSONUtil.toObject(j1, user);
                            Global.setUser(user);
                            Log.i("YI", user.getNickname() + "  \n\t" + user.getUname() + "\n\t" + user.getFavorMusic());
                            handler.sendEmptyMessage(1);
                        }else {
                            handler.sendEmptyMessage(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
/*    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            serUname.setText(user.getUname());
            serPassword.setText(user.getPassword());
             android:name=".util.Global"
        }
    };*/
}
