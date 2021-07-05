package com.hoomicorp.hoomi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;
import com.hoomicorp.hoomi.model.request.HttpRequest;
import com.hoomicorp.hoomi.model.response.AuthResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import pl.droidsonroids.gif.GifImageView;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameET;
    private EditText passwordET;
    private TextView errMsg;

    private FirebaseAuth firebaseAuth;
    private CollectionReference userInfoCollection;
    private RequestQueue requestQueue;

    private final Gson jsonConverter = new Gson();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestQueue = Volley.newRequestQueue(this);
        firebaseAuth = FirebaseAuth.getInstance();
        userInfoCollection = FirebaseFirestore.getInstance().collection("user-info");

        initViews();
        //firebase auth
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void login() {
        final String username = usernameET.getText().toString();
        final String password = passwordET.getText().toString();

        final String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(StandardCharsets.UTF_8));
        final String authHeader = "Basic " + new String(encodedAuth);

        final HttpRequest loginReq = new HttpRequest(Request.Method.POST,
                "http://192.168.100.42:8060/hoomi/api/v1/auth/login",

                token -> {
                    AuthResponse authResponse = jsonConverter.fromJson(token, AuthResponse.class);
                    long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    String userId = authResponse.getUserId();

                    userInfoCollection.document(userId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            final DocumentSnapshot result = task.getResult();
                            if (result.exists()) {
                                hideErrMsg();
                                final UserInfoDto userInfoDto = result.toObject(UserInfoDto.class);
                                firebaseAuth.signInWithCustomToken(authResponse.getToken()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //User session initialization
                                            UserSession.getInstance().setUserInfo(userInfoDto);

                                            // Sign in success, update UI with the signed-in user's information
                                            final Intent applicationStartActivity = new Intent(LoginActivity.this, ApplicationStartActivity.class);
                                            startActivity(applicationStartActivity);
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                moveToRegistrationActivity();
                            }

                        } else {
                            moveToRegistrationActivity();
                        }
                    });


                },
                error -> {
                    Log.e("[Login Activity:]", error.getLocalizedMessage());
                    showErrMsg();
                }, null, authHeader);
//
//                    userInfoCollection.document(userId).set(userInfo).addOnSuccessListener(avoid -> {
//                        firebaseAuth.signInWithCustomToken(authResponse.getToken()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                System.out.println("OnComplete");
//                                if (task.isSuccessful()) {
////                                UserSession.getInstance().setId(authResponse.getUserId());
//                                    // Sign in success, update UI with the signed-in user's information
//                                    Intent applicationStartActivity = new Intent(LoginActivity.this, ApplicationStartActivity.class);
//                                    startActivity(applicationStartActivity);
//                                    finish();
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }).addOnFailureListener(err -> {
//
//                        //TODO log err
//                        Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                Toast.LENGTH_SHORT).show();
//                    });
//
//
//                },
//                error -> {
//                    error.printStackTrace();
//                }, null, authHeader);

        requestQueue.add(loginReq);

    }

    private void showErrMsg() {
        errMsg.setVisibility(View.VISIBLE);
    }

    private void hideErrMsg() {
        errMsg.setVisibility(View.GONE);
    }

    private void moveToRegistrationActivity() {
        Toast.makeText(LoginActivity.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
        Intent loginActivity = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(loginActivity);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initViews() {
        errMsg = findViewById(R.id.login_activity_auth_failed_msg);
        usernameET = findViewById(R.id.login_activity_username_edit_text);
        passwordET = findViewById(R.id.login_activity_password_edit_text);

        findViewById(R.id.login_activity_back_image).setOnClickListener(v -> {
            Intent welcomeActivity = new Intent(this, WelcomeActivity.class);
            startActivity(welcomeActivity);
        });

        findViewById(R.id.login_activity_login_btn).setOnClickListener(v -> login());
    }
}
