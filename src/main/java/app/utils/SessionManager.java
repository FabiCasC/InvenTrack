package app.utils;

import app.model.Usuarios;

/**
 * Gestor de sesión para almacenar el usuario actual
 * Permite acceder al usuario desde cualquier parte de la aplicación
 */
public class SessionManager {
    private static SessionManager instance;
    private Usuarios usuarioActual;

    private SessionManager() {
        // Constructor privado para singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUsuarioActual(Usuarios usuario) {
        this.usuarioActual = usuario;
    }

    public Usuarios getUsuarioActual() {
        return usuarioActual;
    }

    public String getUsuarioId() {
        return usuarioActual != null ? usuarioActual.getUsuarioId() : null;
    }

    public String getUsuarioNombre() {
        return usuarioActual != null ? usuarioActual.getNombre() : null;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
}

