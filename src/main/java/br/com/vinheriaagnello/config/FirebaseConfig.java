package br.com.vinheriaagnello.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.InputStream;

public class FirebaseConfig {
    public static void init() throws Exception {
        InputStream serviceAccount =
            FirebaseConfig.class.getClassLoader().getResourceAsStream("service-account.json");

        GoogleCredentials credentials = serviceAccount != null
            ? GoogleCredentials.fromStream(serviceAccount)
            : GoogleCredentials.getApplicationDefault();

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build();

        FirebaseApp.initializeApp(options);
    }
}
