package com.hoomicorp.hoomi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.dto.CategoryDto;

public class SelectCategoryRecyclerViewFirestoreAdapter extends FirestoreRecyclerAdapter<CategoryDto, SelectCategoryRecyclerViewFirestoreAdapter.RecyclerCategoryViewHolder> {

    private final Context mContext;
    private final RequestOptions requestOptions;
    private final OnItemClickListener<CategoryDto> onItemClickListener;


    public SelectCategoryRecyclerViewFirestoreAdapter(Context mContext, FirestoreRecyclerOptions<CategoryDto> firestoreOptionss, OnItemClickListener<CategoryDto> onItemClickListener) {
        super(firestoreOptionss);
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
        this.requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    @NonNull
    @Override
    public RecyclerCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.select_category_item, parent, false);
        return new RecyclerCategoryViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerCategoryViewHolder holder, int position, @NonNull CategoryDto categoryDto) {
        holder.nameCategory.setText(categoryDto.getDisplayName());
        Glide.with(mContext).load(categoryDto.getImageLink()).apply(requestOptions).into(holder.imgCategory);
    }

    @NonNull
    @Override
    public CategoryDto getItem(int position) {
        return super.getItem(position);
    }



    class RecyclerCategoryViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout categoryContainer;
        private ImageView imgCategory;
        private TextView nameCategory;

        public RecyclerCategoryViewHolder(@NonNull View view) {
            super(view);

            categoryContainer = view.findViewById(R.id.select_category_category_container);

            imgCategory = view.findViewById(R.id.select_category_category_img);

            nameCategory = view.findViewById(R.id.select_category_category_name);

            categoryContainer.setOnClickListener(v -> {

                ViewGroup parent = (ViewGroup) view.getParent();
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = parent.getChildAt(i);
                    childAt.setSelected(false);
                }

                view.setSelected(true);
                int adapterPosition = getAdapterPosition();
                onItemClickListener.onItemClick(getItem(adapterPosition));
//                categoryContainer.setBackgroundResource(R.drawable.custom_frame_layout_yellow_corners);
            });



        }


    }
}
