package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Administrador extends Usuario {

    private List<String> torneosCreadosIds = new ArrayList<>();
    private List<String> productosCreadosIds = new ArrayList<>();

    public Administrador(String id, String username, String password) {
        super(id, username, password, Rol.ADMIN);
    }

    public Torneo crearTorneo(String id, String nombre, LocalDate fecha, int maxParticipantes) {
        Torneo torneo = new Torneo(id, nombre, fecha, maxParticipantes);
        torneosCreadosIds.add(torneo.getId());
        return torneo;
    }

    public Producto registrarProducto(Producto producto) {
        productosCreadosIds.add(producto.getId());
        return producto;
    }

    public Booster crearBooster(String id, String nombre, double precioUSD, List<Carta> cartasRegistradas) {
        Booster booster = new Booster(id, nombre, precioUSD, cartasRegistradas);
        productosCreadosIds.add(booster.getId());
        return booster;
    }

    public List<String> getTorneosCreadosIds() { return torneosCreadosIds; }
    public List<String> getProductosCreadosIds() { return productosCreadosIds; }
}
