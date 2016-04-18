package cn.linyi.music;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.linyi.music.Dao.MusicDao;
import cn.linyi.music.bean.Music;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.MP3Info;


public class PlayService extends Service {

    private MusicDao musicDao;

    private MediaPlayer mediaplayer;
    private boolean isplaying = false;
    private List<Music> musicList;
    private int musicType = LOCAL_MUSIC;
    private int current=0;

    /*
    * android常量调用为 类名.常量名
    * */
    public static final int PLAY_PASUE = 1;
    public static final int NEXT_SONG = 2;
    public static final int PREVIOUS_SONG = 3;
    public static final int SEEKBAR_CHANG = 4;
    public static final int MUSICLIST_PLAY = 5;

    public static final int LOCAL_MUSIC = 1;
    public static final int ONLINE_MUSIC = 2;

    public PlayService() {
    }

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

        public  String getCurMusic(){
            return musicList.get(current).getTitle();
        }
    }

    @Override
    public void onCreate() {
        Log.i("LIN","service on creat");
        super.onCreate();
        init();
    }

    //初始化 mediaPlayer
    private void init(){
        musicDao = new MusicDao(this);
        //   Log.i("LIN", getFilesDir().getPath() + "数据库地址0");
        musicList = findAllMp3();
        ((Global)getApplicationContext()).setLocalMusicList(musicList);
        ((Global)getApplicationContext()).setCurrMusicList(musicList);
        Music m = getLastMusic();
        //position为music 在播放列表中的位置
        current = m.getPosition();
        Log.i("LIN","creat时的值是："+current);
        mediaplayer = new MediaPlayer();
        try {
            mediaplayer.reset();
            mediaplayer.setDataSource(getPath(current));
            mediaplayer.prepare();
            Log.i("LIN","create时progress是："+m.getProgress());
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LIN","service on startcommand");
        int action = intent.getIntExtra("action",-1);
        musicType = intent.getIntExtra("musicType",-1);
        Log.i("LIN", "MUsictype\n" + musicType);
        if(musicType == ONLINE_MUSIC) {
            musicList = Global.getOnlineMusicList();
            Log.i("LIN","ONLINE music play");
        } else  if (musicType == LOCAL_MUSIC) {
            musicList =  Global.getLocalMusicList();
        }
        Global.setCurrMusicList(musicList);
        Log.i("YI",musicList.size()+"   "+musicList.get(0).getTitle());
        musicPlay(action, intent);
        return super.onStartCommand(intent, flags, startId);
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
                Log.i("LIN", intent.getIntExtra("seekmsec", mediaplayer.getCurrentPosition()) + "   seekmsec");
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
        Log.i("LIN","PATH:\n"+getPath(i));
            Log.i("LIN", "next song is " + mediaplayer.isPlaying());
    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.i("LIN","service onBind");
        return new MyBinder();
    }


    //获取音乐地址
    private String getPath(int i) {
        return musicList.get(i).getPath();
    }


//获取当前sdcard内所有歌曲
    public  List<Music> findAllMp3() {
        List<Music> list = new ArrayList<>();
        list = musicDao.findAll();
        //数据库创建时就已经插入了一条空记录用于保存歌曲信息
        if(list.size()==0){
            File root = new File(Environment.getExternalStorageDirectory().getPath());
            getDirectoryMp3(root, list);
            Log.i("LIN",list.size()+"listsize");
            musicDao.insertData(list);
        }
        return list;
    }

    /*获取上一次退时播放的歌曲*/
    private Music getLastMusic() {
       Music m=  musicDao.findById(1);
        if(m.getProgress()!=0){
            Log.i("LIN", m.getPosition() + "position current");
            return m;}
        else return musicDao.findById(2);
    }


    private static void getDirectoryMp3(File directory, List<Music> list) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            //L.e(file.getAbsolutePath());
            if (file.isDirectory()) {
                getDirectoryMp3(file, list);
            } else {
                if (file.getName().endsWith(".mp3") || file.getName().endsWith(".MP3")) {
                    Log.i("LIN", "mp3file" + file.getName() + file.getPath() + "   " + file.getParent());
                   Music music= MP3Info.getMusicInfo(file,new Music());
                    Log.i("LIN", "PATH:     " + music.getPath());
                    Log.i("LIN", "title" + music.getTitle());
                    list.add(music);
                }
            }
        }
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
        Log.i("LIN", "service销毁了");
        super.onDestroy();
    }
}
