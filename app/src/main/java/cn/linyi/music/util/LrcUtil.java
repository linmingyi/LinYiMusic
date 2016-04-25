package cn.linyi.music.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.linyi.music.bean.Lyric;

/**
 * Created by linyi on 2016/4/24.
 */
public class LrcUtil {
   // private List<Lyric> lyricList;//将歌词文件中每一句装到List中去
    public static List<Lyric> getLyrics(String musicFileName)  {
        List<Lyric> lyricList = new ArrayList<Lyric>();
        String lrcFileName = musicFileName.substring(0,musicFileName.lastIndexOf("."))+".lrc";
        Log.i("YI",lrcFileName);
       // lrcFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"1.lrc";
        Log.i("YI",lrcFileName);
        String line = null;
        BufferedReader fRead = null;
        try {
            fRead = new BufferedReader(new FileReader(lrcFileName));
            while((line = fRead.readLine())!=null) {
                if(line.equals("")){
                    continue;
                }
                Lyric lyric = new Lyric();
                String timer = line.substring(1, line.lastIndexOf("]"));
                if(lyricList.size()>=3) {
                    int beginTime = Integer.valueOf(timer.substring(0, timer.indexOf(":"))) * 60
                            + Integer.valueOf(timer.substring(timer.indexOf(":") + 1, timer.lastIndexOf(".")));
                    lyric.setBeginTime(beginTime);
                    lyric.setTimeLine(timer);
                } else {
                    lyric.setBeginTime(0);
                    lyric.setTimeLine("0");
                }
                String lrc = line.substring(line.lastIndexOf("]")+1);
                lyric.setLrc(lrc);
                lyricList.add(lyric);
            }
        } catch (FileNotFoundException e) {
            Lyric lyric = new Lyric();
            lyric.setLrc("未找到歌词"+lrcFileName);
            lyric.setBeginTime(0);
            lyricList.add(lyric);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fRead != null)  {
            fRead.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lyricList;
    }

   /* public List<Lyric> getLyricList() {
        return lyricList;
    }

    public void setLyricList(List<Lyric> lyricList) {
        this.lyricList = lyricList;
    }*/
}
