package com.hoomicorp.hoomi;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.base.Strings;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.hoomicorp.hoomi.fragment.dialog.EmailVerifyDialogFragment;
import com.hoomicorp.hoomi.fragment.dialog.PhoneVerifyDialogFragment;
import com.hoomicorp.hoomi.listener.ResultListener;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;
import com.hoomicorp.hoomi.model.request.HttpRequest;
import com.hoomicorp.hoomi.model.request.RegistrationRequest;
import com.hoomicorp.hoomi.model.response.AuthResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=_])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    private TextInputEditText emailET;
    private EditText phoneET;
    private TextInputEditText usernameET;
    private TextInputEditText passwordET;
    private TextInputEditText birthdayET;
    private CountryCodePicker countryCodePicker;
    private TextView swapEmailPhone;

    //containers
    private TextInputLayout emailContainer;
    private TextInputLayout usernameContainer;
    private TextInputLayout passwordContainer;
    private TextInputLayout dateOfBirthContainer;


    private boolean usePhone = false;

    private FirebaseAuth firebaseAuth;
    private CollectionReference userInfoCollection;
    private RequestQueue requestQueue;

    private final Gson jsonConverter = new Gson();
    private PhoneVerifyDialogFragment phoneVerifyDialogFragment;
    private EmailVerifyDialogFragment emailVerifyDialogFragment;

    private String token;

    ResultListener resultListener = new ResultListener() {
        @Override
        public void onSuccess() {
            signIn(token);
            phoneVerifyDialogFragment.dismiss();
//            Intent applicationStartActivity = new Intent(RegistrationActivity.this, ApplicationStartActivity.class);
//            startActivity(applicationStartActivity);
//            finish();
        }

        @Override
        public void onFailure() {
            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            Intent loginActivity = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initViews();

        //firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        userInfoCollection = FirebaseFirestore.getInstance().collection("user-info");
        requestQueue = Volley.newRequestQueue(this);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void register() {
        String userCountry = getUserCountry(this);
        System.out.println("Country " + userCountry);

        if (!isUserInputsValid()) {
            return;
        }

        String email = !Strings.isNullOrEmpty(emailET.getText().toString()) ? emailET.getText().toString() : null;
        String phone = !Strings.isNullOrEmpty(phoneET.getText().toString()) ? countryCodePicker.getSelectedCountryCodeWithPlus() + phoneET.getText().toString() : null;
        String password = this.passwordET.getText().toString();
        String username = this.usernameET.getText().toString();
        String dateOfBirth = birthdayET.getText().toString();


        System.out.println("Email: " + emailET.getText());
        System.out.println("Phone: " + phoneET.getText());
        System.out.println("Country: " + countryCodePicker.getSelectedCountryCodeWithPlus() + phone);

        final RegistrationRequest registrationRequest =
                new RegistrationRequest(Objects.nonNull(email) ? email : UUID.randomUUID().toString(),
                        password, Objects.nonNull(phone) ? phone : UUID.randomUUID().toString(), username, dateOfBirth);
        //add validation

        final HttpRequest registerReq = new HttpRequest(Request.Method.POST,
                "http://192.168.100.42:8060/hoomi/api/v1/auth/registration",

                token -> {
                    AuthResponse authResponse = jsonConverter.fromJson(token, AuthResponse.class);
                    this.token = authResponse.getToken();

                    long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    String userId = authResponse.getUserId();
                    UserInfoDto userInfo = new UserInfoDto();
                    userInfo.setId(userId);
                    userInfo.setUsername(username);
                    userInfo.setSearchName(username.toLowerCase());
                    userInfo.setProfileImageLink("https://hoomi-images.s3.eu-central-1.amazonaws.com/events-images/pubg.jpg");
                    userInfo.setUpdatedDateTime(now);
                    userInfo.setEmail(email);
                    userInfo.setPhoneNum(phone);
                    userInfoCollection.document(userId).set(userInfo).addOnSuccessListener(avoid -> {

                        if (Objects.nonNull(phone)) {
                            phoneAuth(phone, this.token);

                        } else if (Objects.nonNull(email)) {

                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    currentUser.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            emailVerifyDialogFragment = new EmailVerifyDialogFragment(resultListener);
                                            FragmentManager fragmentManager = getSupportFragmentManager();
                                            emailVerifyDialogFragment.show(fragmentManager, "Email verify dialog");

                                        } else {
                                            Toast.makeText(RegistrationActivity.this, task.getException().getLocalizedMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //todo show user validation text message
                            return;
                        }


                    }).addOnFailureListener(err -> {
                        Intent loginActivity = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(loginActivity);
                        finish();
                    });


                },
                error -> {
                    Toast.makeText(RegistrationActivity.this, "Authentication failed. Pls try again",
                            Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }, registrationRequest);

        requestQueue.add(registerReq);


//        firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
//            System.out.println("");
//            if (task.isSuccessful()) {
//                final FirebaseUser user = firebaseAuth.getCurrentUser();
//                final String userId = user.getUid();
//                database = FirebaseDatabase.getInstance().getReference("users").child(userId);
//
//                final Map<String, String> values =  new HashMap<>();
//                values.put("username", username);
//                values.put("email", emailOrPhone);
//                values.put("phone", emailOrPhone);
//                values.put("dateOfBirth", dateOfBirth);
//                values.put("password", password);
//
//                database.setValue(values).addOnCompleteListener(t -> {
//                     if (t.isSuccessful()) {
//                         Intent mainActivity = new Intent(this, MainActivity.class);
//                         startActivity(mainActivity);
//                         finish();
//                     }
//                });
//            }
//        });
    }


    private boolean isUserInputsValid() {

        boolean valid = true;

        //validate email

        if (usePhone) {

        } else {
            String emailInput = emailContainer.getEditText().getText().toString().trim();
            if (emailInput.isEmpty()) {
                emailContainer.setError("Field can't be empty");
                valid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                emailContainer.setError("Please enter a valid email address");
                valid = false;
            } else {
                emailContainer.setError(null);
            }
        }


        //validate username
        String username = usernameContainer.getEditText().getText().toString().trim();
        if (username.isEmpty()) {
            usernameContainer.setError("Field can't be empty");
            valid = false;
        }
        //todo check is username exist

        else {
            usernameContainer.setError(null);
        }


        //validate password
        String passwordInput = passwordContainer.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()) {
            passwordContainer.setError("Field can't be empty");
            valid = false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            passwordContainer.setError("Password too weak");
            valid = false;
        } else {
            passwordContainer.setError(null);

        }

        return valid;
    }


    private void phoneAuth(String phone, final String token) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setActivity(RegistrationActivity.this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        System.out.println("Verification complete");
                        signIn(token);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        System.out.println("Verification failed");

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        System.out.println("CODE: " + s);

                        phoneVerifyDialogFragment = new PhoneVerifyDialogFragment(s, resultListener);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        phoneVerifyDialogFragment.show(fragmentManager, "Phone verify dialog");

                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

//    private void signIn(final PhoneAuthCredential phoneAuthCredential) {
//        firebaseAuth.signInWithCredential(phoneAuthCredential)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                        resultListener.onSuccess();
//                    } else {
//                        resultListener.onFailure();
//                    }
//
//                });
//    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) {
            //TODO log err
        }
        return null;
    }

    private void signIn(final String token) {
        firebaseAuth.signInWithCustomToken(token).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Intent applicationStartActivity = new Intent(RegistrationActivity.this, ApplicationStartActivity.class);
                    startActivity(applicationStartActivity);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    Intent loginActivity = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initViews() {
        //init views
        emailET = findViewById(R.id.register_activity_email_edit_text);
        phoneET = findViewById(R.id.register_activity_phone_edit_text);
        usernameET = findViewById(R.id.register_activity_username_edit_text);
        passwordET = findViewById(R.id.register_activity_password_edit_text);
        birthdayET = findViewById(R.id.register_activity_bday_edit_text);
        countryCodePicker = findViewById(R.id.register_activity_phone_country_code);
        countryCodePicker = findViewById(R.id.register_activity_phone_country_code);
        swapEmailPhone = findViewById(R.id.register_activity_use_phone_number_tv);

        emailContainer = findViewById(R.id.register_activity_email_edit_text_container);
        usernameContainer = findViewById(R.id.register_activity_username_edit_text_container);
        passwordContainer = findViewById(R.id.register_activity_password_edit_text_container);
        dateOfBirthContainer = findViewById(R.id.register_activity_bday_edit_text_container);

        final LinearLayout phoneLayout = findViewById(R.id.register_activity_phone_layout);
        final Button registerBtn = findViewById(R.id.register_activity_sign_up_btn);


        swapEmailPhone.setOnClickListener(v -> {
            if (usePhone) {
                emailContainer.setVisibility(View.VISIBLE);
                phoneLayout.setVisibility(View.GONE);
                CharSequence text = this.getResources().getText(R.string.use_phone_number_instead);
                swapEmailPhone.setText(text);
                usePhone = false;
            } else {
                phoneLayout.setVisibility(View.VISIBLE);
                emailContainer.setVisibility(View.GONE);
                CharSequence text = this.getResources().getText(R.string.use_email_instead);
                swapEmailPhone.setText(text);
                usePhone = true;

            }
        });

//        emailET.setOnTouchListener((v, event) -> {
//            final int DRAWABLE_LEFT = 0;
//            final int DRAWABLE_TOP = 1;
//            final int DRAWABLE_RIGHT = 2;
//            final int DRAWABLE_BOTTOM = 3;
//
////            if (event.getAction() == MotionEvent.ACTION_UP) {
////                if (event.getRawX() >= (emailET.getRight() - emailET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
////                    phoneLayout.setVisibility(View.VISIBLE);
////                    emailET.setVisibility(View.GONE);
////                    return true;
////                }
////            }
//            return false;
//        });
//
//        phoneET.setOnTouchListener((v, event) -> {
//            final int DRAWABLE_LEFT = 0;
//            final int DRAWABLE_TOP = 1;
//            final int DRAWABLE_RIGHT = 2;
//            final int DRAWABLE_BOTTOM = 3;
//
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                if (event.getRawX() >= (phoneET.getRight() - phoneET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                    emailET.setVisibility(View.VISIBLE);
//                    phoneLayout.setVisibility(View.GONE);
//                    return true;
//                }
//            }
//            return false;
//        });

        birthdayET.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog.OnDateSetListener onDateSetListener = (view, y, m, d) -> {
                final LocalDate date = LocalDate.of(y, m + 1, d);
                birthdayET.setText(date.toString());
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    onDateSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        findViewById(R.id.registration_activity_close_iv).setOnClickListener(v -> {
            Intent welcomeActivity = new Intent(this, WelcomeActivity.class);
            startActivity(welcomeActivity);
            finish();
        });

        registerBtn.setOnClickListener(view -> register());
    }
}
