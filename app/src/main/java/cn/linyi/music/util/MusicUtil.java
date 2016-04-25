package cn.linyi.music.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.linyi.music.Dao.MusicDao;
import cn.linyi.music.bean.Music;

/**
 * Created by linyi on 2016/4/21.
 */
public class MusicUtil {


    /*获取上一次退时播放的歌曲*/
    public static Music getLastMusic(MusicDao musicDao) {
        Music m =  musicDao.findById(1);
        if(m.getProgress()!=0){
            Log.i("LIN", m.getPosition() + "position current");
            return m;}
        else return musicDao.findById(2);
    }

    //获取当前sdcard内所有歌曲
    public static List<Music> findAllMp3(MusicDao musicDao) {
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

}
