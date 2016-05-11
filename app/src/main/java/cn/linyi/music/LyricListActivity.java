package cn.linyi.music;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.linyi.music.bean.Lyric;
import cn.linyi.music.service.PlayService;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.LrcUtil;
import cn.linyi.music.view.LyricListView;

/**
 * Created by linyi on 2016/4/26.
 */
public class LyricListActivity extends Activity {
    private LyricListView lyricListView;
    private List<Lyric> lyricList;
    private ServiceConnection sc;
    private PlayService.MyBinder mb;
    private Intent service;
    private Timer mTimer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyriclist);
        service = Global.getPlayService();
        bindPlayService();
        lyricListView = (LyricListView) findViewById(R.id.lyric_list);
        Log.i("YI","GECI 页面");

    }

    @Override
    protected void onStart() {
        Log.i("NUO","top:"+  lyricListView.getWidth()+"height:"+lyricListView.getHeight());
        lyricList = LrcUtil.getLyrics(Global.getCurrMusicList().get(Global.getCurrentMusicIndex()).getPath());
        lyricListView.changeData(lyricList,0,lyricListView.getHeight());
        super.onStart();
    }

    public void bindPlayService(){
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("LIN", "service connected");
                mb = (PlayService.MyBinder) service;
                if (mTimer == null) {
                    mTimer = new Timer();
                }
                TimerTask mTimerTask = new TimerTask() {
                    @Override
                    public void run() {

                        mb.getProgress();
                        int progress = mb.getProgress();
                        if(lyricList.size()< 5){
                            lyricListView.changeData(lyricList,0,lyricListView.getHeight());
                        } else {
                            for (int i = 0; i < lyricList.size(); i++) {
                                if (progress / 1000 == lyricList.get(i).getBeginTime()) {
                                    lyricListView.changeData(lyricList, i,lyricListView.getHeight());
                                }
                            }
                        }
                        // if(mb.getProgress()==mb.getDuration()) nextMusic();
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

}
