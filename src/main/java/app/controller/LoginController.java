/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.controller;

import app.model.Usuarios;
import app.service.AuthService;
import javax.swing.JOptionPane;

/**
 *
 * @author GMG
 */
public class LoginController {
     private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    public Usuarios iniciarSesion(String email, String contraseña) {
        try {
            if (email == null || email.trim().isEmpty()) {
                mostrarError("Por favor ingrese su email");
                return null;
            }

            if (contraseña == null || contraseña.trim().isEmpty()) {
                mostrarError("Por favor ingrese su contraseña");
                return null;
            }

            Usuarios usuario = authService.login(email.trim(), contraseña);
            //mostrarExito("¡Bienvenido " + usuario.getNombre() + "!");
            return usuario;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return null;
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Login exitoso", JOptionPane.INFORMATION_MESSAGE);
    }
}
