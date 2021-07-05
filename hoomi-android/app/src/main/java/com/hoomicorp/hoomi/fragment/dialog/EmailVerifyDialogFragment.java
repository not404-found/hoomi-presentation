package com.hoomicorp.hoomi.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.listener.ResultListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailVerifyDialogFragment extends DialogFragment {
    private Dialog dialog;
    private final ResultListener resultListener;

    public EmailVerifyDialogFragment(ResultListener resultListener) {
        this.resultListener = resultListener;
    }


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
        View view = inflater.inflate(R.layout.fragment_email_verify_dialog, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser.isEmailVerified()) {
            resultListener.onSuccess();
        }
    }
}