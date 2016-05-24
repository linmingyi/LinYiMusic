package cn.linyi.music;

import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.linyi.music.Dao.MusicDao;
import cn.linyi.music.bean.Music;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.MP3Info;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ScanSdcardActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_scan_full;
    private Button btn_scan_custom;
    private Button btn_share;
    private TextView tv_path_is_scaning;
    private TextView completPercent;
    private Handler handler;
    private  Timer mTimer;
    private TimerTask mTask;

    private MusicDao musicDao;
    private List<Music> list;
    private int percent;
    private String filePath;
    private boolean flag;//判断是否是sdcard主目录，是为true 否为false 用于扫描进度的计算
    private static final int SCAN_FULL_DISK = 1;
    private static final int SCAN_CUSTOM_DISK = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_sdcard);
        handler = new Handler();
        musicDao = new MusicDao(this);
        list = new ArrayList<Music>();
        btn_scan_full = (Button) findViewById(R.id.btn_scan_full);
        btn_scan_custom = (Button) findViewById(R.id.btn_scan_custom);
        btn_share = (Button) findViewById(R.id.btn_share);
        tv_path_is_scaning = (TextView) findViewById(R.id.path_is_scaning);
        completPercent = (TextView) findViewById(R.id.complete_percent);
        btn_scan_full.setOnClickListener(this);
        btn_scan_custom.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        filePath = "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan_full:
                findAllMp3();
                break;
            case R.id.btn_scan_custom:
                //finMp3Custom();
                break;
            case R.id.btn_share:
                showShare();
                break;
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }








    private void findAllMp3() {
        scanMusic(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), SCAN_FULL_DISK);

    }

    private void finMp3Custom(File directory){
        scanMusic(directory,SCAN_CUSTOM_DISK);
        musicDao.updateListsAfCus(list);
        Global.setLocalMusicList(musicDao.findAll());
    }

    private void scanMusic(final File directory,final int type){
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(uiRunnable);
            }
        };
        mTimer.schedule(mTask,500,200);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list =  getDirectoryMp3(directory,list,true);
                    if(type == SCAN_FULL_DISK) {
                        musicDao.updateListsAfFull(list);

                    } else {
                        musicDao.updateListsAfCus(list);
                    }
                    Global.setLocalMusicList(musicDao.findAll());
                    Thread.sleep(1000);
                    mTimer.cancel();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Runnable uiRunnable = new Runnable(){
        @Override
        public void run() {
            if(filePath.length()>40) {
                filePath = percent+"%..."+filePath.substring(filePath.length()-40);
            }
            tv_path_is_scaning.setText(filePath);
            completPercent.setText("已扫描"+percent+"%");
            if(percent == 100){
                completPercent.setText("扫描已完成,共扫描到"+list.size()+"首歌曲");
            }
            Log.i("NUOYL",filePath);
        }
    };
    private  List<Music> getDirectoryMp3(File directory, List<Music> list,boolean flag) {
        File[] files = directory.listFiles();
        if (files == null) {
            return null;
        }
        for (int i=0;i<files.length;i++) {
            if(flag){
                percent = (i+1) * 100/ files.length;//做运算时应该先乘以100以防计算结果为0
            }
            //L.e(files[i].getAbsolutePath());
            if (files[i].isDirectory()) {
                getDirectoryMp3(files[i], list,false);
            } else {
                filePath = files[i].getAbsolutePath();
                if (files[i].getName().endsWith(".mp3") || files[i].getName().endsWith(".MP3")) {
                    Log.i("LIN", "mp3file" + files[i].getName() + files[i].getPath() + "   " + files[i].getParent());
                    Music music= MP3Info.getMusicInfo(files[i],new Music());
                    Log.i("LIN", "PATH:     " + music.getPath());
                    Log.i("LIN", "title" + music.getTitle());
                    list.add(music);
                }
            }
        }
       return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // mTimer.cancel();
    }
}
