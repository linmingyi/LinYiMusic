package cn.linyi.music;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import cn.linyi.music.fragment.FragmentNoLogin;
import cn.linyi.music.fragment.Fragment_folderMusic;
import cn.linyi.music.fragment.Fragment_LocalMusic;
import cn.linyi.music.view.MusicBottomView;

public class LocalMusicActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private FrameLayout frameLayout;
    private Fragment fragment;
    private  FragmentManager fm;
    private MusicBottomView musicBottomView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        Log.i("NUO","sdada");
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        musicBottomView = new MusicBottomView(this);
        fm = getFragmentManager();
        fragment = new Fragment_LocalMusic();
       // fragment = new FragmentNoLogin();
        fm.beginTransaction().add(R.id.local_music_content,fragment).commit();
    }

    public void folder(View view) {
        fragment = new Fragment_folderMusic();
        fm.beginTransaction().replace(R.id.local_music_content,fragment).commit();
    }
}
