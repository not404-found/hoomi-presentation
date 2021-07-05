package com.hoomicorp.hoomi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.dto.SearchResultDto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchGamesRecyclerViewHolder> implements Filterable {
    private final Context mContext;
    private List<SearchResultDto> games;
    private List<SearchResultDto> filteredGames;
    private final OnItemClickListener<SearchResultDto> searchedItemClickedListener;


    public SearchRecyclerViewAdapter(Context mContext, List<SearchResultDto> games, OnItemClickListener<SearchResultDto> searchedItemClickedListener) {
        this.mContext = mContext;
        this.games = games;
        this.filteredGames = games;
        this.searchedItemClickedListener = searchedItemClickedListener;
    }

    @Override
    public Filter getFilter() {
        return new SearchGameFilter();
    }

    @NonNull
    @Override
    public SearchGamesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.search_game_item, parent, false);

        return new SearchGamesRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchGamesRecyclerViewHolder holder, int position) {
        SearchResultDto eventDto = filteredGames.get(position);
        holder.gameTitle.setText(eventDto.getName());

        Glide.with(mContext).load(eventDto.getImageLink()).into(holder.gameImg);
    }

    public void setFilteredGames(List<SearchResultDto> filteredGames) {
        this.filteredGames = filteredGames;
        this.games = filteredGames;
    }

    @Override
    public int getItemCount() {
        return filteredGames.size();
    }

    class SearchGamesRecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView gameImg;
        private TextView gameTitle;
        private ImageView liveImg;
        private TextView liveText;

        public SearchGamesRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImg = itemView.findViewById(R.id.searched_game_img);
            gameTitle = itemView.findViewById(R.id.searched_game_title);
            liveImg = itemView.findViewById(R.id.live_img);
            liveText = itemView.findViewById(R.id.liveText);

            itemView.setOnClickListener(v -> {
                SearchResultDto eventDto = games.get(getAdapterPosition());
                searchedItemClickedListener.onItemClick(eventDto);
            });
        }
    }

    class SearchGameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String key = constraint.toString();
            if (key.isEmpty()) {
                filteredGames = games;
            } else {
                filteredGames = games.stream()
                        .filter(dto -> Objects.equals(dto.getName().toLowerCase(), key.toLowerCase()))
                        .collect(Collectors.toList());
            }
            Log.i("FILTERED GAMES", key + " " + filteredGames.size());
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredGames;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredGames = (List<SearchResultDto>) results.values;
            notifyDataSetChanged();
        }
    }
}
