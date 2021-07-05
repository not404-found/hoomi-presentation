package com.hoomicorp.hoomi.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.fragment.dialog.MyInformationDialogFragment;
import com.hoomicorp.hoomi.fragment.dialog.PaymentCardDialogFragment;
import com.hoomicorp.hoomi.model.UserSession;

/**
 * A simple {@link Fragment} subclass
 */
public class AccountSettingsFragment extends Fragment {
    private NavController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);

        //views init
        ImageView userImage = view.findViewById(R.id.account_details_fragment_user_image);
        TextView username = view.findViewById(R.id.account_details_fragment_username);
        TextView userStatus = view.findViewById(R.id.account_details_fragment_user_status);
        LinearLayout myInformation = view.findViewById(R.id.account_details_fragment_my_info_container);
        LinearLayout streamSettings = view.findViewById(R.id.account_details_fragment_stream_manager_container);
        LinearLayout payments = view.findViewById(R.id.account_details_fragment_payments_container);
        LinearLayout reports = view.findViewById(R.id.account_details_fragment_stats_container);
        LinearLayout help = view.findViewById(R.id.account_details_fragment_help_container);


        //user info setup
        UserSession userSession = UserSession.getInstance();
        Glide.with(getContext()).load(userSession.getProfileImageLink()).into(userImage);
        username.setText(userSession.getUsername());

        //payments setup
        payments.setOnClickListener(v -> {
            PaymentCardDialogFragment paymentCardDialogFragment = new PaymentCardDialogFragment();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            paymentCardDialogFragment.show(ft, "Payment cards");
        });

        //my information setup
        myInformation.setOnClickListener(v -> {
            MyInformationDialogFragment myInformationDialogFragment = new MyInformationDialogFragment();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            myInformationDialogFragment.show(ft, "Payment cards");
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
    }
}