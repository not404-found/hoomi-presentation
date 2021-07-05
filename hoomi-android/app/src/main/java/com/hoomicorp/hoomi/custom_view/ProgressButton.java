package com.hoomicorp.hoomi.custom_view;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hoomicorp.hoomi.R;

import java.util.Objects;

public class ProgressButton {
    private CardView cardView;
    private ConstraintLayout constraintLayout;
    private ProgressBar progressBar;
    private TextView textView;
    private Animation fadeIn;

    public ProgressButton(Context context, View view, String initialText) {
        fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        cardView = view.findViewById(R.id.progress_btn_card_view);
        constraintLayout = view.findViewById(R.id.progress_btn_container);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.progress_btn_text);
        textView.setText(initialText);
    }

    public void buttonActivated() {
        progressBar.setAnimation(fadeIn);
        progressBar.setVisibility(View.VISIBLE);
        textView.setAnimation(fadeIn);
        textView.setText("Please wait...");
    }


    public void buttonSuccessFinished() {
        constraintLayout.setBackgroundColor(cardView.getResources().getColor(R.color.green));
        progressBar.setVisibility(View.GONE);
        textView.setText("Done");
    }

    public void buttonFailedFinished() {
        constraintLayout.setBackgroundColor(cardView.getResources().getColor(R.color.red));
        progressBar.setVisibility(View.GONE);
        textView.setText("Failed");
    }

    public void buttonFailedFinished(final String text) {
        constraintLayout.setBackgroundColor(cardView.getResources().getColor(R.color.red));
        progressBar.setVisibility(View.GONE);
        textView.setText(text);
    }
}
