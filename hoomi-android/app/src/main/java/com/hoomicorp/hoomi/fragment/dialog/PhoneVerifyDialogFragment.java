package com.hoomicorp.hoomi.fragment.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hoomicorp.hoomi.ApplicationStartActivity;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.RegistrationActivity;
import com.hoomicorp.hoomi.custom_view.ProgressButton;
import com.hoomicorp.hoomi.listener.PhoneVerificationCodeTextChangeListener;
import com.hoomicorp.hoomi.listener.ResultListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneVerifyDialogFragment extends DialogFragment {
    private Dialog dialog;
    private final String code;
    private final ResultListener resultListener;

    private EditText opt1;
    private EditText opt2;
    private EditText opt3;
    private EditText opt4;
    private EditText opt5;
    private EditText opt6;
    private ProgressButton saveCardPB;

    public PhoneVerifyDialogFragment(String code, ResultListener resultListener) {
        this.code = code;
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
        View view = inflater.inflate(R.layout.fragment_phone_verify_dialog, container, false);

        //back btn init & setup
        View backImgView = view.findViewById(R.id.verify_phone_dialog_fragment_back_image);
        backImgView.setOnClickListener(v -> {
            dialog.dismiss();
        });

        //otp edit text init & setup
        opt1 = view.findViewById(R.id.opt1);
        opt2 = view.findViewById(R.id.opt2);
        opt3 = view.findViewById(R.id.opt3);
        opt4 = view.findViewById(R.id.opt4);
        opt5 = view.findViewById(R.id.opt5);
        opt6 = view.findViewById(R.id.opt6);

        opt1.addTextChangedListener(new PhoneVerificationCodeTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (opt1.getText().toString().length() > 0) {
                    opt2.requestFocus();
                }
            }
        });

        opt2.addTextChangedListener(new PhoneVerificationCodeTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (opt2.getText().toString().length() > 0) {
                    opt3.requestFocus();
                }
            }
        });

        opt3.addTextChangedListener(new PhoneVerificationCodeTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (opt3.getText().toString().length() > 0) {
                    opt4.requestFocus();
                }
            }
        });

        opt4.addTextChangedListener(new PhoneVerificationCodeTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (opt4.getText().toString().length() > 0) {
                    opt5.requestFocus();
                }
            }
        });

        opt5.addTextChangedListener(new PhoneVerificationCodeTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (opt5.getText().toString().length() > 0) {
                    opt6.requestFocus();
                }
            }
        });

        opt6.addTextChangedListener(new PhoneVerificationCodeTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (opt6.getText().toString().length() > 0) {
                    verifyPhoneCode();
                }
            }
        });


        // resend new code btn init & setup
        View resendTV = view.findViewById(R.id.verify_phone_dialog_fragment_resend_new_code_tv);
        resendTV.setOnClickListener(v -> {
            // todo resend code
        });

        //verify btn init & setup
        View verifyBtn = view.findViewById(R.id.verify_phone_dialog_fragment_verify_btn);
        saveCardPB = new ProgressButton(getContext(), verifyBtn, getContext().getResources().getText(R.string.verify).toString());
        verifyBtn.setOnClickListener(v -> {
            verifyPhoneCode();
        });
        return view;
    }

    private void verifyPhoneCode() {
        saveCardPB.buttonActivated();
        String opt1text = opt1.getText().toString();
        String opt2text = opt2.getText().toString();
        String opt3text = opt3.getText().toString();
        String opt4text = opt4.getText().toString();
        String opt5text = opt5.getText().toString();
        String opt6text = opt6.getText().toString();

        String fullOpt = opt1text + opt2text + opt3text + opt4text + opt5text + opt6text;
        System.out.println(fullOpt);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, fullOpt);
        String smsCode = credential.getSmsCode();

        if (Objects.equals(fullOpt, smsCode)) {
            saveCardPB.buttonSuccessFinished();
            resultListener.onSuccess();
        } else {
            saveCardPB.buttonFailedFinished(getContext().getResources().getText(R.string.failed).toString());
            resultListener.onFailure();
        }

        System.out.println("SMS: " + smsCode + " USER ENTER: " + fullOpt);

        signIn(credential);
    }

    private void signIn(final PhoneAuthCredential phoneAuthCredential) {
//        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                         Sign in success, update UI with the signed-in user's information
//                        resultListener.onSuccess();
//                    } else {
//                        resultListener.onFailure();
//                    }
//
//                });
    }
}