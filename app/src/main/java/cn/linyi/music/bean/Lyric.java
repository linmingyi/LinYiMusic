package cn.linyi.music.bean;

/**
 * Created by linyi on 2016/3/22.
 */
public class Lyric {
    private  int beginTime;
    private int endTime;
    private int timeLine;
    /*歌词部分*/
    private String lrc;
    /* 文件是否存在*/
    private boolean IsLyricExist = false;



    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(int timeLine) {
        this.timeLine = timeLine;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }
}
