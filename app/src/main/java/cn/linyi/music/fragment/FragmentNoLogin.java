package cn.linyi.music.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.linyi.music.R;

/**
 * Created by linyi on 2016/5/19.
 */
public class FragmentNoLogin extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framlayout_to_login,container,false);
        return view;
    }
}
