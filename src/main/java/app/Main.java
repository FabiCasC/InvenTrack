package app;

import app.config.FirebaseConfig;
import app.service.UsuarioService;

public class Main {
    //metodos de prueba
    public static void main(String[] args) {
        FirebaseConfig.estado();
        UsuarioService usuario = new UsuarioService();
        usuario.mostrarUsuarios(); 
    }    
}
    

