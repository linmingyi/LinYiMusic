package cn.linyi.music;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import cn.linyi.music.adpter.MusicListAdapter;
import cn.linyi.music.service.PlayService;
import cn.linyi.music.util.Global;

/**
 * Created by linyi on 2016/5/11.
 */
public class MusicListActivity extends FragmentActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
    private ImageButton btn_back;
    private TextView tv_folderName;
    private ListView musicList;
    private MusicListAdapter musicListAdapter;
    private Intent service;
    @Override
    public void onClick(View v) {
       switch (v.getId()) {
           case R.id.btn_back :
                    finish();
               break;
       }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);//文件名要对哦
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        tv_folderName = (TextView) findViewById(R.id.tv_folderName);
        musicList = (ListView) findViewById(R.id.fragment_localmusic_list);
        service = Global.getPlayService();
        Intent intent = getIntent();
        tv_folderName.setText(intent.getStringExtra("folderName"));
        musicListAdapter = new MusicListAdapter(getLayoutInflater(), Global.getCurrFolderMusicList());
        musicList.setAdapter(musicListAdapter);
        musicList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Global.setCurrMusicList(Global.getCurrFolderMusicList());
        Log.i("NUOYI","curr+:"+Global.getCurrMusicList().size());
        service.putExtra("current", position);
        Log.i("LIN", "ITEMCLICK" + position);
        service.putExtra("action", PlayService.MUSICLIST_PLAY);
        service.putExtra("musicType", PlayService.LOCAL_MUSIC);//歌曲类型在点击是要记得修改 OK！！！
        startService(service);
    }
}
