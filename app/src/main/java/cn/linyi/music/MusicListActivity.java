package cn.linyi.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.List;
import cn.linyi.music.bean.Music;
import cn.linyi.music.util.Global;

/**
 * Created by linyi on 2016/4/11.
 */
public class MusicListActivity extends PopupWindow implements AdapterView.OnItemClickListener,View.OnTouchListener{
    private List<Music> musicList;
    private ListView musicListView;
    private TextView textView;
    private View view;
    private LayoutInflater inflater;
    private MusicListAdapter musicListAdapter;
    private Activity context;
    private Intent service;
    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }
    public  MusicListActivity(){}
    public MusicListActivity(Activity context) {
        this.context = context;
        service = ((Global)context.getApplicationContext()).getPlayService();
        //获取LayoutInflater
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.musiclist,null);//实例化view自定义布局

        musicListView = (ListView) view.findViewById(R.id.musicList);
        textView = (TextView) view.findViewById(R.id.musicList_count);
        //获取应用全局变量 musiclist
        musicList = Global.getCurrMusicList();

        musicListAdapter = new MusicListAdapter(inflater,musicList);
        musicListView.setAdapter(musicListAdapter);
        musicListView.setOnItemClickListener(this);
        textView.setText("播放列表（"+musicList.size()+")");
        //musicListView.setOnItemClickListener(this);
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(Global.SCREEN_HEIGHT*2/3);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        Log.i("LIN","shezhihaole");
        //设置SelectPicPopupWindow弹出窗体动画效果
      //  this.setAnimationStyle(R.style.AnimBottom);
        view.setOnTouchListener(this);
    }

/*
* 假定你的数据集合体为data，如果有新的数据加入或需要把旧数据全部更换，应采用追加的方式，保留data的原引用

1 如data是个ArrayList，应使用add或先clear再addALL

2 否则你用data = 一个新的数据集合体，这时调用notifyDataSetChanged 是无效的

之所以这样做是因为adapter初始化时就绑定了数据集合的地址，所以adapter只关心原地址所指向的数据有没有改变，只有原地址所指向的数据发生变化，调用notifyDataSetChanged 才有效。

第二种做法显然让data 的引用指向了一个新的引用，原地址的数据并没有发生变化
* */
    //更新当前播放列表
    public void updateMusicList(List<Music> list){
   //解决adapter 更新问题方法一 重新new
   /*     musicListAdapter = new MusicListAdapter(inflater, list);
        textView.setText("播放列表（"+list.size()+")");
        //
        Log.i("YI", "数据更新");
        musicListView.setAdapter(musicListAdapter);*/
        //解决adapter更新问题方法二 将adapter中data先clear后addAll 实现更新
        musicListAdapter.notifyDataSetChanged();
        textView.setText("播放列表（"+list.size()+")");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        service.putExtra("current", position);
        Log.i("LIN", "ITEMCLICK" + position);
        service.putExtra("action", PlayService.MUSICLIST_PLAY);
        //service.putExtra("musicType", PlayService.ONLINE_MUSIC);//歌曲类型在点击是要记得修改 OK！！！
        context.startService(service);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = view.findViewById(R.id.musicList_count).getTop();
        int y=(int) event.getY();
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(y<height){
                //销毁弹出框
                dismiss();
                WindowManager.LayoutParams params= context.getWindow().getAttributes();
                params.alpha=1f;
                context.getWindow().setAttributes(params);
            }
        }
        return true;
    }
}

