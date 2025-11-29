package app;

import app.config.FirebaseConfig;
import app.view.LoginView;

public class Main {

    public static void main(String[] args) {
        try {
            // Inicializar Firebase
            FirebaseConfig.iniciarFirebase();
            
            // Mostrar la ventana de login
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new LoginView().setVisible(true);
                }
            });

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

