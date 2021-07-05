package com.hoomicorp.hoomi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;
import com.hoomicorp.hoomi.model.request.HttpRequest;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;
    private FirebaseUser currentUser;
    private CollectionReference userInfoCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        requestQueue = Volley.newRequestQueue(this);

        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuth.signOut();
        currentUser = firebaseAuth.getCurrentUser();
        userInfoCollection = FirebaseFirestore.getInstance().collection("user-info");

        if (Objects.nonNull(currentUser)) {
            currentUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        String idToken = "Bearer " + task.getResult().getToken();

                        System.out.println("idToken: " + idToken);
                        // Send token to your backend via HTTPS

                        verifyUser(idToken);
                    } else {
                        //todo handle error
                        task.getException().printStackTrace();
                        // Handle error -> task.getException();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("[WelcomeActivity]", "Error while login: " + e.getMessage());
                }
            });
        }

        findViewById(R.id.login_btn).setOnClickListener(v -> {
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
        });


        findViewById(R.id.register_btn).setOnClickListener(v -> {
            Intent registerActivity = new Intent(this, RegistrationActivity.class);
            startActivity(registerActivity);
        });

    }

    private void verifyUser(String idToken) {
        final HttpRequest registerReq = new HttpRequest(Request.Method.GET,
                "http://192.168.100.42:8060/hoomi/api/v1/auth/verify",
                token -> {
                    userInfoCollection.document(currentUser.getUid()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    UserInfoDto userInfoDto = documentSnapshot.toObject(UserInfoDto.class);
                                    UserSession instance = UserSession.getInstance();
                                    instance.setUserInfo(userInfoDto);

                                    Intent mainActivity = new Intent(WelcomeActivity.this, ApplicationStartActivity.class);
                                    startActivity(mainActivity);
                                    finish();
                                }
                            })
                            .addOnFailureListener(err -> {
                                moveToLoginActivity();
                            });

                },
                error -> {
                    error.printStackTrace();
                    moveToLoginActivity();
                }, null, idToken);

        requestQueue.add(registerReq);
    }




    private void moveToLoginActivity() {
        Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
        Intent loginActivity = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }
}
