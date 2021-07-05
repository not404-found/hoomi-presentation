package com.hoomicorp.hoomi.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInformationDialogFragment extends DialogFragment {

    private Dialog dialog;

    private UserSession userSession = UserSession.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();

        dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_information_dialog, container, false);

        CollectionReference userInfoCollection = FirebaseFirestore.getInstance().collection("user-info");

        //back img setup
        View backImg = view.findViewById(R.id.my_info_fragment_back_iv);
        backImg.setOnClickListener(v -> {
            dialog.dismiss();
        });

        //user img setup
        CircleImageView userImgContainer = view.findViewById(R.id.my_info_fragment_user_img);
        Glide.with(this).load(userSession.getProfileImageLink()).into(userImgContainer);
        userImgContainer.setOnClickListener(v -> {

        });

        //username et setup
        EditText usernameEt = view.findViewById(R.id.my_info_fragment_username_et);
        usernameEt.setText(userSession.getUsername());

        //email et setup
        EditText emailEt = view.findViewById(R.id.my_info_fragment_email_et);
        emailEt.setText(userSession.getEmail());

        //phone num setup
        EditText phoneNumEt = view.findViewById(R.id.my_info_fragment_phone_number_et);
        phoneNumEt.setText(userSession.getPhoneNumber());

        //save btn setup
        View saveBtn = view.findViewById(R.id.payment_card_fragment_add_new_card_btn);
        saveBtn.setOnClickListener(v -> {
            UserInfoDto newUserInfo = new UserInfoDto();
            newUserInfo.setId(userSession.getId());
            newUserInfo.setPhoneNum(phoneNumEt.getText().toString());
            newUserInfo.setUsername(usernameEt.getText().toString());
            newUserInfo.setEmail(emailEt.getText().toString());

            //todo init imageLink
            newUserInfo.setProfileImageLink("");
            if (!Objects.equals(userSession.getUserInfo(), newUserInfo)) {
                userInfoCollection.document(userSession.getId()).set(newUserInfo, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                        })
                        .addOnFailureListener(exception -> {

                        });
            } else {
                //TODO save user info if changed
                dialog.dismiss();
            }
        });

        return view;
    }
}