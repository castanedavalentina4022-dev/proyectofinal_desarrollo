package model;

public class Usuario {
    private String id;
    private String username;
    private String password;
    private Rol rol;

    public Usuario(String id, String username, String password, Rol rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public Rol getRol() { return rol; }

    public boolean validarCredenciales(String user, String pass) {
        return username.equals(user) && password.equals(pass);
    }

    public boolean esAdmin() { return rol == Rol.ADMIN; }
}
