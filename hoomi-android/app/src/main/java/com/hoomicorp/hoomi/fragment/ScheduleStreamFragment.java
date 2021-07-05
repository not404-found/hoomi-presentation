package com.hoomicorp.hoomi.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.SearchRecyclerViewAdapter;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.CategoryDto;
import com.hoomicorp.hoomi.model.dto.PostDto;
import com.hoomicorp.hoomi.model.dto.SearchResultDto;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleStreamFragment extends Fragment {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 0;
    private PostDto postDto = new PostDto();

    private Uri selectedImageUri;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference streamsCollection;
    private CollectionReference eventsCollection;
    private StorageReference streamsStorage;
    private ImageView streamPreviewImage;


//    private UserInfoDto userInfo;
    private NavController controller;


    private EditText streamEventET;
    private EditText streamScheduledTimeET;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule_stream, container, false);
        UserSession userSession = UserSession.getInstance();
        //arguments init
        ScheduleStreamFragmentArgs scheduleStreamFragmentArgs = ScheduleStreamFragmentArgs.fromBundle(getArguments());
        CategoryDto categoryDto = scheduleStreamFragmentArgs.getCategoryDto();

        //firebase init

        final String userID = userSession.getId();
        streamsStorage = FirebaseStorage.getInstance().getReference().child("hoomi-stream-images").child(userID);
        firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference userInfoDocument = firebaseFirestore.collection("user-info").document(userID);
        eventsCollection = firebaseFirestore.collection("categories");
        streamsCollection = userInfoDocument.collection("scheduled-streams");




//        // get user info
//        userInfoDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        userInfo = document.toObject(UserInfoDto.class);
//                        Log.d("[Go Live Fragment]", "User info" + document.getData());
//                    } else {
//                        Log.d("[Go Live Fragment]", "No such user info document");
//                    }
//                } else {
//                    Log.d("[Go Live Fragment]", "Get user info failed with ", task.getException());
//                }
//            }
//        });


        //views init
        final ImageView closeView = view.findViewById(R.id.stream_conf_fragment_close_iv);

        final EditText streamNameET = view.findViewById(R.id.stream_conf_fragment_name_et);
        streamPreviewImage = view.findViewById(R.id.stream_conf_fragment_stream_image_preview);
        streamEventET = view.findViewById(R.id.stream_conf_fragment_select_game_et);
        streamScheduledTimeET = view.findViewById(R.id.stream_conf_fragment_schedule_et);
        final Button startStreamBtn = view.findViewById(R.id.stream_conf_fragment_start_stream_btn);

        // stream event et setup
        streamEventET.setText(categoryDto.getDisplayName());
        postDto.setUserId(userID);
        postDto.setCategoryId(categoryDto.getId());
        postDto.setCategoryName(categoryDto.getDisplayName());
        postDto.setTags(categoryDto.getTags().stream().map(Enum::name).collect(Collectors.toList()));
        postDto.setUsername(userSession.getUsername());
        postDto.setUserProfileImageLink(userSession.getProfileImageLink());

//        streamEventET.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0) {
//                    searchGamesRV.setVisibility(View.GONE);
//                    streamScheduledTimeET.setVisibility(View.VISIBLE);
//                } else if (!isEntertainmentSelected){
//                    //get entertainments
//                    eventsCollection
//                            .whereGreaterThanOrEqualTo("name", s.toString().toLowerCase())
//                            .whereLessThanOrEqualTo("name", s.toString().toLowerCase() + "\uf8ff")
//                            .limit(3).get()
//                            .addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    ArrayList<SearchResultDto> events = new ArrayList<>();
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        CategoryDto categoryDto = document.toObject(CategoryDto.class);
//                                        categoryDto.setId(document.getId());
//
//                                        SearchResultDto searchResultDto = new SearchResultDto();
//                                        searchResultDto.setName(categoryDto.getDisplayName());
//                                        searchResultDto.setId(categoryDto.getId());
//                                        searchResultDto.setImageLink(categoryDto.getImageLink());
//                                        searchResultDto.setTags(categoryDto.getTags());
//                                        searchResultDto.setStreamer(false);
//
//                                        events.add(searchResultDto);
//                                    }
//
//                                    searchRecyclerViewAdapter.setFilteredGames(events);
//                                    searchRecyclerViewAdapter.getFilter().filter("");
//
//                                    streamScheduledTimeET.setVisibility(View.GONE);
//                                    searchGamesRV.setVisibility(View.VISIBLE);
//                                } else {
////                                TODO LOG error
//                                }
//                            });
//                }
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                System.out.println("AFTER" + s.toString());
//            }
//        });

        closeView.setOnClickListener(v -> {
            NavDirections directions = ScheduleStreamFragmentDirections.actionScheduleStreamFragmentToSelectStreamCategoryFragment(false);
            controller.navigate(directions);
        });

        streamPreviewImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                selectImage();
            }
        });


        //pick date and time for steam
        streamScheduledTimeET.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);


            final DatePickerDialog.OnDateSetListener onDateSetListener = (dateView, y, m, d) -> {

                //month starts from 0
                final LocalDate date = LocalDate.of(y, m + 1, d);

                final TimePickerDialog.OnTimeSetListener onTimeSetListener = (timerView, h, min) -> {

                    final LocalTime time = LocalTime.of(h, min);
                    final LocalDateTime scheduledDateTime = LocalDateTime.of(date, time);

                    final LocalDateTime localDateTimeNow = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                    final LocalDateTime nowPlus5Minutes = scheduledDateTime.plusMinutes(5);
                    System.out.println("NOW: " + localDateTimeNow.toString() + " 5 MIN AFTER : " + nowPlus5Minutes);
                    if (scheduledDateTime.isBefore(localDateTimeNow)) {
                        Toast.makeText(getContext(), "Date incorrect", Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    else {
//                        if (nowPlus5Minutes.isAfter(localDateTimeNow)) {
//                            startStreamBtn.setText(R.string.schedule_stream);
//
//                        } else {
//                            startStreamBtn.setText(R.string.start_stream);
//                        }
//                    }

                    long scheduledDateTimeLong = scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    postDto.setScheduledDateTime(scheduledDateTimeLong);
                    streamScheduledTimeET.setText(scheduledDateTime.toString());

                };


                final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onTimeSetListener, hours, minutes, true);

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), onDateSetListener, year, month, day);
            datePickerDialog.show();
        });


        //initialize post
        startStreamBtn.setOnClickListener(v -> {
            final String streamName = streamNameET.getText().toString();
            final String streamId = UUID.randomUUID().toString();

            postDto.setPostName(streamName);
            postDto.setPostId(streamId);
            if (Objects.isNull(selectedImageUri)) {
                Toast.makeText(getContext(), "Please select preview image", Toast.LENGTH_LONG).show();
            } else {
                storeStreamInformation();
            }

        });
        return view;
    }


    private void selectImage() {

        Intent selectImageIntent = new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(selectImageIntent, REQUEST_CODE_STORAGE_PERMISSION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && resultCode == RESULT_OK && Objects.nonNull(data)) {

            try {
                selectedImageUri = data.getData();
                streamPreviewImage.setImageURI(selectedImageUri);
            } catch (Exception e) {

            }
        }
    }

    private String getFileExtension(final Uri imageUri) {

        final ContentResolver contentResolver = getContext().getContentResolver();
        final String type = contentResolver.getType(imageUri);
        final MimeTypeMap mime = MimeTypeMap.getSingleton();
        return "." + mime.getExtensionFromMimeType(type);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void storeStreamInformation() {

        storeImage(selectedImageUri);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void storeImage(Uri imageUri) {
        final StorageReference child = streamsStorage.child(postDto.getPostId() + getFileExtension(imageUri));

        //save image
        child.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    child.getDownloadUrl().addOnSuccessListener(uri -> {
                        Toast.makeText(getContext(), "Success: " + uri.toString(), Toast.LENGTH_SHORT).show();
                        postDto.setPostImageLink(uri.toString());
                        //save information about stream in firestore
                        storeStream();

                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Cant upload stream image", Toast.LENGTH_SHORT).show();

                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    System.out.println("PROGRESS: " + progress);
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void storeStream() {
//        postDto.setUsername(userInfo.getUsername());
//        postDto.setUserProfileImageLink(userInfo.getProfileImageLink());
        postDto.setUpdatedDateTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        streamsCollection.document(postDto.getPostId()).set(postDto).addOnSuccessListener(avoid -> {
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();
            ScheduleStreamFragmentDirections.ActionScheduleStreamFragmentToAccountFragment directions =
                    ScheduleStreamFragmentDirections.actionScheduleStreamFragmentToAccountFragment();
            directions.setIsStartFromScheduledTab(true);
            controller.navigate(directions);


        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
    }


//    @Override
//    public void onGameClick(SearchResultDto searchResultDto) {
//
//        isEntertainmentSelected = true;
//
//        final String entertainmentName = searchResultDto.getName();
//        postDto.setCategoryName(entertainmentName);
//        postDto.setCategoryId(searchResultDto.getId());
//        streamEventET.setText(entertainmentName);
//
//        streamScheduledTimeET.setVisibility(View.VISIBLE);
//        searchGamesRV.setVisibility(View.GONE);
//

//    }
}