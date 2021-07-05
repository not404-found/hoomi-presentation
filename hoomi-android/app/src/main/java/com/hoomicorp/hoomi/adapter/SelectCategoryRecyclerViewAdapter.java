package com.hoomicorp.hoomi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.model.dto.CategoryDto;
import com.hoomicorp.hoomi.model.dto.SearchResultDto;

import java.util.List;

public class SelectCategoryRecyclerViewAdapter extends RecyclerView.Adapter<SelectCategoryRecyclerViewAdapter.RecyclerCategoryViewHolder> implements Filterable {

    private List<CategoryDto> categoryDtos;
    private final Context mContext;

    public SelectCategoryRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public RecyclerCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.select_category_item, parent, false);
        return new SelectCategoryRecyclerViewAdapter.RecyclerCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerCategoryViewHolder holder, int position) {
        CategoryDto categoryDto = categoryDtos.get(position);

        holder.nameCategory.setText(categoryDto.getDisplayName());
        Glide.with(mContext).load(categoryDto.getImageLink()).into(holder.imgCategory);
    }

    @Override
    public int getItemCount() {
        return categoryDtos.size();
    }

    static class RecyclerCategoryViewHolder extends RecyclerView.ViewHolder {

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

                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//                categoryContainer.setBackgroundResource(R.drawable.custom_frame_layout_yellow_corners);
            });

        }


    }
}
