/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.controller;

import app.service.AuthService;
import javax.swing.JOptionPane;

/**
 *
 * @author GMG
 */
public class RegistroController {
     private final AuthService authService;

    public RegistroController() {
        this.authService = new AuthService();
    }

    public boolean registrarUsuario(String nombre, String email, String contraseña, String confirmarContraseña) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                mostrarError("Por favor ingrese su nombre");
                return false;
            }

            if (email == null || email.trim().isEmpty()) {
                mostrarError("Por favor ingrese su email");
                return false;
            }

            if (contraseña == null || contraseña.trim().isEmpty()) {
                mostrarError("Por favor ingrese una contraseña");
                return false;
            }

            if (!contraseña.equals(confirmarContraseña)) {
                mostrarError("Las contraseñas no coinciden");
                return false;
            }

            if (contraseña.length() < 6) {
                mostrarError("La contraseña debe tener al menos 6 caracteres");
                return false;
            }

            String userId = authService.registrar(nombre.trim(), email.trim(), contraseña);
            mostrarExito("¡Usuario registrado exitosamente!\nTu ID es: " + userId);
            return true;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
    }
}
