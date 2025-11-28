package app;

import app.config.FirebaseConfig;
import app.service.ReporteService;



public class Main {

    //metodos de prueba en menu
    public static void main(String[] args) {
        try {
            FirebaseConfig.iniciarFirebase();
            ReporteService reporteService = new ReporteService();
            reporteService.generarReportes();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
}

