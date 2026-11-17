package model;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {

    private List<Compra> compras = new ArrayList<>();
    private List<String> torneosIds = new ArrayList<>();

    public Cliente(String id, String username, String password) {
        super(id, username, password, Rol.CLIENTE);
    }

    public void agregarCompra(Compra compra) { compras.add(compra); }

    public List<Compra> getHistorial() { return compras; }

    public void agregarTorneo(String torneoId) { torneosIds.add(torneoId); }

    public List<String> getTorneosIds() { return torneosIds; }
}
