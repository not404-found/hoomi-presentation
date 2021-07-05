package com.hoomicorp.hoomi.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.SelectCategoryRecyclerViewFirestoreAdapter;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.dto.CategoryDto;

/**
 * A simple {@link Fragment} subclass
 */
public class SelectStreamCategoryFragment extends Fragment implements OnItemClickListener<CategoryDto> {
    private SelectCategoryRecyclerViewFirestoreAdapter selectCategoryRecyclerViewAdapter;

    private NavController controller;
    private EditText searchCategory;
    private CategoryDto selectedCategory;
    private boolean isGoingLive;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_stream_category, container, false);

        //arguments init
        SearchCategoryFragmentArgs searchCategoryFragmentArgs = SearchCategoryFragmentArgs.fromBundle(getArguments());
        isGoingLive = searchCategoryFragmentArgs.getIsGoingLive();

        //firebase setup
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query categories = firebaseFirestore.collection("categories").limit(25);


        // category rv init
        FirestoreRecyclerOptions<CategoryDto> options = new FirestoreRecyclerOptions.Builder<CategoryDto>().setQuery(categories, CategoryDto.class).build();
        selectCategoryRecyclerViewAdapter = new SelectCategoryRecyclerViewFirestoreAdapter(getContext(), options, this);
        RecyclerView categoriesRV = view.findViewById(R.id.select_stream_category_fragment_categories_rv);
        categoriesRV.setLayoutManager(new GridLayoutManager(getContext(), 4));
        categoriesRV.setAdapter(selectCategoryRecyclerViewAdapter);

        //back button init
        ImageView backBtn = view.findViewById(R.id.select_stream_category_fragment_back_iv);
        backBtn.setOnClickListener(v -> {
            NavDirections direction = SelectStreamCategoryFragmentDirections.actionSelectStreamCategoryFragmentToGoLiveMainFragment();
            controller.navigate(direction);
        });

        //search category init
        searchCategory = view.findViewById(R.id.select_stream_category_fragment_search_event_et);
        searchCategory.setOnClickListener((v) -> {
            NavDirections direction = SelectStreamCategoryFragmentDirections.actionSelectStreamCategoryFragmentToSearchCategoryFragment(isGoingLive);
            controller.navigate(direction);
        });


        //next btn setup
        Button nextBtn = view.findViewById(R.id.select_stream_category_fragment_next);
        nextBtn.setOnClickListener(v -> {
            if (isGoingLive) {
                NavDirections direction = SelectStreamCategoryFragmentDirections.actionSelectStreamCategoryFragmentToStreamSetupFragment(selectedCategory);
                controller.navigate(direction);
            } else {
                NavDirections direction = SelectStreamCategoryFragmentDirections.actionSelectStreamCategoryFragmentToScheduleStreamFragment(selectedCategory);
                controller.navigate(direction);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
    }


    @Override
    public void onStart() {
        super.onStart();
        selectCategoryRecyclerViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        selectCategoryRecyclerViewAdapter.stopListening();
    }

    @Override
    public void onItemClick(CategoryDto item) {
        searchCategory.setText(item.getDisplayName());
        selectedCategory = item;
    }
}