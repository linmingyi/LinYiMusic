package cn.linyi.music;

import android.app.Application;
import android.content.Intent;

import java.util.List;

import cn.linyi.music.bean.Music;

/**
 * Created by linyi on 2016/4/6.
 * 用于保存应用的全局变量实现全局变量的共享实例
 */
public class MusicList extends Application {
    private List<Music> localMusicList;
    private List<Music> onlineMusicList;
    private Intent playService;

    /*  MusicList(){
        }*/

    public List<Music> getLocalMusicList() {
        return localMusicList;
    }

    public void setLocalMusicList(List<Music> localMusicList) {
        this.localMusicList = localMusicList;
    }

    public List<Music> getOnlineMusicList() {
        return onlineMusicList;
    }

    public void setOnlineMusicList(List<Music> onlineMusicList) {
        this.onlineMusicList = onlineMusicList;
    }

    public Intent getPlayService() {
        return playService;
    }

    public void setPlayService(Intent playService) {
        this.playService = playService;
    }
}
