package cn.linyi.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import cn.linyi.music.bean.Music;
import cn.linyi.music.fragment.Fragment_songs_list;
import cn.linyi.music.util.MusicAdapter;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,AdapterView.OnItemClickListener{
    private boolean isstop = false;//activity界面运行
    ServiceConnection sc;
    private Handler handler = null;

    private boolean isContinue = false;//继续播放
    private ImageButton imageButton;
    private SeekBar progress;
    private TextView curtime;
    private TextView duration;
    private TextView curmusic;
    private ListView musicView;
    private List<Music> musicList = null;


    private Intent service;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private PlayService.MyBinder mb;
    private boolean isChanging = false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("LIN","activity on creat");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        service = new Intent("myservice");
        service.setPackage("cn.linyi.service_01");
        ((MusicList)getApplicationContext()).setPlayService(service);//设置全局变量 以保证全局只有一个service 实例
        handler = new Handler();
        findViews();
        progress.setOnSeekBarChangeListener(this);
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("LIN", "service connected");
                mb = (PlayService.MyBinder) service;
                progress.setMax(mb.getDuration());//定义进度条最大长度
                //----------定时器记录播放进度---------//
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
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
                mTimer.schedule(mTimerTask, 0, 10);

            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("LIN", "service disconnected");
            }
        };

        this.bindService(service, sc, Context.BIND_AUTO_CREATE);//bind只发生一次
        Log.i("LIN", "bindservice");
/*
* Fragment 替换
* */
        Fragment_songs_list fragment_songs_list = new Fragment_songs_list();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_songs_list,fragment_songs_list).commit();//务必commi

    }

//获取activity中的元素
    public void findViews() {
        imageButton = (ImageButton) findViewById(R.id.play);
        musicView = (ListView) findViewById(R.id.musicView);
        curtime = (TextView) findViewById(R.id.curtime);
        curmusic = (TextView) findViewById(R.id.curmusic);
        duration = (TextView) findViewById(R.id.duration);
        progress = (SeekBar) findViewById(R.id.progress);
    }



    public void play(View view) {
        if (!isContinue) {
            service.putExtra("action", PlayService.PLAY_PASUE);
            startService(service);
            isContinue = true;
        } else {
            service.putExtra("action",  PlayService.PLAY_PASUE);
            startService(service);
            isContinue = false;
            handler.post(runnableUi);
        }

    }

    private String getMusicTime(int musicTime) {
        musicTime = musicTime / 1000;
        return musicTime / 60 + ":" + musicTime % 60;
    }

    public void next(View view) {

        service.putExtra("action",  PlayService.NEXT_SONG);
        startService(service);
        handler.post(runnableUi);
    }

    public void previous(View view) {

        service.putExtra("action",  PlayService.PREVIOUS_SONG);
        startService(service);
        handler.post(runnableUi);
        // progress.setMax(mb.getDuration());//定义进度条最大长度
    }


    protected void onStart(){
        super.onStart();
        Log.i("LIN", "activity on start");
        isstop=false;
    }


    @Override
    public void onStop() {
        super.onStop();
        //must unbind the service otherwise the ServiceConnection will be leaked.
        Log.i("LIN","ACTIVITY is Stop");
        isstop=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);

        Log.i("LIN", "activity on destroy");
        stopService(service);
    }

    public void scan(View view) {
       // startService(service);
        musicList = mb.getMusicList();
        MusicAdapter adapter = new MusicAdapter(this,musicList);
        musicView.setAdapter(adapter);
        musicView.setOnItemClickListener(this);
    }

    //SeekbarLinsen
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        curtime.setText(getMusicTime(seekBar.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isChanging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        service.putExtra("seekmsec", seekBar.getProgress());
        service.putExtra("action",  PlayService.SEEKBAR_CHANG);
        startService(service);
        isChanging = false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        service.putExtra("action", PlayService.MUSICLIST_PLAY);
        service.putExtra("current", position);
        Log.i("LIN", "MAINLIST" + position);
        service.putExtra("musicType", PlayService.LOCAL_MUSIC);
        if(((MusicList)getApplicationContext()).getPlayService().getIntExtra("musicType",-1) == 1){
            Log.i("LIN","用的是一个实例 引用");
        }else{
            Log.i("LIN","用的不是一个实例 引用");
        }
        Log.i("LIN", "current:" + position);
        startService(service);
        handler.post(runnableUi);
    }


    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            curtime.setText(getMusicTime(mb.getProgress()));
            if (!curmusic.getText().equals(mb.getCurMusic())) {
                curmusic.setText(mb.getCurMusic());

            }

            if (!duration.getText().equals(getMusicTime(mb.getDuration()))) {
                progress.setMax(mb.getDuration());
                duration.setText(getMusicTime(mb.getDuration()));
                Log.i("LIN", duration.getText() + " ");
            }
            if (mb.isplaying()) {
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.pasue));
            } else
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.play));
        }
    };

}

