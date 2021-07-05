package com.hoomicorp.hoomi.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.PaymentCardRecyclerViewAdapter;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.PaymentCardDto;
import com.hoomicorp.hoomi.model.enums.PaymentCardType;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentCardDialogFragment extends DialogFragment implements OnItemClickListener<PaymentCardDto> {

    private Dialog dialog;

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
        View view = inflater.inflate(R.layout.fragment_payment_card, container, false);

        //firebase firestore init
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        //views init
        ImageView backIV = view.findViewById(R.id.payment_card_fragment_back_iv);
        RecyclerView cardsRV = view.findViewById(R.id.payment_card_fragment_payment_rv);
        Button addNewCardBtn = view.findViewById(R.id.payment_card_fragment_add_new_card_btn);


        //user payment cards setup
        String userID = UserSession.getInstance().getId();
        DocumentReference userInfoDocRef = firebaseFirestore.collection("user-info").document(userID);
        CollectionReference userPaymentCardsCollection = userInfoDocRef.collection("payment-card");

        userPaymentCardsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    List<PaymentCardDto> paymentCards = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        PaymentCardDto paymentCardDto = snapshot.toObject(PaymentCardDto.class);
                        paymentCards.add(paymentCardDto);
                    }
                    //rv setup

                    PaymentCardRecyclerViewAdapter paymentCardRecyclerViewAdapter = new PaymentCardRecyclerViewAdapter(getContext(), paymentCards, PaymentCardDialogFragment.this);
                    cardsRV.setAdapter(paymentCardRecyclerViewAdapter);
                    cardsRV.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {
                    //TODO handle ex
                }
            }
        });

        //back iv setup
        backIV.setOnClickListener(v -> {
           dialog.dismiss();
        });


        //add new card btn setup
        addNewCardBtn.setOnClickListener(v -> {
            AddPaymentCardDialogFragment addPaymentCardDialogFragment = new AddPaymentCardDialogFragment();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            addPaymentCardDialogFragment.show(ft, "Add Payment card");
        });


        return view;
    }

    @Override
    public void onItemClick(PaymentCardDto item) {
        System.out.println("CARD CLICK " + item);
        PaymentCardDetailsDialogFragment paymentCardDetailsDialogFragment = new PaymentCardDetailsDialogFragment();
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        paymentCardDetailsDialogFragment.show(ft, "Payment card details");
    }


}