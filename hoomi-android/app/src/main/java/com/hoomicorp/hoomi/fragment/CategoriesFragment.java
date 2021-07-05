package com.hoomicorp.hoomi.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.CategoriesRecyclerViewFirestoreAdapter;
import com.hoomicorp.hoomi.adapter.SearchRecyclerViewAdapter;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.dto.CategoryDto;
import com.hoomicorp.hoomi.model.dto.SearchResultDto;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment implements OnItemClickListener<SearchResultDto>{

    private CategoriesRecyclerViewFirestoreAdapter mostPopularGamesAdapter;
    private NavController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_categories, container, false);

        //firestore setup
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference eventsFromFirestore = firebaseFirestore.collection("categories");
        CollectionReference userInfoFromFirestore = firebaseFirestore.collection("user-info");

        // init back image
        TextView cancelSearch = view.findViewById(R.id.events_fragment_cancel_search_tv);

        // main rv setup
        FirestoreRecyclerOptions<CategoryDto> options = new FirestoreRecyclerOptions.Builder<CategoryDto>().setQuery(eventsFromFirestore, CategoryDto.class).build();
        RecyclerView gamesRecyclerView = view.findViewById(R.id.events_fragment_events_rv);
        mostPopularGamesAdapter = new CategoriesRecyclerViewFirestoreAdapter(getContext(), options, (eventDto) -> {
            NavDirections navDirections = CategoriesFragmentDirections.actionEventsFragmentToEventDetailFragment(eventDto);
            controller.navigate(navDirections);
        });
        gamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gamesRecyclerView.setAdapter(mostPopularGamesAdapter);

        // search rv setup
        RecyclerView searchGamesRV = view.findViewById(R.id.events_fragment_search_events_rv);
        SearchRecyclerViewAdapter searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getContext(), new ArrayList<>(), this);
        searchGamesRV.setAdapter(searchRecyclerViewAdapter);
        searchGamesRV.setLayoutManager(new LinearLayoutManager(getContext()));

        // init search editable textView
        EditText searchEventET = view.findViewById(R.id.events_fragment_search_event_et);
        searchEventET.setOnTouchListener((v, event) -> {
            cancelSearch.setVisibility(View.VISIBLE);
            return false;
        });

        searchEventET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    searchGamesRV.setVisibility(View.GONE);
                    gamesRecyclerView.setVisibility(View.VISIBLE);
                } else {

                    //get entertainments
                    eventsFromFirestore
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

                                    //get user info
                                    userInfoFromFirestore
                                            .whereGreaterThanOrEqualTo("searchName", s.toString().toLowerCase())
                                            .whereLessThanOrEqualTo("searchName", s.toString().toLowerCase() + "\uf8ff")
                                            .limit(3).get()
                                            .addOnCompleteListener(userInfoTask -> {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : userInfoTask.getResult()) {
                                                        UserInfoDto userInfoDto = document.toObject(UserInfoDto.class);

                                                        SearchResultDto searchResultDto = new SearchResultDto();
                                                        searchResultDto.setName(userInfoDto.getUsername());
                                                        searchResultDto.setId(userInfoDto.getId());
                                                        searchResultDto.setImageLink(userInfoDto.getProfileImageLink());
                                                        searchResultDto.setStreamer(true);

                                                        events.add(searchResultDto);
                                                    }

                                                    searchRecyclerViewAdapter.setFilteredGames(events);
                                                    searchRecyclerViewAdapter.getFilter().filter("");

                                                    searchGamesRV.setVisibility(View.VISIBLE);
                                                    gamesRecyclerView.setVisibility(View.GONE);
                                                }

                                            });

                                } else {
//                                LOG error
                                }
                            });
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("AFTER" + s.toString());


            }
        });


        cancelSearch.setOnClickListener(v -> {
            searchGamesRV.setVisibility(View.GONE);
            gamesRecyclerView.setVisibility(View.VISIBLE);
            cancelSearch.setVisibility(View.GONE);

            searchEventET.setText("");
            searchRecyclerViewAdapter.setFilteredGames(new ArrayList<>());
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
        mostPopularGamesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mostPopularGamesAdapter.stopListening();
    }

    @Override
    public void onItemClick(SearchResultDto searchResultDto) {
        //send game information to details activity
        //TODO add subscribers or viewers count

        NavDirections navDirections;
        if (searchResultDto.isStreamer()) {
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setId(searchResultDto.getId());
            userInfoDto.setProfileImageLink(searchResultDto.getImageLink());
            userInfoDto.setUsername(searchResultDto.getName());


            navDirections = CategoriesFragmentDirections.actionEventsFragmentToOtherAccountFragment(userInfoDto);

        } else {

            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(searchResultDto.getId());
            categoryDto.setDisplayName(searchResultDto.getName());
            categoryDto.setImageLink(searchResultDto.getImageLink());
            categoryDto.setTags(searchResultDto.getTags());
            navDirections = CategoriesFragmentDirections.actionEventsFragmentToEventDetailFragment(categoryDto);
        }
        controller.navigate(navDirections);
    }
}