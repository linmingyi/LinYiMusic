package cn.linyi.music.adpter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.linyi.music.R;
import cn.linyi.music.bean.Music;
import cn.linyi.music.util.Global;

/**
 * Created by linyi on 2016/4/11.
 */
public class MusicListAdapter extends BaseAdapter {
    private List<Music> musicList;
    private LayoutInflater inflater;

    public MusicListAdapter(LayoutInflater inflater, List<Music> musicList) {
        this.musicList = musicList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return musicList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_musiclist,null);
        }
        TextView musicTitle = (TextView)convertView.findViewById(R.id.music_title);
        TextView musicArtist = (TextView)convertView.findViewById(R.id.music_artist);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.music_isPlaying);
        if(position == Global.getCurrentMusicIndex()) {
            musicTitle.setTextColor(Color.RED);
            musicArtist.setTextColor(Color.RED);
            imageView.setImageResource(R.drawable.small_red);
        } else {
            musicTitle.setTextColor(Color.BLACK);
            musicArtist.setTextColor(Color.BLACK);
            imageView.setImageResource(R.drawable.music_isnotplaying);
        }
        musicTitle.setText(musicList.get(position).getTitle());
        musicArtist.setText(musicList.get(position).getArtist());
        return convertView;
    }

}
