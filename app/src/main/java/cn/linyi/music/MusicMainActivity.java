package cn.linyi.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.linyi.music.adpter.LyricAdapter;
import cn.linyi.music.bean.Lyric;
import cn.linyi.music.bean.Music;
import cn.linyi.music.service.PlayService;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.LrcUtil;
import cn.linyi.music.view.LyricListView;

public class MusicMainActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView listView;
    private List<Lyric> lyricList;
    private Intent service;
    private boolean isOnTouch;//设置互斥变量
    private boolean isstop = false;//activity界面运行
    private BroadcastReceiver receiver;
    private ImageButton btnBack, btnShare;
    private Handler handler;
    private TextView musicTitle, musicArtist, curMusicTime, curMusicDuration;
    private SeekBar progress;
    private boolean isChanging = false;
    private PlayService.MyBinder mb;//绑定的service服务
    private ServiceConnection sc;

    private TextView musicPlayPre, musicPlay, musicPlayNext, musicPlayList;
    private Music currMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_main);
        init();
        service = Global.getPlayService();
        bindPlayService();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int indext = (int) msg.getData().get("index");
                listView.setSelection(indext);
            }
        };
        currMusic = Global.getCurrMusicList().get(Global.getCurrentMusicIndex());
        lyricList = LrcUtil.getLyrics(currMusic.getPath());
        Log.i("YI", lyricList.size() + "");
        final LyricAdapter lyricAdapter = new LyricAdapter(this.getLayoutInflater(), lyricList);
        listView.setAdapter(lyricAdapter);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int progress = intent.getIntExtra("progress", 0);
                //换歌曲的时候同时换歌词
                if (!currMusic.equals(Global.getCurrMusicList().get(Global.getCurrentMusicIndex()))) {
                    currMusic = Global.getCurrMusicList().get(Global.getCurrentMusicIndex());
                    lyricList.clear();
                    lyricList.addAll(LrcUtil.getLyrics(currMusic.getPath()));
                    lyricAdapter.notifyDataSetChanged();
                }

                for (int i = 0; i < lyricList.size(); i++) {
                    if (progress / 1000 == lyricList.get(i).getBeginTime()) {
                        Log.i("YI", "ListViewHeight" + listView.getHeight());
                        Message message = new Message();
                        Bundle b = new Bundle();
                        if (i >= 4) {
                            b.putInt("index", i - 4);
                        } else {
                            b.putInt("index", 0);
                        }
                        message.setData(b);
                        handler.sendMessage(message);
                        lyricList.get(i).setIscenter(true);
                        Log.i("YI", "LRC的第" + i);
                        lyricAdapter.notifyDataSetChanged();
                    } else {
                        lyricList.get(i).setIscenter(false);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("progress");
        registerReceiver(receiver, filter);
    }

    private void init() {
        musicTitle = (TextView) findViewById(R.id.tv_musicmain_title);
        musicArtist = (TextView) findViewById(R.id.tv_musicmain_artist);
        curMusicTime = (TextView) findViewById(R.id.tv_musicmain_curtime);
        curMusicDuration = (TextView) findViewById(R.id.tv_musicmain_duration);
        btnBack = (ImageButton) findViewById(R.id.btn_musicmain_back);
        btnShare = (ImageButton) findViewById(R.id.btn_musicmain_share);
        listView = (ListView) findViewById(R.id.music_lrc);
        musicPlayPre = (TextView) findViewById(R.id.tv_musicmain_playpre);
        musicPlay = (TextView) findViewById(R.id.tv_musicmain_play);
        musicPlayNext = (TextView) findViewById(R.id.tv_musicmain_playnext);
        musicPlayList = (TextView) findViewById(R.id.tv_musicmain_playlist);
        progress = (SeekBar) findViewById(R.id.sb_musicmain_progress);
        musicPlayPre.setOnClickListener(this);
        musicPlay.setOnClickListener(this);
        musicPlayNext.setOnClickListener(this);
        musicPlayList.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(receiver);
        unbindService(sc);
        Log.i("YI", "DEstroy MUSIC main ");
        super.onDestroy();
        //当Activity销毁的时候取消注册BroadcastReceiver

    }

    private String getMusicTime(int musicTime) {
        musicTime = musicTime / 1000;
        return musicTime / 60 + ":" + musicTime % 60;
    }

    private void releaseImageView(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        if (d != null)
            d.setCallback(null);
        imageView.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_musicmain_playpre:
                service.putExtra("action",  PlayService.PREVIOUS_SONG);
                startService(service);
                break;
            case R.id.tv_musicmain_playnext:
                service.putExtra("action",  PlayService.NEXT_SONG);
                startService(service);
                break;
            default:
                break;
        }
    }

    public void bindPlayService(){
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("LIN", "service connected");
                mb = (PlayService.MyBinder) service;
                progress.setMax(mb.getDuration());//定义进度条最大长度
                //----------定时器记录播放进度---------//
                Timer mTimer = new Timer();
               TimerTask mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (isChanging) {
                            return;
                        }
                        if (!isstop) {
                            progress.setProgress(mb.getProgress());
                            handler.post(runnableUi);
                            // if(mb.getProgress()==mb.getDuration()) nextMusic();
                        }
                    }
                };
                mTimer.schedule(mTimerTask, 0, 100);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("LIN", "service disconnected");
            }
        };
        this.bindService(service, sc, Context.BIND_AUTO_CREATE);//bind只发生一次
    }

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            musicTitle.setText(mb.getCurMusic().getTitle());
            musicArtist.setText(mb.getCurMusic().getArtist());
            curMusicTime.setText(getMusicTime(mb.getProgress()));
            curMusicDuration.setText(getMusicTime(mb.getDuration()));
        }
    };

}
