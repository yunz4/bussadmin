package org.example.demo;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import java.io.FileInputStream;
import java.io.IOException;

public class FirestoreService {
    private Firestore firestore;

    public FirestoreService() throws IOException {
        // Path to your service account key file
        String credentialsPath = "C:\\Users\\Administrator\\Desktop\\demo\\src\\main\\resources\\serviceAccountKey.json";

        // Load the service account key
        FileInputStream serviceAccount = new FileInputStream(credentialsPath);

        // Create credentials using the service account key
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        // Configure Firestore with the credentials
        FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
                .setCredentials(credentials)
                .build();

        // Initialize Firestore
        firestore = firestoreOptions.getService();
    }

    public Firestore getFirestore() {
        return firestore;
    }

    public void close() throws Exception {
        if (firestore != null) {
            firestore.close();
        }
    }
}