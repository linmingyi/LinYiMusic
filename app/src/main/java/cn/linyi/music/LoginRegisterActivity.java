package cn.linyi.music;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.linyi.music.util.RegisterActivity;

public class LoginRegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_showLogin;
    private Button  btn_Register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        btn_showLogin = (Button) findViewById(R.id.btn_showLogin);
        btn_Register = (Button) findViewById(R.id.btn_showRegister);
        btn_showLogin.setOnClickListener(this);
        btn_Register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_showLogin :
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_showRegister :
                Intent intent1 = new Intent(this,RegisterActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
