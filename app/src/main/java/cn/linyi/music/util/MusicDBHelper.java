package cn.linyi.music.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

import cn.linyi.music.bean.Music;

/**
 * Created by linyi on 2016/3/22.
 */
public class MusicDBHelper extends SQLiteOpenHelper {
    private  final static String  DB_NAME= "linmusic.db";//数据库名称
    private  final static int VERSION =1;//版本号

    public MusicDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //为了每次构造时不用传入dbName和版本号，自己得新定义一个构造方法
    public  MusicDBHelper(Context context) {

        this(context,context.getFilesDir().getPath()+ File.separator+DB_NAME,null,VERSION);
    }

    //版本变更时
    public MusicDBHelper(Context cxt,int version) {
        this(cxt,DB_NAME,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table music (_id integer primary key autoincrement,title text," +
                "album text,artist text,path text,progress integer,position integer)";
        Log.i("LIN", "table MUSIC..is created");
        db.execSQL(sql);///竟然忘了执行 我也是醉了

        //建立表之前先插入一条空记录 用于记录退出时的播放信息，position为歌曲在整个播放列表的位置
        String sql1 = "insert into  music(position) values(2)";
        db.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
