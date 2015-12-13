package com.tb2g.inventory.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.tb2g.inventory.activity.PlaceholderFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cuong on 12/10/2015.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<String> mInventoryLocations;
    private HashMap<Integer, PlaceholderFragment> mFragmentTags;
    private FragmentManager mFragmentManager;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, PlaceholderFragment>();
        mInventoryLocations = new ArrayList<String>();
        mInventoryLocations.add("Global");
    }

    public List<String> getmInventoryLocations() {
        return mInventoryLocations;
    }

    public void setmInventoryLocations(List<String> mInventoryLocations) {
        this.mInventoryLocations = mInventoryLocations;
        notifyDataSetChanged();
    }



    @Override
    public Fragment getItem(int position) {
        PlaceholderFragment f= PlaceholderFragment.newInstance(mInventoryLocations.get(position));
        String tag = f.getTag();
        Log.d("SectionsPagerAdapter", "" + position);
        mFragmentTags.put(position, f);
        return f;
    }

    @Override
    public int getCount() {
        return mInventoryLocations.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String l = mInventoryLocations.get(position);
        Log.d("Location: " , l);
        return l;

    }

    public PlaceholderFragment getFragment(int position) {
        return mFragmentTags.get(position);
    }

}
