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

import cn.linyi.music.adpter.PagerAdapter;
import cn.linyi.music.fragment.Fragment_charts;
import cn.linyi.music.fragment.Fragment_per_recommend;
import cn.linyi.music.fragment.Fragment_radio;
import cn.linyi.music.fragment.Fragment_songs_list;
import cn.linyi.music.service.PlayService;
import cn.linyi.music.util.Global;
import cn.linyi.music.view.MusicBottomView;

public class MainsActivity extends FragmentActivity implements View.OnClickListener{
    private Button btn_per,btn_songs,btn_radio,btn_chart;
    private ViewPager pager;
    private PagerAdapter adapter;
    private ImageView main_music_play,red_line;/*main_music_list,main_music_next ;*/
    private TextView main_music_title,main_music_artist;
/*    private MusicListWindow musicListWindow;*/
    private LinearLayout musicButtom;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mains);//文件名要对哦
        handler = new Handler();
        service = new Intent("myservice");
        service.setPackage("cn.linyi.service_01");
        Global.init(this);
        Global.setPlayService(service);//设置全局变量 以保证全局只有一个IntentService 实例
        initView();
        initContent();
        bindPlayService();

    }

    private void initView() {
        musicBottomView = new MusicBottomView(this);
        btn_per = (Button) findViewById(R.id.main_btn_1);
        btn_songs = (Button) findViewById(R.id.main_btn_2);
        btn_radio = (Button) findViewById(R.id.main_btn_3);
        btn_chart = (Button) findViewById(R.id.main_btn_4);
        pager = (ViewPager) findViewById(R.id.vp_content);

        main_music_title =(TextView) findViewById(R.id.main_music_title);
        main_music_artist =(TextView) findViewById(R.id.main_music_artist);
        main_music_play = (ImageView) findViewById(R.id.main_music_play);
      /*  main_music_list = (ImageView) findViewById(R.id.main_music_list);

        main_music_next = (ImageView) findViewById(R.id.main_music_next);*/
        musicButtom = (LinearLayout) findViewById(R.id.main_music_bottom);
        musicButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainsActivity.this,MusicMainActivity.class);
                startActivity(intent);
            }
        });
        progress = (ProgressBar) findViewById(R.id.main_music_progress);
        red_line = (ImageView) findViewById(R.id.red_line);
        Log.i("YI",red_line+" ");

        //获取屏幕尺寸
    /*    WindowManager wm = this.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);*/
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
           /* case R.id.main_music_list:
                showCurrMusicList();
                break;
            case R.id.main_music_play:
                service.putExtra("action",PlayService.PLAY_PASUE);
              //  service.putExtra("musicType",Global.getMusicType());
                startService(service);
                break;
            case R.id.main_music_next:
                service.putExtra("action",PlayService.NEXT_SONG);
                startService(service);
                break;*/
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
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("LIN", "service connected");
                mb = (PlayService.MyBinder) service;
                progress.setMax(mb.getDuration());//定义进度条最大长度
                //----------定时器记录播放进度---------//
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (isChanging) {
                            return;
                        }
                        if (!isstop) {
                            progress.setProgress(mb.getProgress());
                            handler.post(runnableUi);
                            // if(mb.getProgress()==mb.getDuration()) nextMusic();
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

   /* public void showCurrMusicList() {
        if(musicListWindow == null) {
            musicListWindow = new MusicListWindow(this);
           // musicListWindow.setBackgroundDrawable(new ColorDrawable());
        }
        musicListWindow.updateMusicList(Global.getCurrMusicList());
        musicListWindow.showAtLocation(this.findViewById(R.id.main_music_bottom),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);//vity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //
        WindowManager.LayoutParams params=this.getWindow().getAttributes();
        params.alpha=0.7f;
        this.getWindow().setAttributes(params);
        Log.i("YI", "已经点击了显示了");
    }*/

    public void login(View view) {
        /*LoginActivity loginActivity = new LoginActivity(this,getApplicationContext());
        loginActivity.showAtLocation(this.findViewById(R.id.main_music_bottom),
                Gravity.LEFT|Gravity.CENTER_HORIZONTAL,0,0);
        WindowManager.LayoutParams params=this.getWindow().getAttributes();
        params.alpha=0.7f;
        this.getWindow().setAttributes(params);
        Log.i("YI", "已经点击了显示了");*/

        Intent intent = new Intent(this,LoginRegisterActivity.class);
        startActivity(intent);
    }

     public void onDestroy() {
        super.onDestroy();
         //销毁时终止runable
         mTimer.cancel();
         unbindService(sc);
         handler.removeCallbacks(runnableUi);

        }

    //主线程UI变化
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            main_music_artist.setText(mb.getCurMusic().getArtist());
            main_music_title.setText(mb.getCurMusic().getTitle());
            if (mb.isplaying()) {
                main_music_play.setImageDrawable(getResources().getDrawable(R.drawable.desk_pause));
            } else
                main_music_play.setImageDrawable(getResources().getDrawable(R.drawable.desk_play));
        }
    };


    public void showLyric(View view) {
        Intent intent = new Intent(MainsActivity.this,LyricListActivity.class);
        startActivity(intent);
    }

    public void local(View view) {
        Intent intent = new Intent(MainsActivity.this,LocalMusicActivity.class);
        Log.i("NUO","local");
        startActivity(intent);
    }
}
