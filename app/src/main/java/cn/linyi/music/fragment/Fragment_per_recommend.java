package cn.linyi.music.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.linyi.music.R;

/**
 * Created by linyi on 2016/3/30.
 */
public class Fragment_per_recommend extends Fragment {
    float start=0,end=0;
private LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.fragment_per_recommend,container,false);
        Log.i("LIN",view.getId()+"");
        linearLayout = ((LinearLayout) view.findViewById(R.id.fragment_per_recommend));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        start = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        end = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        end = event.getY();
                        Log.i("NUO","end:"+(end-start));
/*                        LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.topMargin =(int)( end - start);
                        Log.i("NUO","end:"+(int)(end-start));
                       linearLayout.setLayoutParams(layoutParams);*/
                        end = 0;
                        start = 0;
                }


                return true;
            }
        });
        return view;
    }

}
