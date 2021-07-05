package com.hoomicorp.hoomi.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.dto.PaymentCardDto;
import com.hoomicorp.hoomi.model.enums.PaymentCardType;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class PaymentCardRecyclerViewAdapter extends RecyclerView.Adapter<PaymentCardRecyclerViewAdapter.PaymentCardRecyclerViewHolder> {

    private final Context mContext;
    private final List<PaymentCardDto> cardDtos;
    private final OnItemClickListener<PaymentCardDto> onPaymentCardClickedListener;
    private final Queue<Drawable> backgrounds = new LinkedList<>();

    public PaymentCardRecyclerViewAdapter(Context mContext, List<PaymentCardDto> cardDtos, OnItemClickListener<PaymentCardDto> onPaymentCardClickedListener) {
        this.mContext = mContext;
        this.cardDtos = cardDtos;
        this.onPaymentCardClickedListener = onPaymentCardClickedListener;
        backgrounds.add(ContextCompat.getDrawable(mContext, R.drawable.gradient_royal));
        backgrounds.add(ContextCompat.getDrawable(mContext, R.drawable.gradient_dark_skies));
        backgrounds.add(ContextCompat.getDrawable(mContext, R.drawable.gradient_mauve));
        backgrounds.add(ContextCompat.getDrawable(mContext, R.drawable.gradient_metapolic));
//        backgrounds.add(ContextCompat.getDrawable(mContext, R.drawable.gradient_dark_skies));
//        backgrounds.add(ContextCompat.getDrawable(mContext, R.drawable.gradient_solid_vault));
//        backgrounds.add(ContextCompat.getDrawable(mContext, R.drawable.gradient_deep_see_space));



    }

    @NonNull
    @Override
    public PaymentCardRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.payment_card_item, parent, false);

        return new PaymentCardRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentCardRecyclerViewHolder holder, int position) {
        PaymentCardDto paymentCardDto = cardDtos.get(position);
        holder.cardNum.setText(paymentCardDto.getCardNum());

        Drawable bg = backgrounds.poll();
        holder.cardContainer.setBackground(bg);
        backgrounds.offer(bg);

        if (paymentCardDto.isMain()) {
            holder.isMainCard.setChecked(true);
        }

        PaymentCardType cardType = paymentCardDto.getCardType();
        if (Objects.equals(cardType, PaymentCardType.MASTERCARD)) {

            holder.cardType.setImageResource(R.drawable.ic_mastercard_selected);

        } else if (Objects.equals(cardType, PaymentCardType.PAYPAL)) {

            holder.cardType.setImageResource(R.drawable.ic_paypal_selected);

        } else if(Objects.equals(cardType, PaymentCardType.BTC)) {


            holder.cardType.setImageResource(R.drawable.ic_bit_coin_selected);
        } else if (Objects.equals(cardType, PaymentCardType.VISA)){
            int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, mContext.getResources().getDisplayMetrics());
            int dimension16InDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, mContext.getResources().getDisplayMetrics());

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    dimensionInDp,
                    dimensionInDp
            );
            layoutParams.endToEnd = holder.cardContainer.getId();
            layoutParams.bottomToBottom = holder.cardContainer.getId();
            layoutParams.setMargins(0, 0, dimension16InDp, 0);

            holder.cardType.setLayoutParams(layoutParams);
            holder.cardType.setImageResource(R.drawable.ic_visa_selected);

        }

    }

    @Override
    public int getItemCount() {
        return cardDtos.size();
    }

    class PaymentCardRecyclerViewHolder  extends RecyclerView.ViewHolder  {
        private ConstraintLayout cardContainer;
        private ImageView removeCardIcon;
        private TextView cardNum;
        private CheckBox isMainCard;
        private ImageView cardType;

        public PaymentCardRecyclerViewHolder(@NonNull View view) {
            super(view);

            cardContainer = view.findViewById(R.id.payment_card_container);
            removeCardIcon = view.findViewById(R.id.payment_card_remove_icon);
            cardNum = view.findViewById(R.id.payment_card_number);
            cardType = view.findViewById(R.id.payment_card_type);
            isMainCard = view.findViewById(R.id.payment_card_is_main);

            removeCardIcon.setOnClickListener(v -> {

            });

            isMainCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {

                    ViewGroup parent = (ViewGroup) view.getParent();
                    int childCount = parent.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = parent.getChildAt(i);
                        CheckBox isMainIV = childAt.findViewById(R.id.payment_card_is_main);
                        isMainIV.setChecked(false);
                    }

                    cardDtos.forEach(dto -> dto.setMain(false));
                    PaymentCardDto paymentCardDto = cardDtos.get(getAdapterPosition());
                    paymentCardDto.setMain(true);
                    isMainCard.setChecked(isChecked);
                }
            });

            cardContainer.setOnClickListener(v -> {
                PaymentCardDto item = cardDtos.get(getAdapterPosition());
                onPaymentCardClickedListener.onItemClick(item);
            });

        }
    }
}
