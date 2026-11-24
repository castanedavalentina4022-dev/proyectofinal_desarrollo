package api;

import model.*;
import service.TasaCambioService.Tasa;
import java.util.*;


public class Vistas {

    public static Map<String, Object> cliente(Cliente c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("username", c.getUsername());
        m.put("rol", c.getRol());
        m.put("torneosIds", c.getTorneosIds());
        return m;
    }

    public static Map<String, Object> carta(Carta c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("nombre", c.getNombre());
        m.put("rareza", c.getRareza().getCodigo());
        return m;
    }

    public static Map<String, Object> producto(Producto p, boolean admin, Tasa tasa) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("tipo", p.getTipo());
        m.put("nombre", p.getNombre());
        m.put("precioCOP", p.calcularPrecioCOP(tasa.valor()));
        m.put("fuenteTasa", tasa.fuente());
        if (p instanceof Carta) m.put("rareza", ((Carta) p).getRareza().getCodigo());
        if (p instanceof Booster) {
            m.put("disponible", ((Booster) p).estaDisponible());
        } else {
            m.put("stock", p.getStock());
        }
        if (admin) m.put("precioUSD", p.getPrecioUSD());
        return m;
    }

    public static List<Map<String, Object>> productos(List<? extends Producto> lista, boolean admin) {
        Tasa tasa = Producto.tasaVigente();      // una sola consulta para toda la lista
        List<Map<String, Object>> r = new ArrayList<>();
        for (Producto p : lista) r.add(producto(p, admin, tasa));
        return r;
    }

    public static Map<String, Object> boosterPropio(Booster b) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", b.getId());
        m.put("nombre", b.getNombre());
        m.put("abierto", b.estaAbierto());
        if (b.estaAbierto()) {
            List<Map<String, Object>> cartas = new ArrayList<>();
            for (Carta c : b.getCartasSiAbierto()) cartas.add(carta(c));
            m.put("cartas", cartas);
        } else {
            m.put("cartas", "Sin abrir");
        }
        return m;
    }

    public static Map<String, Object> compra(Compra compra, Inventario inventario, boolean admin) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", compra.getId());
        m.put("clienteId", compra.getClienteId());
        m.put("fecha", compra.getFecha().toString());

        List<Map<String, Object>> detalles = new ArrayList<>();
        for (DetalleCompra d : compra.getDetalles()) {
            Map<String, Object> dm = new LinkedHashMap<>();
            dm.put("productoId", d.getProductoId());
            dm.put("producto", d.getNombreProducto());
            dm.put("tipo", d.getTipo());
            dm.put("cantidad", d.getCantidad());
            dm.put("precioUnitarioCOP", d.getPrecioUnitarioCOP());
            dm.put("subtotalCOP", d.getSubtotalCOP());
            if (admin) dm.put("precioUnitarioUSD", d.getPrecioUnitarioUSD());
            if (d.getTipo() == TipoProducto.BOOSTER) {
                Producto p = inventario.buscarPorId(d.getProductoId());
                if (p instanceof Booster) {
                    Booster b = (Booster) p;
                    dm.put("abierto", b.estaAbierto());
                    dm.put("cartas", b.estaAbierto() ? boosterPropio(b).get("cartas") : "Sin abrir");
                }
            }
            detalles.add(dm);
        }
        m.put("detalles", detalles);
        m.put("totalCOP", compra.getTotalCOP());
        if (admin) m.put("totalUSD", compra.getTotalUSD());
        m.put("tasaUsada", compra.getTasaUsada());
        m.put("fuenteTasa", compra.getFuenteTasa());
        return m;
    }

    public static Map<String, Object> torneo(Torneo t, boolean admin) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", t.getId());
        m.put("nombre", t.getNombre());
        m.put("fecha", t.getFecha().toString());
        m.put("maxParticipantes", t.getMaxParticipantes());
        m.put("inscritos", t.getParticipantesIds().size());
        m.put("cuposDisponibles", t.cuposDisponibles());
        m.put("vigente", t.estaVigente());
        if (admin) m.put("participantesIds", t.getParticipantesIds());
        return m;
    }
}
