package app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

public class FirebaseConfig {
    private static Firestore firestore;
    // metodo para conectar firebase
    public static boolean iniciarFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Primero intentar cargar desde el classpath (recursos empaquetados)
                String[] candidatosClasspath = new String[] {
                    "assets/firebase/firebase-credentials.json",
                    "assets/firabase/firebase-credentials.json", // manejo del typo si existe
                    "firebase-credentials.json"
                };

                InputStream credStream = null;
                String rutaEncontrada = null;

                for (String cp : candidatosClasspath) {
                    credStream = FirebaseConfig.class.getClassLoader().getResourceAsStream(cp);
                    if (credStream != null) {
                        rutaEncontrada = "classpath:" + cp;
                        break;
                    }
                }

                // Si no está en el classpath, intentar rutas del sistema de archivos (desarrollo)
                if (credStream == null) {
                    String[] posiblesRutas = new String[] {
                        "src/main/java/assets/firebase/firebase-credentials.json",
                        "src/main/java/assets/firabase/firebase-credentials.json",
                        "src/main/resources/assets/firebase/firebase-credentials.json",
                        "src/main/resources/assets/firabase/firebase-credentials.json",
                        "target/classes/assets/firebase/firebase-credentials.json",
                        "target/classes/assets/firabase/firebase-credentials.json",
                        "firebase-credentials.json"
                    };

                    for (String ruta : posiblesRutas) {
                        try (FileInputStream test = new FileInputStream(ruta)) {
                            // si abre, lo usamos
                            rutaEncontrada = ruta;
                            credStream = new FileInputStream(ruta);
                            break;
                        } catch (IOException e) {
                            // seguir buscando
                        }
                    }
                }

                if (credStream == null) {
                    System.out.println("Error: No se encontró el archivo de credenciales de Firebase");
                    return false;
                }

                try (InputStream credInput = credStream) {
                    FirebaseOptions opciones = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(credInput)).build();
                    FirebaseApp.initializeApp(opciones);
                    firestore = FirestoreClient.getFirestore();
                }
                return true;  //conexion exitosa
            } else {
                // Firebase ya está inicializado, obtener la instancia de Firestore
                if (firestore == null) {
                    firestore = FirestoreClient.getFirestore();
                }
                return true; // ya estaba en conexion
            }
        } catch (IOException e) {
            System.out.println("Error al conectar.. Intente de nuevo: " + e.getMessage());
            e.printStackTrace();
            return false; // error de conexion
        } catch (Exception e) {
            System.out.println("Error inesperado al conectar Firebase: " + e.getMessage());
            e.printStackTrace();
            return false;
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
            if (!iniciarFirebase()) {
                throw new IllegalStateException("No se pudo inicializar Firebase. Verifique las credenciales.");
            }
        }
        if (firestore == null) {
            throw new IllegalStateException("Firestore no está inicializado. Verifique la configuración de Firebase.");
        }
        return firestore;
    }
    
}
