package cn.linyi.music.adpter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.linyi.music.R;
import cn.linyi.music.bean.Music;
import cn.linyi.music.util.Global;
import cn.linyi.music.view.MusicListView;

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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_musiclist, null);
        }
        MusicListView musicListView = (MusicListView) convertView.findViewById(R.id.music_list_view);
        Log.i("NUO","musicList.size()"+musicList.size());
        Log.i("NUO","musicList.size()"+musicList.size());
        if(musicList.get(position).getPath().equals(Global.getCurrMusicList().get(Global.getCurrentMusicIndex()).getPath())) {
      /*      ImageView imageView = new ImageView(convertView.getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setImageResource(R.drawable.small_red);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setLayoutParams(layoutParams);
            ((LinearLayout)convertView).addView(imageView);*/
            musicListView.changData(musicList.get(position),Global.SCREEN_WIDTH/5*4,true);
        } else {
            musicListView.changData(musicList.get(position),Global.SCREEN_WIDTH/5*4,false);
        }
       // Log.i("WANG",musicList.get(position).getPath()+"adapter");
      /*  TextView musicTitle = (TextView) convertView.findViewById(R.id.music_title);
        TextView musicArtist = (TextView) convertView.findViewById(R.id.music_artist);

        if(position == Global.getCurrentMusicIndex()) {
            musicTitle.setTextColor(Color.RED);
            musicArtist.setTextColor(Color.RED);
            //imageView.setImageResource(R.drawable.small_red);
        } else {
            musicTitle.setTextColor(Color.BLACK);
            musicArtist.setTextColor(Color.BLACK);
            //imageView.setImageResource(R.drawable.music_isnotplaying);
        }
        musicTitle.setText(musicList.get(position).getTitle());
        musicArtist.setText(musicList.get(position).getArtist());*/
        return convertView;
    }

}
