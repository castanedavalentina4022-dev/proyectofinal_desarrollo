package model;

import java.util.ArrayList;
import java.util.List;

public class Inventario {

    private final List<Producto> productos;

    public Inventario(List<Producto> productos) { this.productos = productos; }

    public void agregar(Producto p) { productos.add(p); }

    public Producto buscarPorId(String id) {
        for (Producto p : productos) if (p.getId().equals(id)) return p;
        return null;
    }

    public List<Producto> listarTodos() { return new ArrayList<>(productos); }

    public List<Producto> listarCatalogo() {
        List<Producto> r = new ArrayList<>();
        for (Producto p : productos) {
            if (p instanceof Booster && !((Booster) p).estaDisponible()) continue;
            r.add(p);
        }
        return r;
    }

    public List<Carta> listarCartas() {
        List<Carta> r = new ArrayList<>();
        for (Producto p : productos) if (p instanceof Carta) r.add((Carta) p);
        return r;
    }

    public List<Booster> listarBoostersDisponibles() {
        List<Booster> r = new ArrayList<>();
        for (Producto p : productos) {
            if (p instanceof Booster && ((Booster) p).estaDisponible()) r.add((Booster) p);
        }
        return r;
    }

    public List<Booster> boostersDeCliente(String clienteId) {
        List<Booster> r = new ArrayList<>();
        for (Producto p : productos) {
            if (p instanceof Booster && ((Booster) p).esPropietario(clienteId)) r.add((Booster) p);
        }
        return r;
    }
}
