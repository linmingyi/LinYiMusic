package cn.linyi.music;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import cn.linyi.music.fragment.Fragment_folderMusic;
import cn.linyi.music.fragment.Fragment_LocalMusic;

public class LocalMusicActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private Fragment fragment;
    private  FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        Log.i("NUO","sdada");
        fm = getFragmentManager();
        fragment = new Fragment_LocalMusic();
        Log.i("NUO","localMusicActivity");
        fm.beginTransaction().add(R.id.local_music_content,fragment).commit();
    }

    public void folder(View view) {
        fragment = new Fragment_folderMusic();
        fm.beginTransaction().replace(R.id.local_music_content,fragment).commit();
    }
}
