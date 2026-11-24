package model;

import exception.TiendaException;
import java.util.ArrayList;
import java.util.List;


public class Booster extends Producto {

    public static final int TAMANO = 10;

    private List<Carta> cartas;
    private boolean abierto;
    private String propietarioId;   

    public Booster(String id, String nombre, double precioUSD, List<Carta> cartasRegistradas) {
        super(id, nombre, precioUSD, 1, TipoProducto.BOOSTER);
        this.cartas = generarCartas(cartasRegistradas);
        this.abierto = false;
        this.propietarioId = null;
    }

    public boolean estaDisponible() { return propietarioId == null; }
    public boolean estaAbierto() { return abierto; }
    public String getPropietarioId() { return propietarioId; }

    public boolean esPropietario(String clienteId) {
        return propietarioId != null && propietarioId.equals(clienteId);
    }

    public void marcarVendido(String clienteId) {
        if (!estaDisponible()) throw TiendaException.conflicto("El booster " + getId() + " ya fue vendido");
        this.propietarioId = clienteId;
    }

    public List<Carta> abrir(String clienteId) {
        if (!esPropietario(clienteId)) throw TiendaException.prohibido("El booster " + getId() + " no le pertenece");
        if (abierto) throw TiendaException.conflicto("El booster " + getId() + " ya fue abierto");
        abierto = true;
        return cartas;
    }

    public List<Carta> getCartasSiAbierto() { return abierto ? cartas : null; }

    private static List<Carta> generarCartas(List<Carta> registradas) {
        if (registradas == null || registradas.isEmpty()) {
            throw TiendaException.conflicto("No hay cartas registradas: no se puede crear el booster");
        }
        List<Carta> resultado = new ArrayList<>();
        for (int i = 0; i < TAMANO; i++) {
            Rareza sorteada = Rareza.sortear();
            resultado.add(elegirCarta(registradas, sorteada));
        }
        return resultado;
    }

    private static Carta elegirCarta(List<Carta> registradas, Rareza sorteada) {
        for (Rareza r = sorteada; r != null; r = r.inferior()) {
            Carta c = alAzar(registradas, r);
            if (c != null) return c;
        }
        for (Rareza r = sorteada.superior(); r != null; r = r.superior()) {
            Carta c = alAzar(registradas, r);
            if (c != null) return c;
        }
        throw TiendaException.conflicto("No hay cartas registradas");
    }

    private static Carta alAzar(List<Carta> registradas, Rareza rareza) {
        List<Carta> candidatas = new ArrayList<>();
        for (Carta c : registradas) if (c.getRareza() == rareza) candidatas.add(c);
        if (candidatas.isEmpty()) return null;
        return candidatas.get((int) (Math.random() * candidatas.size()));
    }
}
