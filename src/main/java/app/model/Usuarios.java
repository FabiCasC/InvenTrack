package app.model;

/**
 * Modelo base para Usuarios
 * RF1.1 - Datos mínimos requeridos: correo, contraseña
 */
public class Usuarios {
    private String usuarioId;
    private String nombre;
    private String email;
    private String contraseña;
    private String rol;

    public Usuarios() {
    }

    public Usuarios(String usuarioId, String nombre, String email, String contraseña, String rol) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    @Override
    public String toString() {
        return "Usuarios{" + "usuarioId=" + usuarioId + ", nombre=" + nombre + ", email=" + email + ", contrase\u00f1a=" + contraseña + ", rol=" + rol + '}';
    }
    
    
}
