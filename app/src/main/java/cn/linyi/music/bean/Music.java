package cn.linyi.music.bean;

/**
 * Created by linyi on 2016/3/22.
 */
public class Music {
    /*
   *播放的音乐类型
   * */
    public static final int LOCAL_MUSIC = 1;
    public static final int ONLINE_MUSIC = 2;

    private int id;
    private int position;
    private int progress;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int duration;



    /*歌曲名称*/
    private String title;
    /*专辑名称*/
    private String album;
    /*歌手*/
    private String artist;
    /*路径*/
    private String path;
    /*文件大小*/
    private long length;

    private boolean isplaying;

    private int musicType;

    public int getMusicType() {
        return musicType;
    }

    public void setMusicType(int musicType) {
        this.musicType = musicType;
    }

    public boolean isplaying() {
        return isplaying;
    }

    public void setIsplaying(boolean isplaying) {
        this.isplaying = isplaying;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }



    public Music(){

    }

    public   Music(int position,String artist,String title){
        this.position = position;
        this.artist = artist;
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
