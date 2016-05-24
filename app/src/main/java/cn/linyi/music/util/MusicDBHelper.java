package cn.linyi.music.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

import cn.linyi.music.R;
import cn.linyi.music.bean.Music;

/**
 * Created by linyi on 2016/3/22.
 */
public class MusicDBHelper extends SQLiteOpenHelper {
    private Context context;
    private  final static String  DB_NAME= "linmusic.db";//数据库名称
    private  final static int VERSION = 3;//版本号

    public MusicDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //为了每次构造时不用传入dbName和版本号，自己得新定义一个构造方法
    public  MusicDBHelper(Context context) {
        this(context,context.getFilesDir().getPath()+ File.separator+DB_NAME,null,VERSION);
        this.context = context;
    }

    //版本变更时
    public MusicDBHelper(Context cxt,int version) {
        this(cxt,DB_NAME,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
       // Log.i("LIN", "table MUSIC..is created");

        db.execSQL(context.getString(R.string.sql_createTable_music));///竟然忘了执行 我也是醉了
        db.execSQL(context.getString(R.string.sql_createTable_playlist));
        db.execSQL(context.getString(R.string.sql_createTable_play_history));

        //建立表之前先插入一条空记录 用于记录退出时的播放信息，position为歌曲在整个播放列表的位置其id为1
        //String sql1 = "insert into  music(position) values(2)";
        //db.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(null == context){
            Log.i("YIYI","context null");
        } else {
            Log.i("YIYI","context not null");
        }
        Log.i("YIYI",context.getString(R.string.sql_createTable_music));
        db.execSQL(context.getString(R.string.sql_createTable_music));///竟然忘了执行 我也是醉了
        db.execSQL("ALTER TABLE music ADD duration INTEGER");
        db.execSQL(context.getString(R.string.sql_createTable_playlist));
        db.execSQL(context.getString(R.string.sql_createTable_play_history));


       /* db.execSQL( "CREATE TABLE playlist IF NOT EXISTS (_id INTEGER PRIMARY KEY, name VARCHAR,"
                +"art INTEGER, track_count INTEGER, creator_id INTEGER, creator_nickname VARCHAR,"
                +"creator_avatar INTEGER, collected_count INTEGER, comment_count INTEGER, share_count INTEGER,"
                +"play_count INTEGER, desc VARCHAR, tags VARCHAR, update_time INTEGER,track_number_update_time INTEGER,"
                +"track_update_time INTEGER, special_type INTEGER, extra_info VARCHAR");*/
    }
}
