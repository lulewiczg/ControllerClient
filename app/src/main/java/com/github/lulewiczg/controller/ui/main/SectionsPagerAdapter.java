package com.github.lulewiczg.controller.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.ui.fragment.MouseFragment;
import com.github.lulewiczg.controller.ui.fragment.TextFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that returns fragment depending on selected tab.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.action_mouse, R.string.action_text, R.string.action_settings};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        fragments.add(new MouseFragment());
        fragments.add(new TextFragment());
        fragments.add(new TextFragment());
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }


}