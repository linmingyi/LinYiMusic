package cn.linyi.music.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.linyi.music.R;

/**
 * Created by linyi on 2016/3/30.
 */
public class Fragment_radio extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio,container,false);
        Log.i("LIN", view.getId() + "   " + R.layout.fragment_radio);
        return view;
    }
}
