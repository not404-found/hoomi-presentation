package com.hoomicorp.hoomi.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.model.dto.CategoryDto;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.enums.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CategoriesRecyclerViewFirestoreAdapter extends FirestoreRecyclerAdapter<CategoryDto, CategoriesRecyclerViewFirestoreAdapter.RecyclerEventViewHolder>
//        implements Filterable
{
    private final Context mContext;
    //    private final List<EventDto> games;
    private final RequestOptions requestOptions;
    private final OnItemClickListener<CategoryDto> onItemClickListener;
    private final FirestoreRecyclerOptions<CategoryDto> firestoreOptions;

//    private List<EventDto> filteredGames;

    public CategoriesRecyclerViewFirestoreAdapter(Context mContext, FirestoreRecyclerOptions<CategoryDto> firestoreOptions, OnItemClickListener<CategoryDto> onItemClickListener) {
        super(firestoreOptions);
        this.mContext = mContext;
        this.firestoreOptions = firestoreOptions;
//        this.games = games;
//        this.filteredGames = games;
        this.onItemClickListener = onItemClickListener;
        this.requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    @NonNull
    @Override
    public RecyclerEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.game_row_item, parent, false);

        return new RecyclerEventViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerEventViewHolder holder, int position, @NonNull CategoryDto model) {
//        EventDto eventDto = filteredGames.get(position);

        System.out.println(model);

        holder.tvGameName.setText(model.getDisplayName());
        String viewersCount = model.getViewersCount();
        String viewers = Objects.nonNull(viewersCount) && !viewersCount.isEmpty() ? viewersCount : String.valueOf(Math.random() * 999);
        holder.tvGameViewersCount.setText(viewers);

        Glide.with(mContext).load(model.getImageLink()).apply(requestOptions).into(holder.ivGameThumbnail);

        List<Tag> categories = model.getTags();
        List<TextView> textViews = holder.tvCategories;
        int min = Math.min(categories.size(), textViews.size());
        for (int i = 0; i < min; i++) {
            TextView textView = textViews.get(i);
            Tag tags = categories.get(i);
            textView.setText(tags.name());
            textView.setVisibility(View.VISIBLE);

        }

    }

    private void createTextView(int position, int id, RecyclerEventViewHolder holder, final String category) {

        ConstraintLayout cl = holder.cl;
        TextView textView = new TextView(mContext);

        textView.setId(id);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        textView.setText(category);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setLayoutParams(layoutParams);
//        textView.setBackground(mContext.getResources().getDrawable(R.drawable.game_category_background));


        cl.addView(textView, 3 + id);


        ConstraintSet set = new ConstraintSet();
        set.clone(cl);

        if (position == 0) {
            set.connect(textView.getId(), ConstraintSet.START, cl.getId(), ConstraintSet.END, 16);

        } else {
            set.connect(textView.getId(), ConstraintSet.START, position - 1, ConstraintSet.END, 4);

        }

        set.connect(textView.getId(), ConstraintSet.BOTTOM, holder.ivGameThumbnail.getId(), ConstraintSet.BOTTOM, 0);
        set.applyTo(cl);
    }


//    @Override
//    public int getItemCount() {
//        return filteredGames.size();
//    }


//    @Override
//    public Filter getFilter() {
//        return new GamesFilter();
//    }


//    class GamesFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//
//            String key = constraint.toString();
//            if (key.isEmpty()) {
//                filteredGames = games;
//            } else {
//                filteredGames = games.stream()
//                        .filter(dto -> Objects.equals(dto.getName().toLowerCase(), key.toLowerCase()))
//                        .collect(Collectors.toList());
//            }
//            FilterResults filterResults = new FilterResults();
//            filterResults.values = filteredGames;
//            return filterResults;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//
//            filteredGames = (List<EventDto>) results.values;
//            notifyDataSetChanged();
//        }
//    }


    class RecyclerEventViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout cl;
        private TextView tvGameName;
        private TextView tvGameViewersCount;
        final List<TextView> tvCategories = new ArrayList<>();
        private ImageView ivGameThumbnail;

        public RecyclerEventViewHolder(@NonNull View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.game_row_cl);
            tvGameName = itemView.findViewById(R.id.game_title);
            tvGameViewersCount = itemView.findViewById(R.id.game_viewers_count);
            tvCategories.add(itemView.findViewById(R.id.game_category));
            tvCategories.add(itemView.findViewById(R.id.game_category1));
            tvCategories.add(itemView.findViewById(R.id.game_category2));
            tvCategories.add(itemView.findViewById(R.id.game_category3));

            ivGameThumbnail = itemView.findViewById(R.id.game_thumbnail);

            itemView.setOnClickListener(v -> {
                CategoryDto item = getItem(getAdapterPosition());
//                EventDto eventDto = games.get(getAdapterPosition());
                onItemClickListener.onItemClick(item);
            });
        }


    }
}
