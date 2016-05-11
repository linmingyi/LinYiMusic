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
    public static List<Lyric> getLyrics(String musicFileName) {
        List<Lyric> lyricList = new ArrayList<Lyric>();
        String lrcFileName = musicFileName.substring(0, musicFileName.lastIndexOf(".")) + ".lrc";
        String line = null;
        BufferedReader fRead = null;
        try {
            fRead = new BufferedReader(new FileReader(lrcFileName));
            while ((line = fRead.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                Lyric lyric = new Lyric();
                String timer = line.substring(1, line.lastIndexOf("]"));
                if (lyricList.size() >= 3) {
                    int beginTime = Integer.parseInt(timer.substring(0, timer.indexOf(":"))) * 60
                            + Integer.parseInt(timer.substring(timer.indexOf(":") + 1, timer.lastIndexOf(".")));
                    lyric.setBeginTime(beginTime);
                    lyric.setTimeLine(timer);
                } else {
                    lyric.setBeginTime(0);
                    lyric.setTimeLine("0");
                }
                String lrc = line.substring(line.lastIndexOf("]") + 1);
                lyric.setLrc(lrc);
                lyricList.add(lyric);
            }
        } catch (FileNotFoundException e) {
            Lyric lyric = new Lyric();
            lyric.setLrc("");
            // lyric.setBeginTime(0);
            int i = 4;
            while (i-- > 0) {
                lyricList.add(lyric);
                Log.i("YI", lyric.getLrc() + i + "lrc");
                Log.i("YI", "第几个：" + i);
            }//认为添加四空行，用于是的未找到歌词居中显示
            Lyric lrc = new Lyric();//如果不重新new的话会造成lyricList 中的lyric对象内容一致。证明lyricList保存的是对象的引用
            lrc.setBeginTime(0);
            lrc.setLrc("未找到歌词" + lrcFileName);
            lyricList.add(lrc);
            for (Lyric l : lyricList) {
                Log.i("YI", lyric.getLrc() + i + "lrc");
                Log.i("YI", "第几个：" + i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fRead != null) {
                fRead.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        rmNull(lyricList);
        return lyricList;
    }
    //取出LyricList中lrc为“” 的值
    public static void rmNull(List<Lyric> lyricList) {
     /*   for (Lyric l: lyricList){
            if(l.getLrc().equals(""))
                lyricList.remove(l);
        }*/
        for (int i = 0; i < lyricList.size(); i++) {
            if (lyricList.get(i).getLrc().equals("")) {
                lyricList.remove(i);
                i--;
            }
        }

    }
}
