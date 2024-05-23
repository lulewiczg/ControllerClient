package com.github.lulewiczg.controller.ui.component;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.ui.fragment.LoginFragment;
import com.github.lulewiczg.controller.ui.fragment.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that returns fragments for home activity
 */
public class HomeSectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>();

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