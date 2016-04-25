package cn.linyi.music.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.linyi.music.R;
import cn.linyi.music.bean.Music;

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
        musicTitle.setText(musicList.get(position).getTitle());
        musicArtist.setText(musicList.get(position).getArtist());
        return convertView;
    }
}
