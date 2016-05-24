package cn.linyi.music.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.linyi.music.R;

/**
 * Created by linyi on 2016/5/19.
 */
public class FragmentLogined extends Fragment{
    private TextView tv_nickName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.framlayout_logined,null);
        tv_nickName = (TextView) view.findViewById(R.id.user_nickname);
        return view;
    }
}
