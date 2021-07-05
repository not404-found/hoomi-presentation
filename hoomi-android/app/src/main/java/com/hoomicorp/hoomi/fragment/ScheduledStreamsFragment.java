package com.hoomicorp.hoomi.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.StreamsRecyclerViewFirestoreAdapter;
import com.hoomicorp.hoomi.listener.NavigationListener;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.PostDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduledStreamsFragment extends Fragment implements OnItemClickListener<PostDto> {

    private StreamsRecyclerViewFirestoreAdapter scheduledStreamsRVadapter;
    private NavigationListener navigationListener;
    private String userId;

    public ScheduledStreamsFragment(NavigationListener navigationListener, String userId) {
        this.navigationListener = navigationListener;
        this.userId = userId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scheduled_streams, container, false);
        //firestore-setup
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        CollectionReference scheduledStreams = firebaseFirestore.collection("user-info").document(userId).collection("scheduled-streams");

        Query query = scheduledStreams.whereGreaterThanOrEqualTo("scheduledDateTime", now).orderBy("scheduledDateTime", Query.Direction.ASCENDING);

        // main rv setup
        FirestoreRecyclerOptions<PostDto> options = new FirestoreRecyclerOptions.Builder<PostDto>().setQuery(query, PostDto.class).build();
        RecyclerView gamesRecyclerView = view.findViewById(R.id.scheduled_streams_fragment_rv);
        scheduledStreamsRVadapter = new StreamsRecyclerViewFirestoreAdapter(getContext(), options, this);
        gamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gamesRecyclerView.setAdapter(scheduledStreamsRVadapter);

        //
        return view;
    }

    @Override
    public void onItemClick(PostDto item) {
        if (Objects.nonNull(navigationListener)) {
            navigationListener.navigateToGoLiveFragment(item);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        scheduledStreamsRVadapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        scheduledStreamsRVadapter.stopListening();
    }
}