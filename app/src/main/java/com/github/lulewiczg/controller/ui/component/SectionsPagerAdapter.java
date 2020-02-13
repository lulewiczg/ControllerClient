package com.github.lulewiczg.controller.ui.component;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.lulewiczg.controller.ui.fragment.BindsFragment;
import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.ui.fragment.KeyboardFragment;
import com.github.lulewiczg.controller.ui.fragment.MouseFragment;
import com.github.lulewiczg.controller.ui.fragment.SettingsFragment;
import com.github.lulewiczg.controller.ui.fragment.TextFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that returns fragment depending on selected tab.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.action_mouse, R.string.action_keyboard, R.string.action_text, R.string.action_binds, R.string.action_settings};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        fragments.add(new MouseFragment());
        fragments.add(new KeyboardFragment());
        fragments.add(new TextFragment());
        fragments.add(new BindsFragment());
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