package com.hoomicorp.hoomi.fragment.dialog;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.custom_view.ProgressButton;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.PaymentCardDto;
import com.hoomicorp.hoomi.model.enums.PaymentCardType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass
 */
public class AddPaymentCardDialogFragment extends DialogFragment {

    private Dialog dialog;
    private DocumentReference userInfoDocRef;
    private boolean isCardSaved = false;

    TextInputEditText cardNumberET;
    TextInputEditText cardHolderNameET;
    AppCompatSpinner cardExpMonth;
    AppCompatSpinner cardExpYear;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_payment_card, container, false);

        //firebase firestore init
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        //user info init
        String userID = UserSession.getInstance().getId();
        userInfoDocRef = firebaseFirestore.collection("user-info").document(userID);

        //views init
        View backBtn = view.findViewById(R.id.add_payment_card_fragment_back_iv);
        cardNumberET = view.findViewById(R.id.add_payment_card_fragment_card_number_et);
        cardHolderNameET = view.findViewById(R.id.add_payment_card_fragment_card_holder_name_et);
        cardExpMonth = view.findViewById(R.id.add_payment_card_fragment_card_exp_month);
        cardExpYear = view.findViewById(R.id.add_payment_card_fragment_card_exp_year);
        View saveCardBtn = view.findViewById(R.id.add_payment_card_fragment_save_card_btn);
        ProgressButton saveCardPB = new ProgressButton(getContext(), saveCardBtn, "Save card");

        // card exp year setup
        final int nowYear = LocalDate.now().getYear();
        final int maxAvailableYear = nowYear + 10;
        final List<String> years = new ArrayList<>();
        for (int i = nowYear; i < maxAvailableYear; i++) {
            years.add(String.valueOf(i));
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardExpYear.setAdapter(adapter);

        //back btn setup
        backBtn.setOnClickListener(v -> dialog.dismiss());

        //save card btn setup
        saveCardBtn.setOnClickListener(v -> {


            if (isCardSaved) {
                dialog.dismiss();
            }


            if (!isValidCardDetails()) {
                return;
            }

            saveCardPB.buttonActivated();

            String cardNum = cardNumberET.getText().toString();
            String cardHolderName = cardHolderNameET.getText().toString();
            String expMonth = cardExpMonth.getItemAtPosition(this.cardExpMonth.getSelectedItemPosition()).toString();
            String expYear = cardExpYear.getItemAtPosition(this.cardExpYear.getSelectedItemPosition()).toString();
            String[] firstNameLastName = cardHolderName.split("\\s");
            String firstName = firstNameLastName[0];
            String lastName = firstNameLastName[1];

            LocalDate expDate = LocalDate.of(Integer.parseInt(expYear), Integer.parseInt(expMonth), 1);
            PaymentCardDto paymentCardDto = new PaymentCardDto(cardNum, PaymentCardType.VISA, false, firstName, lastName, expDate.toString());
            String cardID = UUID.randomUUID().toString();
            userInfoDocRef.collection("payment-card").document(cardID).set(paymentCardDto).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        saveCardPB.buttonSuccessFinished();
                        isCardSaved = true;

                    } else {
                        saveCardPB.buttonFailedFinished("Retry");
                    }
                }
            });

        });


        return view;
    }

    public boolean isValidCardDetails() {
        return true;
    }
}