package cn.linyi.music.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.linyi.music.FolderMusicListActivity;
import cn.linyi.music.R;
import cn.linyi.music.adpter.FolderAdapter;
import cn.linyi.music.bean.Music;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.MusicUtil;

/**
 * Created by linyi on 2016/5/11.
 */
public class Fragment_folderMusic extends Fragment implements AdapterView.OnItemClickListener{
    private ListView folderList;
    private FolderAdapter folderAdapter;
    private Map<String,List<Music>> maps;
    private FolderOnClickListener folderOnClickListener;


    public FolderOnClickListener getFolderOnClickListener() {
        return folderOnClickListener;
    }

    public void setFolderOnClickListener(FolderOnClickListener folderOnClickListener) {
        this.folderOnClickListener = folderOnClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_foldermusic,container,false);
        folderList = (ListView) view.findViewById(R.id.folder_musiclist);
        maps = new HashMap<String,List<Music>>();
        MusicUtil.classfied(maps, Global.getLocalMusicList());//将本地音乐按文件夹来划分
        folderAdapter = new FolderAdapter(inflater,maps,getActivity());//这个应该设置为全局变量
        if(folderAdapter == null){  Log.i("NUO","folderadapter is null");}
        else  {Log.i("NUO","folderadapter is not null");}
        folderList.setAdapter(folderAdapter);
        folderList.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(folderAdapter != null){
        Global.setCurrFolderMusicList((List<Music>) folderAdapter.getItem(position));
        } else {
            Log.i("NUO","folderadapter is null++ONITEM");
        }

        Intent intent = new Intent(getActivity(), FolderMusicListActivity.class);
        intent.putExtra("folderName",folderAdapter.getKeyByPosition(position).toString());
        getActivity().startActivity(intent);
    }

    public interface FolderOnClickListener {
        void onTouchlick();
    };
}
