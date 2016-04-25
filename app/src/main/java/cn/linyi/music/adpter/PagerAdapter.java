package cn.linyi.music.adpter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @类描述 主界面的fragment容器
 *PagerAdapter
 *@author lin
 * created at 2016/4/18 14:39
 */

public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> list_fragments;

    public void setList_fragments(List<Fragment> list_fragments) {
        this.list_fragments = list_fragments;
    }

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return list_fragments.get(position);
    }

    @Override
    public int getCount() {
        return list_fragments != null ? list_fragments.size() : 0;
    }
}
