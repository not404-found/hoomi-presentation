package com.hoomicorp.hoomi.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.SearchRecyclerViewAdapter;
import com.hoomicorp.hoomi.fragment.SearchCategoryFragmentDirections;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.dto.CategoryDto;
import com.hoomicorp.hoomi.model.dto.SearchResultDto;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchCategoryFragment extends Fragment implements OnItemClickListener<SearchResultDto> {

    private NavController controller;

    private boolean isGoingLive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_search_category, container, false);

        // arguments init
        SearchCategoryFragmentArgs searchCategoryFragmentArgs = SearchCategoryFragmentArgs.fromBundle(getArguments());
        isGoingLive = searchCategoryFragmentArgs.getIsGoingLive();

        //firestore setup
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference categories = firebaseFirestore.collection("categories");

        //categories recycler view init
        RecyclerView searchGamesRV = view.findViewById(R.id.search_category_fragment_categories_rv);
        SearchRecyclerViewAdapter searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getContext(), new ArrayList<>(), this);
        searchGamesRV.setAdapter(searchRecyclerViewAdapter);
        searchGamesRV.setLayoutManager(new LinearLayoutManager(getContext()));

        //search category fragment edit text
        EditText searchCategoryEt = view.findViewById(R.id.search_category_fragment_et);
        searchCategoryEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {

                } else {
                    //get categories
                    categories
                            .whereGreaterThanOrEqualTo("name", s.toString().toLowerCase())
                            .whereLessThanOrEqualTo("name", s.toString().toLowerCase() + "\uf8ff")
                            .limit(3).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    ArrayList<SearchResultDto> events = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        CategoryDto categoryDto = document.toObject(CategoryDto.class);
                                        categoryDto.setId(document.getId());

                                        SearchResultDto searchResultDto = new SearchResultDto();
                                        searchResultDto.setName(categoryDto.getDisplayName());
                                        searchResultDto.setId(categoryDto.getId());
                                        searchResultDto.setImageLink(categoryDto.getImageLink());
                                        searchResultDto.setTags(categoryDto.getTags());
                                        searchResultDto.setStreamer(false);

                                        events.add(searchResultDto);
                                    }

                                    searchRecyclerViewAdapter.setFilteredGames(events);
                                    searchRecyclerViewAdapter.getFilter().filter("");

                                } else {
//                                LOG error
                                }
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //init back button
        View backbtn = view.findViewById(R.id.search_category_fragment_back_iv);
        backbtn.setOnClickListener(v -> {
            NavDirections directions = SearchCategoryFragmentDirections.actionSearchCategoryFragmentToSelectStreamCategoryFragment(isGoingLive);
            controller.navigate(directions);
        });

        return view;
    }


    @Override
    public void onItemClick(SearchResultDto item) {
        CategoryDto categoryDto = new CategoryDto(item.getId(), item.getName(), item.getName(), item.getTags(), "0", item.getImageLink());

        if (isGoingLive) {
            NavDirections direction = SearchCategoryFragmentDirections.actionSearchCategoryFragmentToStreamSetupFragment(categoryDto);
            controller.navigate(direction);
        } else {
            NavDirections direction = SelectStreamCategoryFragmentDirections.actionSelectStreamCategoryFragmentToScheduleStreamFragment(categoryDto);
            controller.navigate(direction);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        final InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
    }
}