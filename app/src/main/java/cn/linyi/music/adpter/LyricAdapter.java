package cn.linyi.music.adpter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.linyi.music.R;
import cn.linyi.music.bean.Lyric;

/**
 * Created by linyi on 2016/4/24.
 */
public class LyricAdapter extends BaseAdapter {
    private List<Lyric> lyricList;
    private LayoutInflater inflater;
    public LyricAdapter(LayoutInflater inflater,List<Lyric> lyricList){
        this.lyricList = lyricList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return lyricList.size();
    }

    @Override
    public Object getItem(int position) {
        return lyricList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       if(convertView == null){
            convertView = inflater.inflate(R.layout.adapter_lyriclist,null);
       }
        TextView lyric = (TextView) convertView.findViewById(R.id.lyric);
        lyric.setText(lyricList.get(position).getLrc());

        if(lyricList.get(position).iscenter()){
            lyric.setTextColor(Color.WHITE);
            lyric.setTextSize(18);
        } else {
            lyric.setTextColor(Color.GRAY);
            lyric.setTextSize(14);
        }
        return convertView;
    }
}
