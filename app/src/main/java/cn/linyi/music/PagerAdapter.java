package cn.linyi.music;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by linyi on 2016/3/30.
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
