package com.github.lulewiczg.controller.ui.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.ui.fragment.LoginFragment;
import com.github.lulewiczg.controller.ui.fragment.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that returns fragments for home activity
 */
public class HomeSectionsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.action_login, R.string.action_settings};
    private final Context mContext;

    public HomeSectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        fragments.add(new LoginFragment());
        fragments.add(new SettingsFragment());
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