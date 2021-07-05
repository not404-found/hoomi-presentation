package com.hoomicorp.hoomi.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Strings;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.listener.OnPollAddListener;
import com.hoomicorp.hoomi.model.dto.PollDto;
import com.hoomicorp.hoomi.model.dto.PostDto;
import com.hoomicorp.hoomi.mqtt.MqttClientInstance;

import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class PollDialogFragment extends DialogFragment {

    private Dialog dialog;
    private final String topic;
    private final PostDto postDto;
    private final OnPollAddListener onPollAddListener;
    private final MqttClientInstance mqttClient = MqttClientInstance.getInstance();

    public PollDialogFragment(PostDto postDto, OnPollAddListener onPollAddListener) {
        this.postDto = postDto;
        this.topic = postDto.getUserId() + "/live-stream/" + postDto.getPostId() + "/poll";
        this.onPollAddListener = onPollAddListener;
    }

    @Override
    public void onStart() {
        super.onStart();

        dialog = getDialog();
        if (dialog != null) {
            int width = MATCH_PARENT;
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());;
            dialog.getWindow().setLayout(width, height);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_poll_dialog, container, false);

        //views init
        final EditText questionET = view.findViewById(R.id.poll_dialog_fragment_question_et);
        final EditText leftVoteET = view.findViewById(R.id.poll_dialog_fragment_question_left_vote_et);
        final EditText rightVoteET = view.findViewById(R.id.poll_dialog_fragment_question_right_vote_et);
        final TextInputEditText leftVotePriceET = view.findViewById(R.id.poll_dialog_fragment_question_left_vote_price);
        final TextInputEditText rightVotePriceET = view.findViewById(R.id.poll_dialog_fragment_question_right_vote_price);
        final Button addPollBtn = view.findViewById(R.id.poll_dialog_fragment_add_poll_btn);

        //add poll button setup

        addPollBtn.setOnClickListener(v -> {
            final String question = questionET.getText().toString();
            final String leftVote = leftVoteET.getText().toString();
            final String rightVote = rightVoteET.getText().toString();
            final String leftVotePriceString = leftVotePriceET.getText().toString();
            final String rightVotePriceString = rightVotePriceET.getText().toString();
            if (Objects.isNull(question)) {
                return;
            }
            if (Objects.isNull(leftVote)) {
                return;
            }

            if (Objects.isNull(rightVote)) {
                return;
            }

            final int leftVotePrice = Strings.isNullOrEmpty(leftVotePriceString) ? 0 : Integer.parseInt(leftVotePriceString);
            final int rightVotePrice = Strings.isNullOrEmpty(rightVotePriceString) ? 0 : Integer.parseInt(rightVotePriceString);

            final PollDto pollDto = new PollDto(question, leftVote, leftVotePrice, rightVote, rightVotePrice);
            mqttClient.sendMessage(topic, pollDto.toString().getBytes());
            onPollAddListener.addPoll(pollDto);
            dialog.dismiss();
        });

        return view;
    }
}