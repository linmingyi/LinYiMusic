package cn.linyi.music.util;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import cn.linyi.music.MainsActivity;
import cn.linyi.music.R;
import cn.linyi.music.bean.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_register;
    private EditText uname;
    private EditText password;
    private EditText ip;//仅用于测试用途
    private Handler handler;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_login);
        btn_register = (Button) findViewById(R.id.login);
        btn_register.setText("注册");
        btn_register.setOnClickListener(this);
        uname = (EditText) findViewById(R.id.uname);
        password = (EditText) findViewById(R.id.password);
        ip = (EditText) findViewById(R.id.ip_adress);
        user = new User();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    Log.i("YI","注册成功");
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainsActivity.class);
                    startActivity(intent);
                } else if (msg.what == 0) {
                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login :
                register();
                Log.i("YI","REGISTER");
                break;
            default:
                break;
        }
    }

    public void  register() {
        final String ipAdress = ip.getText().toString();
        Log.i("YI",ipAdress.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil httpUtil = new HttpUtil("http://"+ipAdress+":8080/musicServer/register");
                    Log.i("YI",httpUtil.toString());
                    HttpURLConnection conn = httpUtil.getConn();
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
                        Log.i("YI",sb.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        if(jsonObject.getInt("resultCode") == 1) {
                            JSONObject j1 = jsonObject.getJSONObject("user");
                            Log.i("YI", j1.toString());
                            user = JSONUtil.toObject(j1, user);
                            Global.setUser(user);
                            Log.i("YI", user.getNickname() + "  \n\t" + user.getUname() + "\n\t" + user.getFavorMusic());
                            handler.sendEmptyMessage(1);
                        }else {
                            Log.i("YI","不能转换");
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
}
