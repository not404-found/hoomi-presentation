package com.hoomicorp.hoomi.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoomicorp.hoomi.R;


public class GoLiveMainFragment extends Fragment {
    private NavController controller;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_go_live_main, container, false);

        //init views
        View goLiveRV = view.findViewById(R.id.go_live_main_fragment_stream_games_container);
        View scheduleStreamRV = view.findViewById(R.id.go_live_main_fragment_stream_irl_container);

        goLiveRV.setOnClickListener(v -> {
            NavDirections navDirections = GoLiveMainFragmentDirections.actionGoLiveMainFragmentToSelectStreamCateforyFragement(true);
            controller.navigate(navDirections);
        });

        scheduleStreamRV.setOnClickListener(v -> {
            NavDirections navDirections = GoLiveMainFragmentDirections.actionGoLiveMainFragmentToSelectStreamCateforyFragement(false);
            controller.navigate(navDirections);
        });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
    }
}