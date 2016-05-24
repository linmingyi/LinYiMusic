package cn.linyi.music.adpter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.linyi.music.R;
import cn.linyi.music.bean.Music;
import cn.linyi.music.util.Global;
import cn.linyi.music.util.MusicUtil;

/**
 * Created by linyi on 2016/5/10.
 * 按文件夹分类音乐
 */
public class FolderAdapter extends BaseAdapter{
    private Map<String,List<Music>> maps;//将统一文件夹下的音乐 插入到map中做为 value值。
    private LayoutInflater inflater;
    private Context context;
    private ViewGroup.LayoutParams layoutParams;
    public FolderAdapter(LayoutInflater inflater, Map<String,List<Music>> maps,Context context) {
        this.maps = maps;
        this.inflater = inflater;
        this.context = context;
    }

    @Override
    public int getCount() {
        return maps.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i("NUO","position"+position);
        Log.i("NUO",((List<Music>)getValueByPosition(position)).size()+"nuo folderSize");
        return getValueByPosition(position);
    }

    public Object getValueByPosition(int position) {
        Object[] keySet = maps.keySet().toArray();
        Log.i("NUO",keySet[position]+"kryset");
        return maps.get(keySet[position].toString());
    }
    public Object getKeyByPosition(int position) {
        Object[] keySet = maps.keySet().toArray();
        return keySet[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.view_folder_adapter,null);
        }
        TextView folderName = (TextView) convertView.findViewById(R.id.folder_name);
        TextView folderPath = (TextView) convertView.findViewById(R.id.folder_path);
        ImageView stateView = (ImageView) convertView.findViewById(R.id.btn_folder_fun);
        folderName.setText(getKeyByPosition(position).toString());
        List<Music> list = (ArrayList<Music>)getValueByPosition(position);
        String path = list.get(0).getPath();
        folderPath.setText(list.size() + "首" + path.substring(0,
                path.lastIndexOf(getKeyByPosition(position).toString())));

        LinearLayout linearView = (LinearLayout) convertView;
        Log.i("NUO",linearView.getChildCount()+"childCount"+ folderName.getText().toString()+"1"
                +linearView.getChildAt(0).getId()+"0"+linearView.getChildAt(1).getId());
        Log.i("NUOYI",MusicUtil.getFolderName(Global.getCurrMusic())+"currfolder   "+getKeyByPosition(position).toString()+"currname");
        if(MusicUtil.getFolderName(Global.getCurrMusicList().get(Global.getCurrentMusicIndex())).equals(getKeyByPosition(position).toString())) {
            stateView.setImageResource(R.drawable.small_red);
        } else {
            stateView.setImageResource(R.drawable.list_icn_more);
        }
        return convertView;
    }
}
