package cn.linyi.music;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import cn.linyi.music.Dao.MusicDao;
import cn.linyi.music.adpter.PagerAdapter;
import cn.linyi.music.bean.Music;
import cn.linyi.music.fragment.Fragment_charts;
import cn.linyi.music.fragment.Fragment_per_recommend;
import cn.linyi.music.fragment.Fragment_radio;
import cn.linyi.music.fragment.Fragment_songs_list;
import cn.linyi.music.service.PlayService;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.MusicUtil;
import cn.linyi.music.view.MusicBottomView;

public class MainsActivity extends FragmentActivity implements View.OnClickListener{
    private  final String TAG = this.getClass().getSimpleName();//定义TAG用于调试

    private TextView btn_per,btn_songs,btn_radio,btn_chart;
    private ViewPager pager;
    private PagerAdapter adapter;
    private ImageView main_music_play,red_line;/*main_music_list,main_music_next ;*/
    private TextView main_music_title,main_music_artist;
/*    private MusicListWindow musicListWindow;*/
/*    private LinearLayout musicButtom;*/

    private Intent service;
    ServiceConnection sc;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private PlayService.MyBinder mb;
    private ProgressBar progress;
    private boolean isstop = false;//activity界面运行
    private boolean isContinue = false;//继续播放
    private boolean isChanging = false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    private Handler handler = null;

    private int imageWidth;//图片宽度
    private int width;
    private int height;

    private LinearLayout.LayoutParams layoutParams;

    /*    private Fragment_per_recommend fragment_per_recommend;
        private Fragment_songs_list fragment_songs_list;
        private Fragment_radio fragment_radio;
        private Fragment_charts fragment_charts;*/
    private List<Fragment> list_fragments;

    private MusicBottomView musicBottomView;
    private MusicDao musicDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mains);//文件名要对哦
        handler = new Handler();
        musicDao = new MusicDao(MainsActivity.this);
        Log.i("NUO",musicDao.findAll().size()+"localsize");
        if(musicDao.findAll() != null && musicDao.findAll().size() > 0) {
            Global.setLocalMusicList(musicDao.findAll());

            Music m = MusicUtil.getLastMusic(musicDao);
                /*if(m.getPath().substring(0,4).equals("http")) {
                    Global.setCurrMusicList(Global.getOnlineMusicList());
                    Log.i("NUO",Global.getOnlineMusicList().size()+"onlinesize");
                } else {*/
                    Global.setCurrMusicList(musicDao.findAll());//目前想讲本地所有音乐当做初始音乐目录
              //  }
                Global.setCurrentMusicIndex(m.getPosition());
            Global.getCurrMusicList().get(m.getPosition()).setProgress(m.getProgress());
        }
        service = new Intent("myservice");
        service.setPackage("cn.linyi.music");//设置service的包名！！！！
        Global.init(this);
        Global.setPlayService(service);//设置全局变量 以保证全局只有一个IntentService 实例
        initView();
        initContent();
        bindPlayService();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
       // savedInstanceState.get`

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        if( Global.getCurrMusicList().size() > 0){
            Log.i("LIN","size" +MusicUtil.findAllMp3(musicDao).size());
            musicBottomView.setVisable(true);
        } else {
            musicBottomView.setVisable(false);
        }
    }

    private void initView() {
        musicBottomView = new MusicBottomView(this);
        btn_per = (TextView) findViewById(R.id.main_btn_1);
        btn_songs = (TextView) findViewById(R.id.main_btn_2);
        btn_radio = (TextView) findViewById(R.id.main_btn_3);
        btn_chart = (TextView) findViewById(R.id.main_btn_4);
        pager = (ViewPager) findViewById(R.id.vp_content);

        main_music_title =(TextView) findViewById(R.id.main_music_title);
        main_music_artist =(TextView) findViewById(R.id.main_music_artist);
        main_music_play = (ImageView) findViewById(R.id.main_music_play);
      /*  main_music_list = (ImageView) findViewById(R.id.main_music_list);

        main_music_next = (ImageView) findViewById(R.id.main_music_next);*/
      /*  musicButtom = (LinearLayout) findViewById(R.id.main_music_bottom);
        musicButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainsActivity.this,MusicMainActivity.class);
                startActivity(intent);
            }
        });*/
        progress = (ProgressBar) findViewById(R.id.main_music_progress);
        red_line = (ImageView) findViewById(R.id.red_line);

        int screenW = Global.SCREEN_WIDTH;
        width = Global.SCREEN_WIDTH;

        Log.i("LIN",width+"");
        imageWidth = (int) (width/4.0);
        Log.i("LIN",imageWidth+"width+");
        //设置图片位置属性
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.LEFT;
        layoutParams.leftMargin =0;
        layoutParams.width = imageWidth;
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,getResources().getDisplayMetrics());
        if(red_line!=null) {
            //设置图片初始值
            red_line.setLayoutParams(layoutParams);
        }
        btn_per.setOnClickListener(this);
        btn_songs.setOnClickListener(this);
        btn_radio.setOnClickListener(this);
        btn_chart.setOnClickListener(this);

       /* main_music_list.setOnClickListener(this);
        main_music_play.setOnClickListener(this);
        main_music_next.setOnClickListener(this);*/

    }

    private void initContent() {
        list_fragments = new ArrayList<Fragment>();
        list_fragments.add(new Fragment_per_recommend());
        list_fragments.add(new Fragment_songs_list());
        list_fragments.add(new Fragment_radio());
        list_fragments.add(new Fragment_charts());
        adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.setList_fragments(list_fragments);
        pager.setAdapter(adapter);
        Log.i("LIN", adapter.getCount()+"   ");
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                layoutParams.leftMargin =     (int)((position + positionOffset)*imageWidth);//计算红条移动的位置

                red_line.setLayoutParams(layoutParams);
            }
            @Override
            public void onPageSelected(int position) {
                setCurrentPage(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setCurrentPage(0);//默认选中样式
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_1:
                setCurrentPage(0);
                break;
            case R.id.main_btn_2:
                setCurrentPage(1);
                break;
            case R.id.main_btn_3:
                setCurrentPage(2);
                break;
            case R.id.main_btn_4:
                setCurrentPage(3);
                break;
            default:
                break;
        }
    }

    private void setCurrentPage(int item) {
        switch (item) {
            case 0:
                pager.setCurrentItem(0, true);
                btn_per.setTextColor(Color.RED);
                btn_chart.setTextColor(Color.BLACK);
                btn_songs.setTextColor(Color.BLACK);
                btn_radio.setTextColor(Color.BLACK);
                break;
            case 1:
                pager.setCurrentItem(1, true);
                btn_per.setTextColor(Color.BLACK);
                btn_songs.setTextColor(Color.RED);
                btn_chart.setTextColor(Color.BLACK);
                btn_radio.setTextColor(Color.BLACK);
                break;
            case 2:
                pager.setCurrentItem(2, true);
                btn_per.setTextColor(Color.BLACK);
                btn_songs.setTextColor(Color.BLACK);
                btn_radio.setTextColor(Color.RED);
                btn_chart.setTextColor(Color.BLACK);
                break;
            case 3:
                pager.setCurrentItem(3, true);
                btn_per.setTextColor(Color.BLACK);
                btn_songs.setTextColor(Color.BLACK);
                btn_radio.setTextColor(Color.BLACK);
                btn_chart.setTextColor(Color.RED);
                break;
            default:
                break;
        }
    }


    public void bindPlayService(){
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder ser) {
                Log.i("LIN", "service connected");
                mb = (PlayService.MyBinder) ser;
                //----------定时器记录播放进度---------//
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (isChanging) {
                            return;
                        }
                        if (!isstop) {
                            handler.post(runnableUi);
                        }
                    }
                };
                mTimer.schedule(mTimerTask, 0, 100);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("LIN", "service disconnected");
            }
        };
        this.bindService(service, sc, Context.BIND_AUTO_CREATE);//bind只发生一次
    }

    public void login(View view) {
       MainSettingWindow mainSettingWindow = new MainSettingWindow(this);
        mainSettingWindow.showAtLocation(this.findViewById(R.id.main_ll_head),Gravity.LEFT,0,0);
        WindowManager.LayoutParams params= getWindow().getAttributes();
        params.alpha=0.7f;
        getWindow().setAttributes(params);

    }

     public void onDestroy() {
        super.onDestroy();
         //销毁时终止runable
         if(null != mTimer) {
            mTimer.cancel();
         }
         unbindService(sc);
         handler.removeCallbacks(runnableUi);

        }

    //主线程UI变化
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (musicBottomView.isVisable()) {
                progress.setProgress(mb.getProgress());
                main_music_artist.setText(mb.getCurMusic().getArtist());
                main_music_title.setText(mb.getCurMusic().getTitle());
                if (mb.isplaying()) {
                    main_music_play.setImageDrawable(getResources().getDrawable(R.drawable.music_btn_pause));
                } else {
                    main_music_play.setImageDrawable(getResources().getDrawable(R.drawable.list_icn_play));
                }
                Global.updateProgressBarMax( progress,mb.getDuration());
            }

        }
    };



    public void showLyric(View view) {
        Intent intent = new Intent(MainsActivity.this,
                LoginRegisterActivity.class);
        startActivity(intent);
    }

    public void local(View view) {
        Intent intent = new Intent(MainsActivity.this,LocalMusicActivity.class);
        Log.i("NUO","local");
        startActivity(intent);
    }

    public void showScan(View view) {
        Intent intent = new Intent(MainsActivity.this,ScanSdcardActivity.class);
        Log.i("NUO","scan");
        startActivity(intent);
    }
}
