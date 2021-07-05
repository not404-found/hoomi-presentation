package com.hoomicorp.hoomi.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.fragment.LiveStreamsFragment;
import com.hoomicorp.hoomi.fragment.VideosFragment;

public class FollowingFragmentTabAdapter extends FragmentPagerAdapter {
    private final int numOfTabs;
    private final Context context;

    public FollowingFragmentTabAdapter(@NonNull FragmentManager fm, int numOfTabs, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new LiveStreamsFragment();
        } else {
            return new VideosFragment();
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
                return context.getResources().getText(R.string.live_streams);
            case 1:
                return context.getResources().getText(R.string.videos);
        }
        return null;
    }
}
