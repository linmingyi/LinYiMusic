package cn.linyi.music.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import cn.linyi.music.R;
import cn.linyi.music.adpter.MusicListAdapter;
import cn.linyi.music.service.PlayService;
import cn.linyi.music.util.Global;

public class Fragment_LocalMusic extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private TextView musicCount;
    private MusicListAdapter musicListAdapter;
    private Intent service;

    public Fragment_LocalMusic(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_localmusic, container, false);
        listView = (ListView) view.findViewById(R.id.fragment_localmusic_list);
        musicCount = (TextView) view.findViewById(R.id.fragment_localmusic_count);
        service = Global.getPlayService();
        Log.i("NUO",Global.getLocalMusicList().size()+"首");
        musicCount.setText("共"+Global.getLocalMusicList().size()+"首");
        musicListAdapter = new MusicListAdapter(inflater, Global.getLocalMusicList());
        listView.setAdapter(musicListAdapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        service.putExtra("current", position);
        Log.i("LIN", "ITEMCLICK" + position);
        service.putExtra("action", PlayService.MUSICLIST_PLAY);
        service.putExtra("musicType", PlayService.LOCAL_MUSIC);//歌曲类型在点击是要记得修改 OK！！！
        getActivity().startService(service);
    }
}