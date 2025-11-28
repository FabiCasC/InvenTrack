package app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConfig {
    private static Firestore firestore;
    // metodo para conectar firebase
    public static boolean iniciarFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream credencial = new FileInputStream("src/main/java/assets/firebase/firebase-credentials.json");
                FirebaseOptions opciones = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(credencial)).build();
                FirebaseApp.initializeApp(opciones);
                firestore = FirestoreClient.getFirestore();
                return true;  //conexion exitosa
            }
            return true; // ya estaba en conexion
        } catch (IOException e) {
            System.out.println("Error al conectar.. Intente de nuevo");
            return false; // error de conexion
        }
    }
    
    //metodo para confirmar conexion en el main
    public static void estado() {
        if (iniciarFirebase()) {
            System.out.println("Firebase conectado eres un crack");
        } else {
            System.out.println("Error al conectar no eres un crack");
        }
    }
    
    
    public static Firestore getFirestore() {
        if (firestore == null) {
            iniciarFirebase();
        }
        return firestore;
    }
    
}
