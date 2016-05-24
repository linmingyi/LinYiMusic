package cn.linyi.music;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.List;

import cn.linyi.music.adpter.MusicListAdapter;
import cn.linyi.music.bean.Music;
import cn.linyi.music.service.PlayService;
import cn.linyi.music.util.Global;

/**
 * Created by linyi on 2016/4/11.
 */
    public class MusicListWindow extends PopupWindow implements AdapterView.OnItemClickListener,View.OnTouchListener{
        private List<Music> musicList;
        private ListView musicListView;
        private TextView textView;
        private View view;
        private LayoutInflater inflater;
        private MusicListAdapter musicListAdapter;
        private Context context;
        private Intent service;
        private boolean scrollFlag = false;// 标记是否滑动
        private int lastVisibleItemPosition = 0;// 标记上次滑动位置
        public void setMusicList(List<Music> musicList) {
            this.musicList = musicList;
        }

        public MusicListWindow(Context context) {
                this.context = context;
                service = Global.getPlayService();
                //获取LayoutInflater
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.musiclist,null);//实例化view自定义布局

                musicListView = (ListView) view.findViewById(R.id.musicList);
                textView = (TextView) view.findViewById(R.id.musicList_count);
                //获取应用全局变量 musiclist
                musicList = Global.getCurrMusicList();
                if(Global.getMusicListAdapter() == null) {
                    musicListAdapter = new MusicListAdapter(inflater,musicList);
                    Global.setMusicListAdapter(musicListAdapter);
                } else {
                    musicListAdapter = Global.getMusicListAdapter();
                }

                musicListView.setAdapter(musicListAdapter);

                textView.setText("播放列表（"+musicList.size()+")");
                this.setContentView(view);
                //设置SelectPicPopupWindow弹出窗体的宽
                this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
                //设置SelectPicPopupWindow弹出窗体的高
                this.setHeight(Global.SCREEN_HEIGHT*3/5);
                //设置SelectPicPopupWindow弹出窗体可点击
                this.setFocusable(true);
                Log.i("LIN","shezhihaole");
                //设置SelectPicPopupWindow弹出窗体动画效果
              //  this.setAnimationStyle(R.style.AnimBottom);
                view.setOnTouchListener(this);
                    musicListView.setOnItemClickListener(this);
                musicListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        switch (scrollState) {
                            case  AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
                                scrollFlag = false;
                                // 判断滚动到顶部
                                if (musicListView.getFirstVisiblePosition() == 0) {
                                    musicListView.setSelection(0);
                                }
                                lastVisibleItemPosition = view.getFirstVisiblePosition();
                                break;
                            case  AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                                scrollFlag = true;
                                break;
                            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时
                                scrollFlag = false;
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        Log.i("NUO","FIR:"+firstVisibleItem+"LAST:"+lastVisibleItemPosition);
                      if (scrollFlag) {
                          if(firstVisibleItem > lastVisibleItemPosition) {
                              Log.i("NUO","上滑");
                          } else if (firstVisibleItem <lastVisibleItemPosition) {
                              Log.i("NUO","下滑");
                          }    else {
                              Log.i("NUO","meihua");
                              return;
                          }
                          lastVisibleItemPosition = firstVisibleItem ;
                      }

                    }
                });
    }

/*
* 假定你的数据集合体为data，如果有新的数据加入或需要把旧数据全部更换，应采用追加的方式，保留data的原引用

1 如data是个ArrayList，应使用add或先clear再addALL

2 否则你用data = 一个新的数据集合体，这时调用notifyDataSetChanged 是无效的

之所以这样做是因为adapter初始化时就绑定了数据集合的地址，所以adapter只关心原地址所指向的数据有没有改变，只有原地址所指向的数据发生变化，调用notifyDataSetChanged 才有效。

第二种做法显然让data 的引用指向了一个新的引用，原地址的数据并没有发生变化
* */
    //更新当前播放列表
    public void updateMusicList(){
        //解决adapter 更新问题方法一 重新new
        //解决adapter更新问题方法二 将adapter中data先clear后addAll 实现更新
        Log.i("YI","调用了更新viewAdpter方法");
        musicListAdapter.notifyDataSetChanged();
        textView.setText("播放列表（"+musicList.size()+")");
    }


    public void updateMusicListView(){
        int currIndex = Global.getCurrentMusicIndex();
        int allVisableCount = musicListView.getLastVisiblePosition() - musicListView.getFirstVisiblePosition() + 1;
        Log.i("NUO","allvisable:"+ musicListView.getLastVisiblePosition());
        if(currIndex>=4) {
            musicListView.setSelection(currIndex -3);
        } else {
            musicListView.setSelection(0);
        }
        Log.i("YI",musicListView.getSelectedItemPosition()+"select position");
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Global.setCurrentMusicIndex(position);
        service.putExtra("current", position);
        Log.i("LIN", "ITEMCLICK" + position);
        Log.i("WANG","music:::"+ ((Music)musicListAdapter.getItem(position)).getTitle());
        ((Music) musicListAdapter.getItem(position)).setIsplaying(true);//设置当前歌曲为处于播放状态
        service.putExtra("action", PlayService.MUSICLIST_PLAY);
        //service.putExtra("musicType", PlayService.ONLINE_MUSIC);//歌曲类型在点击是要记得修改 OK！！！
        context.startService(service);
        updateMusicList();
    }
/*
* PopupWindow 如果不设置其背景则需要自己重写onTouch()监听方法以实现点击空白处消失
* 恢复原界面
*
* 如果设置了其背景则只需要设置PopupWindow的 setOnDismissListener()方法即可实现点击空白处消失
* 并回复原界面
*
* */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = view.findViewById(R.id.musicList_count).getTop();
        int y=(int) event.getY();
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(y<height){
                //销毁弹出框
                Log.i("YI","消失");
                WindowManager.LayoutParams params= ((Activity)context).getWindow().getAttributes();
                params.alpha=1f;
                ((Activity)context).getWindow().setAttributes(params);
                Log.i("YI","界面恢复");
                Log.i("YI","消失");
                dismiss();

            }
        }
        return true;
    }
}

