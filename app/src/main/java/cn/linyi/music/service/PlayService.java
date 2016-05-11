package cn.linyi.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.linyi.music.Dao.MusicDao;
import cn.linyi.music.bean.Music;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.MusicUtil;

public class PlayService extends Service {
    private MusicDao musicDao;
    private MediaPlayer mediaplayer;
    private boolean isplaying = false;
    private List<Music> musicList;
    private int musicType = LOCAL_MUSIC;
    private int current = 0;
    private Timer mTimer;
    /*
    * android常量调用为 类名.常量名
    * */
    public static final int PLAY_PASUE = 1;
    public static final int NEXT_SONG = 2;
    public static final int PREVIOUS_SONG = 3;
    public static final int SEEKBAR_CHANG = 4;
    public static final int MUSICLIST_PLAY = 5;
    /*
    *播放的音乐类型
    * */
    public static final int LOCAL_MUSIC = 1;
    public static final int ONLINE_MUSIC = 2;

    //自定义binder 用于与主线程进行数据通信
    public class MyBinder extends Binder{
        public int getProgress(){
            return   mediaplayer.getCurrentPosition();
        }

        public  int getDuration(){
            return mediaplayer.getDuration();
        }

        public  List<Music> getMusicList(){
            return musicList;
        }

        public boolean isplaying(){
            return mediaplayer.isPlaying();
        }

        public  Music getCurMusic(){
            return musicList.get(current);
        }

        public MediaPlayer getMediaPlayer() {
            return mediaplayer;
        }
    }

    //初始化应用
    @Override
    public void onCreate() {
        Log.i("LIN","service on creat");
        super.onCreate();
        init();
    }
/*
* 应用启动时即开启Playservice服务，
* 在服务初始化阶段所有的播放信息均从数据库中读取，然后加载到musicmainsActivity中去
* 在应用退出时将播放信息保存到数据库中。
* 要保存的信息有 当前播放列表的id 列表的总数量 上一次播放的音乐的id progress
* 还有音乐类型 path;都要加载好
*
* */
    //初始化 mediaPlayer
    private void init(){
        musicDao = new MusicDao(this);
        musicList = MusicUtil.findAllMp3(musicDao);
        Music m = MusicUtil.getLastMusic(musicDao);
        //position为music 在播放列表中的位置
        current = m.getPosition();
        Global.setLocalMusicList(musicList);
        Log.i("NUOYI", Global.getLocalMusicList().size()+"local size");
        Global.setCurrMusicList(musicList);
        Global.setMusicType(LOCAL_MUSIC);//musicType 应该保存到数据库中以保留退出时的播放列表
        Global.setCurrentMusicIndex(current);
        Log.i("LIN",musicList.size()+"MUSICLIST>SIZE_______________________________");
        Log.i("YI",Global.getCurrMusicList().size()+"global.size  \n\t hashcode"+Global.getCurrMusicList().hashCode());
        Log.i("LIN","creat时的值是："+current);
        mediaplayer = new MediaPlayer();
        try {
            mediaplayer.reset();
            mediaplayer.setDataSource(getPath(current));
            mediaplayer.prepare();
            mediaplayer.seekTo(m.getProgress());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!mediaplayer.isPlaying()) {
                    Log.i("LIN", mediaplayer.isPlaying() + "isplaying");
                    current = (++current) % musicList.size();
                    Log.i("LIN", current + "   current");
                    play(current);
                    Log.i("LIN", mediaplayer.isPlaying() + "isplaying");
                }
            }
        });
        mTimer = new Timer();
        TimerTask mTimertask = new TimerTask() {
            @Override
            public void run() {
                sendProgress(mediaplayer.getCurrentPosition());
            }
        };
        mTimer.schedule(mTimertask,0,500);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LIN","service on startcommand");
        if(intent == null) {Log.i("LIN","intent wei kong ");}
        int action = intent.getIntExtra("action",-1);
        musicType = intent.getIntExtra("musicType",-1);
        Log.i("LIN", "MUsictype\n" + musicType);
        if(musicType == ONLINE_MUSIC) {
            musicList = Global.getOnlineMusicList();
            Log.i("LIN","ONLINE music play");
        } else  if (musicType == LOCAL_MUSIC) {
            musicList =  Global.getLocalMusicList();
        }
        Log.i("YI",musicList.size()+"set 前的musicList");
        Global.setCurrMusicList(musicList);
        Log.i("YI",Global.getCurrMusicList().size()+"global.size  \n\t hashcode"+Global.getCurrMusicList().hashCode());
        musicPlay(action, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendProgress(int progress){
        Intent intent = new Intent();
        intent.setAction("progress");
        intent.putExtra("progress",progress);
        sendBroadcast(intent);
    }


    public void musicPlay(int action,Intent intent){
        switch (action){
            //播放、暂停
            case PLAY_PASUE:
                if(!mediaplayer.isPlaying()){
                    Log.i("LIN", mediaplayer.isPlaying() + "isnotplaying!!!");
                    mediaplayer.start();
                } else {
                    Log.i("LIN", mediaplayer.isPlaying() + "isplaying++++");
                    mediaplayer.pause();
                }
                break;
            //下一首
            case NEXT_SONG:
                current = (++current)%musicList.size();
                Log.i("LIN",current+"   current");
                play(current);
                break;
            //上一首
            case PREVIOUS_SONG:
                current = (--current+musicList.size())%musicList.size();
                Log.i("LIN",current+"   current");
                play(current);
                break;
            //拖动进度条
            case SEEKBAR_CHANG:
                mediaplayer.seekTo(intent.getIntExtra("seekmsec",mediaplayer.getCurrentPosition()));
                Log.i("LIN", intent.getIntExtra("seekmsec", mediaplayer.getCurrentPosition())
                        + "   seekmsec");
                mediaplayer.start();
                break;
            //列表播放
            case MUSICLIST_PLAY:
                current = intent.getIntExtra("current",0);
                Log.i("LIN","service current:"+current);
                play(current);
                break;
        }
    }

    //播放当前列表中的第i首歌曲//此时的i是最终要播放的歌曲的序号
    public void play(int i) {
            mediaplayer.stop();
            mediaplayer.reset();
        if (musicType == ONLINE_MUSIC) {
            mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
            try {
                mediaplayer.setDataSource(getPath(i));
                // mediaplayer.setOnPreparedListener(preparedListener);
                mediaplayer.prepare();
                mediaplayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        Global.setCurrentMusicIndex(i);
        Log.i("LIN","PATH:\n"+getPath(i));
        Log.i("LIN", "next song is " + mediaplayer.isPlaying());
    }


//绑定服务
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("LIN","service onBind");
        return new MyBinder();
    }

    //获取音乐地址
    private String getPath(int i) {
        return musicList.get(i).getPath();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("LIN","解除绑定");
        Log.i("LIN","service on Unbind");
        Music music = musicList.get(current);
        music.setProgress(mediaplayer.getCurrentPosition());
        mediaplayer.stop();
        musicDao.updateData(1, current, music.getProgress(),music.getPath());
        Log.i("LIN", "最后一首是" + current + music.getTitle() + "播放进度是" + music.getProgress());
        mediaplayer.release();
        return super.onUnbind(intent);
    }

    /*终止服务时将最后一次的播放信息保存起来*/
    @Override
    public void onDestroy() {
        if( mediaplayer != null) {
            mediaplayer = null;
        }
        mTimer.cancel();
        Log.i("LIN", "service销毁了");
        super.onDestroy();
    }
}
