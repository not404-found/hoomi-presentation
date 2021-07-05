package com.hoomicorp.hoomi.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.FollowingFragmentTabAdapter;
import com.hoomicorp.hoomi.model.UserSession;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FollowingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        Log.i("[FollowingFragment]", "onCreateView");

        TabLayout tabLayout = view.findViewById(R.id.following_fragment_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.following_fragment_view_pager);


        FollowingFragmentTabAdapter tabsAdapter = new FollowingFragmentTabAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getContext());

        viewPager.setAdapter(tabsAdapter);

        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}