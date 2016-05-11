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
 */
public class FolderAdapter extends BaseAdapter{
    private Map<String,List<Music>> maps;
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
        folderName.setText(getKeyByPosition(position).toString());
        List<Music> list = (ArrayList<Music>)getValueByPosition(position);
        String path = list.get(0).getPath();
        folderPath.setText(list.size() + "首" + path.substring(0,
                path.lastIndexOf(getKeyByPosition(position).toString())));

        LinearLayout linearView = (LinearLayout) convertView;
        Log.i("NUO",linearView.getChildCount()+"view"+ folderName.getText().toString()+"1");
        if(MusicUtil.getFolderName(Global.getCurrMusic()).equals(folderName.getText().toString())) {
            ImageView imView = new ImageView(context);
            if(layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
            imView.setId(R.id.folder_isplaying);
            imView.setBackgroundResource(R.drawable.small_red);
            imView.setScaleType(ImageView.ScaleType.CENTER);
            imView.setLayoutParams(layoutParams);
            linearView.removeViewAt(2);
            linearView.addView(imView,2);
            Log.i("NUO",linearView.getChildCount()+"view"+ folderName.getText().toString()+"2");

        } else {
            if(linearView.getChildAt(2).getId() == R.id.folder_isplaying ) {
               // linearView.getChildAt(2).setBackgroundResource();
                linearView.removeViewAt(2);
                ImageView imView = new ImageView(context);
                imView.setBackgroundResource(R.drawable.scan_icn_folder);
                imView.setId(R.id.btn_folder_fun);
                linearView.addView(imView);
                Log.i("NUO",linearView.getChildCount()+"view"+ folderName.getText().toString()+"3");
            }
        }
        return convertView;
    }
}
