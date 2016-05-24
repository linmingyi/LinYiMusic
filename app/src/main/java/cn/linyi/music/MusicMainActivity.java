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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


public class MusicMainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{
    private LyricListView lyricListView;
    private List<Lyric> lyricList;
    private LyricAdapter lyricAdapter;
    private Intent service;
    private ImageButton btnBack, btnShare;
    private Handler handler;
    private TextView musicTitle, musicArtist, curMusicTime;
    private TextView  curMusicDuration;
    private SeekBar progress;
    private boolean isOnTouch;//设置互斥变量
    private boolean isstop = false;//activity界面运行
    private boolean isChanging = false;
    private PlayService.MyBinder mb;//绑定的service服务
    private ServiceConnection sc;

    private ImageView musicPlayPre, musicPlay, musicPlayNext, musicPlayList,playingMode;
    private Music currMusic;
    private Timer mTimer;
    private LinearLayout linearLayout;

    public static final int LOOP_MODE = 1;//循环播放
    public static final int RADOM_MODE = 2;//随机播放
    public static final int SINGLE_MODE = 3;//单曲循环

    private int lastPlayMode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_main);
        init();
        service = Global.getPlayService();
        bindPlayService();
       /* lyricAdapter = new LyricAdapter(this.getLayoutInflater(), lyricList);
        listView.setAdapter(lyricAdapter);*/
        handler = new Handler();
    }

//
    @Override
    protected void onResume() {
        lyricList = LrcUtil.getLyrics(Global.getCurrMusicList().get(Global.getCurrentMusicIndex()).getPath());
        Log.i("NUO","onresume:height:"+lyricListView.getHeight());
        lyricListView.changeData(lyricList,0,lyricListView.getHeight());
        super.onResume();
    }

    private void init() {
        linearLayout = (LinearLayout) findViewById(R.id.ly_musicmain);
        musicTitle = (TextView) findViewById(R.id.tv_musicmain_title);
        musicArtist = (TextView) findViewById(R.id.tv_musicmain_artist);
        curMusicTime = (TextView) findViewById(R.id.tv_musicmain_curtime);
        curMusicDuration = (TextView) findViewById(R.id.tv_musicmain_duration);
        btnBack = (ImageButton) findViewById(R.id.btn_musicmain_back);
        btnShare = (ImageButton) findViewById(R.id.btn_musicmain_share);

        lyricListView = (LyricListView) findViewById(R.id.music_lrc);

        musicPlayPre = (ImageView) findViewById(R.id.tv_musicmain_playpre);
        musicPlay = (ImageView) findViewById(R.id.tv_musicmain_play);
        musicPlayNext = (ImageView) findViewById(R.id.tv_musicmain_playnext);
        musicPlayList = (ImageView) findViewById(R.id.tv_musicmain_playlist);
        playingMode = (ImageView) findViewById(R.id.playing_mode);
        progress = (SeekBar) findViewById(R.id.sb_musicmain_progress);
        progress.setOnSeekBarChangeListener(this);
        btnShare.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        musicPlayPre.setOnClickListener(this);
        musicPlay.setOnClickListener(this);
        musicPlayNext.setOnClickListener(this);
        musicPlayList.setOnClickListener(this);
        playingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (lastPlayMode) {
                    case LOOP_MODE :
                        playingMode.setImageResource(R.drawable.play_icn_shuffle);
                        Toast.makeText(MusicMainActivity.this,"随机播放",Toast.LENGTH_SHORT).show();
                        Global.setPlayingMode(RADOM_MODE);
                        lastPlayMode = RADOM_MODE;
                        break;
                    case RADOM_MODE :
                        playingMode.setImageResource(R.drawable.play_icn_one);
                        Toast.makeText(MusicMainActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
                        Global.setPlayingMode(SINGLE_MODE);
                        lastPlayMode = SINGLE_MODE;
                        break;
                    case SINGLE_MODE :
                        playingMode.setImageResource(R.drawable.play_icn_loop);
                        Toast.makeText(MusicMainActivity.this,"列表循环",Toast.LENGTH_SHORT).show();
                        Global.setPlayingMode(LOOP_MODE);
                        lastPlayMode = LOOP_MODE;
                        break;
                }
            }
        });
    }






    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_musicmain_playpre:
                doService(PlayService.PREVIOUS_SONG);
                break;
            case R.id.tv_musicmain_playnext:
                doService(PlayService.NEXT_SONG);
                break;
            case R.id.tv_musicmain_play:
                doService(PlayService.PLAY_PASUE);
                break;
            case R.id.btn_musicmain_back:
                Log.i("YI","finish() is called");
                this.finish();
                break;
            case R.id.btn_musicmain_share:
                shareMusic();
                break;
            default:
                break;
        }
        handler.post(runnableUi);
    }

//根据传入的ActionName 向PlaYservuce发送信息
    private void doService(int ActionName){
            service.putExtra("action",ActionName);
            startService(service);
    }


//分享歌曲，应该单独拿出来，应为不仅仅这一个地方会用到
    private void shareMusic() {
            ShareSDK.initSDK(this);
            OnekeyShare oks = new OnekeyShare();
            //关闭sso授权
            oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
            //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(getString(R.string.share));
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
            oks.setTitleUrl("http://sharesdk.cn");
            // text是分享文本，所有平台都需要这个字段
            oks.setText("我正在使用林悦音乐听" + Global.getCurrMusicList().get(Global.getCurrentMusicIndex()).getTitle());
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
            // url仅在微信（包括好友和朋友圈）中使用
           // oks.setUrl("http://sharesdk.cn");
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
            //oks.setComment("我是测试评论文本");
            // site是分享此内容的网站名称，仅在QQ空间使用
            //oks.setSite(getString(R.string.app_name));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            //oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
            oks.show(this);
        }





    //在activity　的oncreat是绑定服务。
    public void bindPlayService(){
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
               // Log.i("NUO", "service connected");
                mb = (PlayService.MyBinder) service;
                progress.setMax(mb.getDuration());//定义进度条最大长度
                currMusic = Global.getCurrMusicList().get(Global.getCurrentMusicIndex());
                lyricList = LrcUtil.getLyrics(currMusic.getPath());
                updateLyric();
                //----------定时器记录播放进度---------//
                if (mTimer == null) {
                    mTimer = new Timer();
                }
                TimerTask mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (isChanging) {
                            return;
                        }
                        if (!isstop) {
                            progress.setProgress(mb.getProgress());
                         //   Log.i("YI","musicMain 更新progress");
                            handler.post(runnableUi);
                            // if(mb.getProgress()==mb.getDuration()) nextMusic();
                        }
                       // Log.i("YI","musicMain 不更新但是还在用progress");
                    }
                };
                mTimer.schedule(mTimerTask, 0, 100);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
               // Log.i("LIN", "service disconnected");
            }
        };
        this.bindService(service, sc, Context.BIND_AUTO_CREATE);//bind只发生一次
    }



    @Override
    protected void onPause() {
        isstop = true;
        super.onPause();
    }

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            musicTitle.setText(mb.getCurMusic().getTitle());
            musicArtist.setText(mb.getCurMusic().getArtist());
            //curMusicDuration.setText(getMusicTime(mb.getDuration()));
            if(mb.isplaying()){
                musicPlay.setImageDrawable(getResources().getDrawable(R.drawable.play_btn_pause));
            }else {
                musicPlay.setImageDrawable(getResources().getDrawable(R.drawable.play_btn_play));
            }
            updateLyric();
            Global.updateSeekBarMax(progress,mb.getDuration());
            Global.updateDuration(curMusicDuration,mb.getDuration());
        }
    };

    public void updateLyric(){
        //换歌曲的时候同时换歌词
        if (!currMusic.equals(Global.getCurrMusicList().get(Global.getCurrentMusicIndex()))) {
            currMusic = Global.getCurrMusicList().get(Global.getCurrentMusicIndex());
            lyricList.clear();
            lyricList.addAll(LrcUtil.getLyrics(currMusic.getPath()));
            lyricListView.changeData(lyricList, 0,lyricListView.getHeight());
        }
        boolean found = false;
        int progress = mb.getProgress() / 1000;
        if(lyricList.size()< 5){
            lyricListView.changeData(lyricList,0,lyricListView.getHeight());
        } else {
            for (int i = 0; i < lyricList.size(); i++) {
                if (progress == lyricList.get(i).getBeginTime()) {
                    lyricListView.changeData(lyricList, i,lyricListView.getHeight());
                    found = true;
                    break;
                }
            }
            if(!found) {
                for (int i = 0; i<lyricList.size()-1;i++) {
                    if(progress > lyricList.get(i).getBeginTime() && progress<lyricList.get(i+1).getBeginTime()) {
                        lyricListView.changeData(lyricList, i,lyricListView.getHeight());
                        found = true;
                        break;
                    }
                }
            }
            //如果还是没有发现则一定是歌词的最后一句了
            if(!found){
                lyricListView.changeData(lyricList,lyricList.size()-1,lyricListView.getHeight());
                found =true;
            }
        }
    }

    //SeekbarLinsen
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        curMusicTime.setText(Global.getMusicTime(seekBar.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isChanging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        service.putExtra("seekmsec", seekBar.getProgress());
        service.putExtra("action", PlayService.SEEKBAR_CHANG);
        startService(service);
        isChanging = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//        unregisterReceiver(receiver);
        unbindService(sc);
       // currMusic = null;
       lyricListView = null;
        lyricList.clear();
        if(null != mTimer) {
            mTimer.cancel();
        }
        handler.removeCallbacks(runnableUi);
        releaseImageView(musicPlayPre);
        releaseImageView(musicPlay);
        releaseImageView(musicPlayNext);
        releaseImageView(musicPlayList);
        Drawable d = linearLayout.getBackground();
        if(d != null) {
           //Log.i("YI","release drawble");
            d.setCallback(null);
        }
        linearLayout.setBackgroundDrawable(null);
        Log.i("YI", "DEstroy MUSIC main ");
        super.onDestroy();
        //当Activity销毁的时候取消注册BroadcastReceiver

    }


    private void releaseImageView(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        if (d != null)
            d.setCallback(null);
        imageView.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);
    }

    private void releaseTextView(TextView textView){
        Drawable d = textView.getBackground();
        if(d != null) {
            Log.i("YI","release drawble");
            d.setCallback(null);
        }
        textView.setBackgroundDrawable(null);
        textView = null;
    }

}
