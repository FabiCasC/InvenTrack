
package app.service;

import app.model.Usuarios;
import app.repository.UsuarioRepository;


public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Usuarios login(String email, String contraseña) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("El email no puede estar vacío");
        }
        
        if (contraseña == null || contraseña.trim().isEmpty()) {
            throw new Exception("La contraseña no puede estar vacía");
        }

        Usuarios usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            throw new Exception("Usuario no encontrado");
        }

        if (!usuario.getContraseña().equals(contraseña)) {
            throw new Exception("Contraseña incorrecta");
        }

        return usuario;
    }

    public String registrar(String nombre, String email, String contraseña) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacío");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new Exception("El email no puede estar vacío");
        }

        if (contraseña == null || contraseña.trim().isEmpty()) {
            throw new Exception("La contraseña no puede estar vacía");
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new Exception("El email ya está registrado");
        }

        Usuarios nuevoUsuario = new Usuarios();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContraseña(contraseña);

        return usuarioRepository.save(nuevoUsuario);
    }
}
