package cn.linyi.music.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.linyi.music.bean.Music;
import cn.linyi.music.util.MusicDBHelper;

/**
 * Created by linyi on 2016/3/22.
 */
public class MusicDao {
    public SQLiteDatabase getDb() {
        return db;
    }

    private MusicDBHelper helper;
    private   SQLiteDatabase db;

    public MusicDao(Context context){
        helper = new MusicDBHelper(context);
//        Log.i("LIN",context.getFilesDir().getPath()+"数据库地址1");
//        Log.i("LIN",helper.toString());
        if( null != helper ) {
            Log.i("YIYI","helper not null");
        } else {
            Log.i("YIYI","helper null");
        }
        this.db= helper.getWritableDatabase();
//        Log.i("LIN",db.getPath()+db.getVersion());
    }

    /**
     * 当Activity中调用此构造方法，传入一个版本号时，系统会在下一次调用数据库时调用Helper中的onUpgrade()方法进行更新
     * @param context
     * @param version
     */
    public MusicDao(Context context,int version) {
        helper = new MusicDBHelper(context,version);
        this.db= helper.getWritableDatabase();
    }


    public void insertData(Music music){
        String sql ="insert into music(title,album,artist,path,progress) values(?,?,?,?,?)";
        db.execSQL(sql,new Object[]{music.getTitle(),music.getAlbum(),music.getArtist(),music.getPath(),music.getProgress()});
       // List<Music>   musiclist = findAll();
       // Log.i("LIN", "findALL title:" + musiclist.size());
       // db.insert()
    }

    public  void insertData(List<Music> musiclist){
        db.beginTransaction();
        for(Music music:musiclist) {
            insertData(music);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        }

    public void deleteData(int id){
        String sql ="delete music where _id=?";
        db.execSQL(sql, new Object[]{id});
    }
    //更新单条music的数据
    public void updateData(Music music){
        db.beginTransaction();
        String sql ="update music set title=?,album=?,artist=?,path=?,duration=? where _id=?";
        db.execSQL(sql,new Object[]{music.getTitle(),music.getAlbum(),music.getArtist(),music.getPath(),music.getDuration(),music.getId()});
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    //扫描本地全部文件后更新music最新信息
    public void updateListsAfFull(List<Music> musicList){
        String sql = "delete from music where _id >?";
        db.execSQL(sql,new Object[]{1});
        Log.i("WANG",musicList.size()+"musicListSize");
        insertData(musicList);
    }

    //扫描自定义文件夹后更新music最新信息
    public void updateListsAfCus(List<Music> musicList){
        String sql = "delete from music where _id >?";
        db.execSQL(sql,new Object[]{1});
        insertData(musicList);
    }


    //用于更新数据库ID为0的记录 用于记录用户上一次退出时的播放信息
    public void updateData(int id,int position,int progress,String path){
        String sql ="update music set position=?,progress=?,path=? where _id=?";
        db.execSQL(sql,new Object[]{position,progress,path,id});
    }


    public  List<Music> findAll(){
        List<Music> musicList = new ArrayList<Music>();
        String sql = "select * from music where _id>? " +
                "order by title";
        Cursor c= db.rawQuery(sql,new String[]{"1"});
        c.moveToFirst();
        Log.i("LIN","CURSOR"+c.getCount());
        musicList = CursorToList(c,musicList);
        return musicList;
    }


    public List<Music> findByTitle(String title){
        List<Music> musicList = new ArrayList<Music>();
        String sql = "select * from music where title=?";
        Cursor c= db.rawQuery(sql,new String[]{title});
        musicList = CursorToList(c,musicList);
        return musicList;
    }

    public Music findById(int id){
        Music m = new Music();
        String sql = "select * from music where _id=?";
        Cursor c= db.rawQuery(sql,new String[]{id+""});
        while(c.moveToNext()){
            m.setId(c.getInt(0));
            m.setAlbum(c.getString(c.getColumnIndex("album")));
            m.setArtist(c.getString(c.getColumnIndex("artist")));
            m.setPath(c.getString(c.getColumnIndex("path")));
            m.setTitle(c.getString(c.getColumnIndex("title")));
            m.setProgress(c.getInt(c.getColumnIndex("progress")));
            m.setPosition(c.getInt(c.getColumnIndex("position")));
        }
        return m;
    }

    public boolean isExist(Music music) {
        String sql = "select * from music where path=?";
        Cursor c = db.rawQuery(sql,new String[]{music.getPath()});
        if (c != null){
            return true;
        } else return false;
    }



    public  List<Music> CursorToList(Cursor c, List<Music> musicList){
        while (c.moveToNext()){
            Music m = new Music();
            m.setId(c.getInt(0));
            m.setAlbum(c.getString(c.getColumnIndex("album")));
            m.setArtist(c.getString(c.getColumnIndex("artist")));
            m.setPath(c.getString(c.getColumnIndex("path")));
            m.setTitle(c.getString(c.getColumnIndex("title")));
            m.setProgress(c.getInt(c.getColumnIndex("progress")));
            musicList.add(m);
        }

        return musicList;
    }

}
