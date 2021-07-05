package com.hoomicorp.hoomi.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.fragment.AboutFragment;
import com.hoomicorp.hoomi.fragment.HomeFragment;
import com.hoomicorp.hoomi.fragment.LibraryFragment;
import com.hoomicorp.hoomi.fragment.ScheduledStreamsFragment;
import com.hoomicorp.hoomi.listener.NavigationListener;

public class AccountFragmentTabAdapter extends FragmentPagerAdapter {
    private final int numOfTabs;
    private final Context context;
    private final NavigationListener navigationListener;
    private final String userId;

    public AccountFragmentTabAdapter(@NonNull FragmentManager fm, int numOfTabs, String userId, Context context, NavigationListener navigationListener) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
        this.userId = userId;
        this.context = context;
        this.navigationListener = navigationListener;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new HomeFragment();
        } else if (position == 1){
            return new ScheduledStreamsFragment(navigationListener, userId);
        } else if (position == 2) {
            return new AboutFragment();
        } else if (position == 3) {
            return new LibraryFragment();
        } else {
            throw new RuntimeException("No such fragment");
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return context.getResources().getText(R.string.home);
            case 1:
                return context.getResources().getText(R.string.schedule);
            case 2:
                return context.getResources().getText(R.string.about);
            case 3:
                return context.getResources().getText(R.string.library);
        }
        return null;
    }
}
