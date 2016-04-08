package cn.linyi.music.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.linyi.music.R;
import cn.linyi.music.bean.Music;

/**
 * Created by linyi on 2016/3/23.
 */
public class MusicAdapter extends BaseAdapter {
    private List<Music> data;
    private LayoutInflater inflater;

    public  MusicAdapter(Context context,List<Music> data){
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_music_list,null);
        TextView title = (TextView)convertView.findViewById(R.id.titile);
        TextView artist = (TextView)convertView.findViewById(R.id.artist);
        Music m = data.get(position);
        title.setText(m.getTitle());
        artist.setText(m.getArtist());
        return convertView;
    }
}
