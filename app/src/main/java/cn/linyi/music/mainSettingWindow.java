package cn.linyi.music;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.zip.Inflater;

import cn.linyi.music.fragment.FragmentLogined;
import cn.linyi.music.fragment.FragmentNoLogin;
import cn.linyi.music.util.Global;

/**
 * Created by linyi on 2016/5/19.
 */
public class MainSettingWindow extends PopupWindow implements View.OnTouchListener{
    private View view;
    private Context context;
    private TextView tvNickName,signToday,logingOut;
    private String nickName;
    private FragmentManager fm;
    private TextView tvToLogin,tvLodginTitle;

    public MainSettingWindow(final Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popwindow_mainsetting,null);//
        tvNickName = (TextView) view.findViewById(R.id.user_nickname);
        tvToLogin = (TextView) view.findViewById(R.id.tv_to_login);
        tvLodginTitle = (TextView) view.findViewById(R.id.login_title);
        signToday = (TextView) view.findViewById(R.id.sign_today);
        logingOut = (TextView) view.findViewById(R.id.tv_login_out);
        final SharedPreferences sp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);//获取从持久化数据中账户信息
        if(sp.getString(LoginActivity.NICKNAME,null) != null){
            tvToLogin.setVisibility(View.INVISIBLE);
            tvLodginTitle.setVisibility(View.INVISIBLE);
            logingOut.setVisibility(View.VISIBLE);
            tvNickName.setVisibility(View.VISIBLE);
            signToday.setVisibility(View.VISIBLE);
            logingOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("确认")
                            .setMessage("确定吗？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sp.edit().clear().commit();
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                }
            });
            nickName = sp.getString(LoginActivity.NICKNAME,null);
            Log.i("NUO",nickName);
            tvNickName.setText(nickName);
        } else {
            tvToLogin.setVisibility(View.VISIBLE);
            tvLodginTitle.setVisibility(View.VISIBLE);
            logingOut.setVisibility(View.INVISIBLE);
            signToday.setVisibility(View.INVISIBLE);
            tvNickName.setVisibility(View.INVISIBLE);
            tvToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,LoginRegisterActivity.class);
                    context.startActivity(intent);
                }
            });
        }
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(Global.SCREEN_WIDTH*3/5);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(Global.SCREEN_HEIGHT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        Log.i("LIN","shezhihaole");
        //设置SelectPicPopupWindow弹出窗体动画效果
        //  this.setAnimationStyle(R.style.AnimBottom);
        view.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int width = view.findViewById(R.id.login_or_not).getRight();
        int x =(int) event.getX();
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(x > width){
                //销毁弹出框
                Log.i("YI","消失");
                WindowManager.LayoutParams params= ((Activity)context).getWindow().getAttributes();
                params.alpha=1f;
                ((Activity)context).getWindow().setAttributes(params);
                Log.i("YI","界面恢复");
                Log.i("YI","消失");
                dismiss();

            }
        }
        return true;
    }
}
