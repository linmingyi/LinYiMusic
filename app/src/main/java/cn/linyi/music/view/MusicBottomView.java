package cn.linyi.music.view;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import cn.linyi.music.MusicListWindow;
import cn.linyi.music.R;
import cn.linyi.music.service.PlayService;
import cn.linyi.music.util.Global;

/**
 * Created by linyi on 2016/5/8.
 */
public class MusicBottomView implements View.OnClickListener{
    private MusicBottomListener musicBottomListener;
    private Intent service;
    private Activity activity;
    private ImageView main_music_list,main_music_play,main_music_next ;
    private MusicListWindow musicListWindow;
    public MusicBottomView(Context c){
        activity = ((Activity)c);
        main_music_list = (ImageView) activity.findViewById(R.id.main_music_list);
        main_music_play = (ImageView) activity.findViewById(R.id.main_music_play);
        main_music_next = (ImageView) activity.findViewById(R.id.main_music_next);
        service = Global.getPlayService();
        main_music_list.setOnClickListener(this);
        main_music_next.setOnClickListener(this);
        main_music_play.setOnClickListener(this);
    }




    public void setMusicBottomListener(MusicBottomListener musicBottomListener) {
        this.musicBottomListener = musicBottomListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_music_list:
                showCurrMusicList();
                break;
            case R.id.main_music_play:
                service.putExtra("action", PlayService.PLAY_PASUE);
                //  service.putExtra("musicType",Global.getMusicType());
                activity.startService(service);
                break;
            case R.id.main_music_next:
                service.putExtra("action",PlayService.NEXT_SONG);
                activity.startService(service);
                break;
            default:
                break;
        }
    }

    public void showCurrMusicList() {
        if(musicListWindow == null) {
            musicListWindow = new MusicListWindow(activity);
            // musicListWindow.setBackgroundDrawable(new ColorDrawable());
        }
        Log.i("NUOYI","curr+:mainbootoom"+Global.getCurrMusicList().size());
        musicListWindow.updateMusicList(Global.getCurrMusicList());
        musicListWindow.showAtLocation(activity.findViewById(R.id.main_music_bottom),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);//vity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //
        WindowManager.LayoutParams params= activity.getWindow().getAttributes();
        params.alpha=0.7f;
        activity.getWindow().setAttributes(params);
        Log.i("YI", "已经点击了显示了");
    }









    public interface MusicBottomListener {};
}
