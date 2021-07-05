package com.hoomicorp.hoomi.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.AccountFragmentTabAdapter;
import com.hoomicorp.hoomi.listener.NavigationListener;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.PostDto;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;

import java.util.Objects;

public class AccountFragment extends Fragment implements NavigationListener {

    private UserInfoDto userInfoDto;
    private NavController controller;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        final String ownerUid = UserSession.getInstance().getId();


        //firebase setup
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final CollectionReference userInfoCollection = firestore.collection("user-info");
        final DocumentReference ownerInfoDocument = userInfoCollection.document(ownerUid);


        //init views
        TextView usernameTV = view.findViewById(R.id.account_fragment_username);
        ImageView profileImage = view.findViewById(R.id.account_fragment_user_image);
        ImageView settings = view.findViewById(R.id.account_fragment_settings);
        ImageView twitchIcon = view.findViewById(R.id.account_fragment_twitch_icon);
        ImageView instagramIcon = view.findViewById(R.id.account_fragment_instagram_icon);
        ImageView youtubeIcon = view.findViewById(R.id.account_fragment_youtube_icon);


        settings.setOnClickListener(v -> {
            NavDirections navDirections = AccountFragmentDirections.actionAccountFragmentToAccountDetailsFragment();
            controller.navigate(navDirections);
        });

        profileImage.setOnClickListener(v -> {
            //todo make availability to upload profile image
        });


        ownerInfoDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userInfoDto = document.toObject(UserInfoDto.class);
                    usernameTV.setText(userInfoDto.getUsername());
                    String profileImageLink = userInfoDto.getProfileImageLink();

                    Glide.with(this).load(profileImageLink).into(profileImage);
                } else {
                    System.out.println("Document not exist");

                    //TODO log err
                }
            } else {
                //TODO log err

                System.out.println("Error while getting user info");

            }
        });


        //init tabs
        TabLayout tabLayout = view.findViewById(R.id.account_fragment_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.account_fragment_view_pager);

        if (Objects.nonNull(getArguments())) {
            AccountFragmentArgs accountFragmentArgs = AccountFragmentArgs.fromBundle(getArguments());
            final boolean isStartFromScheduledTab = accountFragmentArgs.getIsStartFromScheduledTab();

            AccountFragmentTabAdapter tabsAdapter = new AccountFragmentTabAdapter(getChildFragmentManager(), tabLayout.getTabCount(), ownerUid, getContext(), this);
            viewPager.setAdapter(tabsAdapter);
            tabLayout.setupWithViewPager(viewPager);


            if (isStartFromScheduledTab) {
                tabLayout.getTabAt(1).select();
            }


        }


        //social links setup

        twitchIcon.setOnClickListener(v -> {
            openSocial("https://www.instagram.com/a.kblv/");
        });

        youtubeIcon.setOnClickListener(v -> {
            openSocial("https://www.instagram.com/a.kblv/");
        });

        instagramIcon.setOnClickListener(v -> {
            openSocial("https://www.instagram.com/a.kblv/");
        });


        return view;
    }

    private void openSocial(final String url) {
        final Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
    }

    public void navigateToGoLiveFragment(PostDto item) {
//        AccountFragmentDirections.actionAccountFragmentToStreamSetupFragment()
//        AccountFragmentDirections.ActionAccountFragmentToGoLiveFragment action =
//                AccountFragmentDirections.actionAccountFragmentToGoLiveFragment();
//        action.setPostDto(item);
//
//        controller.navigate(action);
    }


}