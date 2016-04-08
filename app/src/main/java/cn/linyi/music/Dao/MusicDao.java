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
    MusicDBHelper helper = null;
    private   SQLiteDatabase db;

    public MusicDao(Context context){
        helper = new MusicDBHelper(context);
        Log.i("LIN",context.getFilesDir().getPath()+"数据库地址1");
        Log.i("LIN",helper.toString());
        this.db= helper.getWritableDatabase();
        Log.i("LIN",db.getPath()+db.getVersion());
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
        List<Music>   musiclist = findAll();
        Log.i("LIN", "findALL title:" + musiclist.size());
    }

    public  void insertData(List<Music> musiclist){
        String sql = "insert into music(title,album,artist,path,progress) values(?,?,?,?,?)";
        db.beginTransaction();
        for(Music music:musiclist) {
            insertData(music);
               /* db.execSQL(sql, new Object[]{music.getTitle(), music.getAlbum(), music.getArtist(), music.getPath(), music.getProgress()});*/
            musiclist = findAll();
            Log.i("LIN", "findALL title:" + musiclist.size());
            Log.i("LIN", "music title" + music.getTitle());


        }
        db.setTransactionSuccessful();
        db.endTransaction();
        }

    public void deleteData(int id){
        String sql ="delete music where _id=?";
        db.execSQL(sql, new Object[]{id});
    }
    public void updateData(Music music){
        String sql ="update music set title=?,album=?,artist=?,path=? where _id=?";
        db.execSQL(sql,new Object[]{music.getTitle(),music.getAlbum(),music.getArtist(),music.getPath(),music.getId()});
    }

    public void updateData(int id,int position,int progress){
        String sql ="update music set position=?,progress=? where _id=?";
        db.execSQL(sql,new Object[]{position,progress,id});
    }

    public  List<Music> findAll(){
        List<Music> musicList = new ArrayList<Music>();
        String sql = "select * from music where _id>?";
        Cursor c= db.rawQuery(sql,new String[]{"1"});
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
