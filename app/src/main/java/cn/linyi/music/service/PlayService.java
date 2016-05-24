package cn.linyi.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.linyi.music.Dao.MusicDao;
import cn.linyi.music.MusicMainActivity;
import cn.linyi.music.bean.Music;
import cn.linyi.music.bean.MusicType;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.MusicUtil;

public class PlayService extends Service {
    private MusicDao musicDao;
    private MediaPlayer mediaplayer;
    private boolean isplaying = false;
    private List<Music> musicList;
    private int musicType = Music.LOCAL_MUSIC;
    private int current = 0;
    private Timer mTimer;
    private boolean onstart = false;
    /*
    * android常量调用为 类名.常量名
    * */
    public static final int PLAY_PASUE = 1;
    public static final int NEXT_SONG = 2;
    public static final int PREVIOUS_SONG = 3;
    public static final int SEEKBAR_CHANG = 4;
    public static final int MUSICLIST_PLAY = 5;
    private int duration;//
    private int currentPosition;


    //自定义binder 用于与主线程进行数据通信
    public class MyBinder extends Binder{
        public int getProgress(){
            int progress;
            try{
                progress = mediaplayer.getCurrentPosition();
            }catch (Exception e){
                progress = 0;
            }
            return  progress;
        }

        public  int getDuration(){
           try{
               if(musicList.get(current).getDuration() == 0) {
               musicList.get(current).setDuration(mediaplayer.getDuration());
               musicDao.updateData(musicList.get(current));
                }
           } catch (Exception e){
               Log.i("NUO","mediaplayer is not prepared");
           }
            return musicList.get(current).getDuration();
        }

        public  List<Music> getMusicList(){
            return musicList;
        }

        public boolean isplaying(){
            try {
                return mediaplayer.isPlaying();
            }catch (Exception e){

            }
            return false;
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
        mediaplayer = new MediaPlayer();
        if(Global.getCurrMusicList().size()>0) {
            musicList = Global.getCurrMusicList();
            current = Global.getCurrentMusicIndex();

            Music m = musicList.get(Global.getCurrentMusicIndex());
            Log.i("NUO", Global.getLocalMusicList().size() + "local size");
         //   Global.setMusicType(Music.LOCAL_MUSIC);//musicType 应该保存到数据库中以保留退出时的播放列表
            Log.i("NUO", musicList.size() + "MUSICLIST>SIZE______________________");
            Log.i("NUO", Global.getCurrMusicList().size() + "global.size  \n\t hashcode" + Global.getCurrMusicList().hashCode());
            Log.i("NUO", m.getDuration()+"diration"+m.getProgress()+"creat时的值是：" + current);
            try {
                mediaplayer.setDataSource(m.getPath());
                mediaplayer.prepare();
                mediaplayer.seekTo(m.getProgress());
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    duration = mp.getDuration();
                   if(onstart) {
                         mp.start();//确保进入时不播放。只有通过点击才开始播放
                   }

                }
            });
            mediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.i("WANG","what:"+what+"extra:"+extra);
                 /*   mp.reset();
                    play(current);*/
                    return true;

                }
            });
        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!mediaplayer.isPlaying() && onstart) {
                    Log.i("NUO", mediaplayer.isPlaying() + "completion     ");
                    current = (++current) % musicList.size();
                    Log.i("NUO", current + "   current");
                    Log.i("NUO", mediaplayer.isPlaying() + "completion isplaying");
                   // play(current);
                    Global.setCurrentMusicIndex(current);
                    mediaplayer.stop();
                    mediaplayer.reset();
                try {
                    mediaplayer.setDataSource(getPath(current));
                    mediaplayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("LIN","path is invalid");
                }
                Log.i("LIN", mediaplayer.isPlaying() + "completion isplaying");

                }
            }
        });
       /* mTimer = new Timer();
        TimerTask mTimertask = new TimerTask() {
            @Override
            public void run() {
                sendProgress(mediaplayer.getCurrentPosition());
            }
        };
        mTimer.schedule(mTimertask,0,500);*/
    }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onstart = true;
        Log.i("NUO","service on startcommand");
        if(intent == null) {Log.i("LIN","intent wei kong ");}
        int action = intent.getIntExtra("action",-1);
        musicType = intent.getIntExtra("musicType",-1);
        Log.i("NUO", "MUsictype\n" + musicType);
       musicList = Global.getCurrMusicList();
        Log.i("NUO",musicList.size()+"set 前的musicList");
        Log.i("NUO",Global.getCurrMusicList().size()+"global.size  \n\t hashcode"+Global.getCurrMusicList().hashCode());
        musicPlay(action, intent);
        Log.i("NUO",action+"：action");
        return super.onStartCommand(intent, flags, startId);
    }

    public void musicPlay(int action,Intent intent){
        switch (action){
            //播放、暂停
            case PLAY_PASUE:
                if(!mediaplayer.isPlaying()){
                    Log.i("NUO", mediaplayer.isPlaying() + "isnotplaying!!!");
                    mediaplayer.start();
                } else {
                    Log.i("NUO", mediaplayer.isPlaying() + "isplaying++++");
                    mediaplayer.pause();
                }
                break;
            //下一首
            case NEXT_SONG:
               playNext();
                break;
            //上一首
            case PREVIOUS_SONG:
                playPre();
                break;
            //拖动进度条
            case SEEKBAR_CHANG:
                mediaplayer.seekTo(intent.getIntExtra("seekmsec",mediaplayer.getCurrentPosition()));
                Log.i("NUO", intent.getIntExtra("seekmsec", mediaplayer.getCurrentPosition())
                        + "   seekmsec");
                mediaplayer.start();
                break;
            //列表播放
            case MUSICLIST_PLAY:
                current = intent.getIntExtra("current",0);
                Log.i("NUO","service current:"+current);
                play(current);
                break;
        }
    }

    private void playNext() {
        switch (Global.getPlayingMode()) {
            case MusicMainActivity.LOOP_MODE :
                current = (++current + musicList.size())%musicList.size();
                break;
            case MusicMainActivity.RADOM_MODE :
                current = (int) (Math.random() * musicList.size());
                break;
            case MusicMainActivity.SINGLE_MODE :
                break;
        }
        play(current);
    }

    private void playPre() {
        switch (Global.getPlayingMode()) {
            case MusicMainActivity.LOOP_MODE :
                current = (--current + musicList.size())%musicList.size();
                break;
            case MusicMainActivity.RADOM_MODE :
                current = (int) (Math.random() * musicList.size());
                break;
            case MusicMainActivity.SINGLE_MODE :
                break;
        }
        play(current);
    }


    //播放当前列表中的第i首歌曲//此时的i是最终要播放的歌曲的序号
    public synchronized void play(int i) {
                mediaplayer.stop();
                mediaplayer.reset();
                if(musicType == Music.ONLINE_MUSIC) {
                    mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//在线音乐播放
                }
                try {
                    Log.i("NUO","PATH:\n"+getPath(i));
                    mediaplayer.setDataSource(getPath(i));
                    // mediaplayer.setOnPreparedListener(preparedListener);
                    //mediaplayer.setDataSource("http:\\/\\/linyinuo.site\\/Music\\/We Are One.mp3");
                    mediaplayer.prepare();
                    mediaplayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
            }
        Global.setCurrentMusicIndex(i);

        Log.i("NUO", "next song is " + mediaplayer.isPlaying());
    }


//绑定服务
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("NUO","service onBind");
        return new MyBinder();
    }

    //获取音乐地址
    private String getPath(int i) {
        return musicList.get(i).getPath();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("NUO","解除绑定");
        Log.i("NUO","service on Unbind");
        Music music = musicList.get(current);
        music.setProgress(mediaplayer.getCurrentPosition());
        mediaplayer.stop();
        musicDao.updateData(1, current, music.getProgress(),music.getPath());
        Log.i("NUO", "最后一首是" + current + music.getTitle() + "播放进度是" + music.getProgress());
        mediaplayer
    .release();
    // mTimer.cancel();
    return super.onUnbind(intent);
}
    /*终止服务时将最后一次的播放信息保存起来*/
    @Override
    public void onDestroy() {
        if( mediaplayer != null) {
            mediaplayer = null;
        }
        if(mTimer !=null) {
        mTimer.cancel();
        }
        Log.i("NUO", "service销毁了");
        super.onDestroy();
    }
}
