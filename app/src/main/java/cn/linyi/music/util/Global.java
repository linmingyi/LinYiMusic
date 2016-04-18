package cn.linyi.music.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import cn.linyi.music.bean.Music;

/**
 * Created by linyi on 2016/4/6.
 * 用于保存应用的全局变量实现全局变量的共享实例
 */
public class Global extends Application {
    private static List<Music> localMusicList;
    private static List<Music> onlineMusicList;
    public static List<Music> currMusicList;
    private Intent playService;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public  static void init(Activity a){
        SCREEN_HEIGHT = a.getWindowManager().getDefaultDisplay().getHeight();
        SCREEN_WIDTH = a.getWindowManager().getDefaultDisplay().getWidth();
    }
    public static List<Music>  getCurrMusicList() {
        return currMusicList;
    }

    public static void setCurrMusicList(List<Music> curr) {
        if(currMusicList != null) {
            currMusicList.clear();
            currMusicList.addAll(curr);
        } else currMusicList = curr;
    }

    public static List<Music> getLocalMusicList() {
        return localMusicList;
    }

    public  static void setLocalMusicList(List<Music> localList) {
        localMusicList = localList;
    }

    public static List<Music> getOnlineMusicList() {
        return onlineMusicList;
    }

    public  static void setOnlineMusicList(List<Music> onlineList) {
        onlineMusicList = onlineList;
    }

    public Intent getPlayService() {
        return playService;
    }

    public void setPlayService(Intent playService) {
        this.playService = playService;
    }

   public  Global getGlobal() {
       Context context = getApplicationContext();
       return (Global) context;
   }
}
