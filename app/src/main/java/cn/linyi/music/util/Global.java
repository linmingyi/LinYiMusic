package cn.linyi.music.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.linyi.music.bean.Music;
import cn.linyi.music.bean.User;

/**
 * Created by linyi on 2016/4/6.
 * 用于保存应用的全局变量实现全局变量的共享实例   extends Application
 * public  Global getGlobal() {
 Context context = getApplicationContext();
 return (Global) context;
 }
 */
public class Global  {
    private static List<Music> localMusicList;
    private static List<Music> onlineMusicList;
    private static List<Music> currMusicList;
    private static List<Music> currFolderMusicList;

    public static List<Music> getCurrFolderMusicList() {
        return currFolderMusicList;
    }

    public static void setCurrFolderMusicList(List<Music> currFolderMusicList) {
        Global.currFolderMusicList = currFolderMusicList;
    }

    private static Music currMusic;
    private static User user;
    private static Intent playService;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private static int MusicType;
    private static int CurrentMusicIndex;

    public static int getCurrentMusicIndex() {
        return CurrentMusicIndex;
    }

    public static Music getCurrMusic() {return currMusicList.get(CurrentMusicIndex);}

    public static void setCurrentMusicIndex(int currentMusicIndex) {
        CurrentMusicIndex = currentMusicIndex;
    }

    public static int getMusicType() {
        return MusicType;
    }

    public static void setMusicType(int musicType) {
        MusicType = musicType;
    }

    public static User getUser() {
        return user;
    }

    public static void   setUser(User user) {

        Global.user = user;
    }

    public  static void init(Activity a){
        SCREEN_HEIGHT = a.getWindowManager().getDefaultDisplay().getHeight();
        SCREEN_WIDTH = a.getWindowManager().getDefaultDisplay().getWidth();
    }
    public static List<Music>  getCurrMusicList() {
        return currMusicList;
    }

    public static void setCurrMusicList(List<Music> curr) {

        if(currMusicList != null) {
            if(!currMusicList.equals(curr)) {
                currMusicList.clear();
                Log.i("YI","clear了");
                Log.i("YI",curr.size()+"   curr       id:"+curr.hashCode());
                Log.i("YI",currMusicList.size()+"          id:"+currMusicList.hashCode());
            }
        } else {
            Log.i("YI","初始化了");
            currMusicList = new ArrayList<Music>();
        }
        currMusicList.addAll(curr);
        Log.i("YI",currMusicList.size()+"          id:"+currMusicList.hashCode());
    }

    public static List<Music> getLocalMusicList() {
        return localMusicList;
    }

    public  static void setLocalMusicList(List<Music> localList) {
       if(localMusicList != null){
           localMusicList.clear();
       } else {
           localMusicList = new ArrayList<Music>();
       }
        localMusicList.addAll(localList);
    }

    public static List<Music> getOnlineMusicList() {
        return onlineMusicList;
    }

    public  static void setOnlineMusicList(List<Music> onlineList) {
        onlineMusicList = onlineList;
    }

    public static Intent getPlayService() {
        return playService;
    }

    public static void  setPlayService(Intent service) {
        playService = service;
    }


}
