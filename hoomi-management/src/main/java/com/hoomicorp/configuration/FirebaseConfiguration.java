package com.hoomicorp.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfiguration {
    @Value("${firebase.key}")
    private String firebaseKey;

    @PostConstruct
    void configureFirebase() throws IOException {

        final FileInputStream serviceAccount =new FileInputStream(firebaseKey);

        final FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://hoomi-firebase.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }

    @Bean
    Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    @Bean
    FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
