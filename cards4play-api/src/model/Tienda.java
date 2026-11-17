package model;

import exception.TiendaException;
import util.Persistencia;
import java.time.LocalDate;
import java.util.*;


public class Tienda {

    public record ItemCompra(String productoId, int cantidad) {}

    private final Persistencia persistencia;
    private final List<Administrador> administradores;
    private final List<Cliente> clientes;
    private final Inventario inventario;
    private final List<Torneo> torneos;

    public Tienda() { this(new Persistencia()); }

    public Tienda(Persistencia persistencia) {
        this.persistencia = persistencia;
        this.administradores = persistencia.leerLista("administradores.json", Administrador.class);
        this.clientes = persistencia.leerLista("clientes.json", Cliente.class);
        this.inventario = new Inventario(persistencia.leerLista("productos.json", Producto.class));
        this.torneos = persistencia.leerLista("torneos.json", Torneo.class);

        if (administradores.isEmpty()) {   // administrador por defecto
            administradores.add(new Administrador("ADM-1", "admin", "admin123"));
            guardar();
        }
    }

    public Inventario getInventario() { return inventario; }

    private void guardar() {
        persistencia.guardarLista("administradores.json", administradores);
        persistencia.guardarLista("clientes.json", clientes);
        persistencia.guardarLista("productos.json", inventario.listarTodos());
        persistencia.guardarLista("torneos.json", torneos);
    }

    private static String siguienteId(String prefijo, Collection<String> existentes) {
        int max = 0;
        for (String id : existentes) {
            if (id.startsWith(prefijo)) {
                try { max = Math.max(max, Integer.parseInt(id.substring(prefijo.length()))); }
                catch (NumberFormatException ignorado) { }
            }
        }
        return prefijo + (max + 1);
    }

    //USUARIOS
    public synchronized Usuario autenticar(String username, String password) {
        for (Administrador a : administradores) if (a.validarCredenciales(username, password)) return a;
        for (Cliente c : clientes) if (c.validarCredenciales(username, password)) return c;
        return null;
    }

    public synchronized Cliente registrarCliente(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank())
            throw TiendaException.datosInvalidos("username y password son obligatorios");
        for (Administrador a : administradores)
            if (a.getUsername().equalsIgnoreCase(username.trim()))
                throw TiendaException.conflicto("El usuario '" + username + "' ya existe");
        for (Cliente c : clientes)
            if (c.getUsername().equalsIgnoreCase(username.trim()))
                throw TiendaException.conflicto("El usuario '" + username + "' ya existe");

        List<String> ids = new ArrayList<>();
        for (Cliente c : clientes) ids.add(c.getId());
        Cliente nuevo = new Cliente(siguienteId("CLI-", ids), username.trim(), password);
        clientes.add(nuevo);
        guardar();
        return nuevo;
    }

    public synchronized List<Cliente> listarClientes() { return new ArrayList<>(clientes); }

    public synchronized Cliente buscarCliente(String id) {
        for (Cliente c : clientes) if (c.getId().equals(id)) return c;
        throw TiendaException.noEncontrado("Cliente no encontrado: " + id);
    }

    //PRODUCTOS
    public synchronized Producto buscarProducto(String id) {
        Producto p = inventario.buscarPorId(id);
        if (p == null) throw TiendaException.noEncontrado("Producto no encontrado: " + id);
        return p;
    }

    private String nuevoIdProducto(String prefijo) {
        List<String> ids = new ArrayList<>();
        for (Producto p : inventario.listarTodos()) ids.add(p.getId());
        return siguienteId(prefijo, ids);
    }

    public synchronized Producto registrarProducto(Administrador admin, TipoProducto tipo, String nombre,
                                                   double precioUSD, int stock, Rareza rareza) {
        Producto p;
        switch (tipo) {
            case CARTA:
                if (rareza == null) throw TiendaException.datosInvalidos("La carta requiere 'rareza'");
                p = new Carta(nuevoIdProducto("CAR-"), nombre, precioUSD, stock, rareza);
                break;
            case SELLADO:
                p = new ProductoSellado(nuevoIdProducto("SEL-"), nombre, precioUSD, stock);
                break;
            case ACCESORIO:
                p = new Accesorio(nuevoIdProducto("ACC-"), nombre, precioUSD, stock);
                break;
            default:
                throw TiendaException.datosInvalidos("Los boosters se crean en POST /boosters");
        }
        admin.registrarProducto(p);
        inventario.agregar(p);
        guardar();
        return p;
    }

    public synchronized Producto actualizarProducto(String id, String nombre, Double precioUSD,
                                                    Integer stock, Rareza rareza) {
        Producto p = buscarProducto(id);
        if (p instanceof Booster && (stock != null || rareza != null))
            throw TiendaException.datosInvalidos("Un booster solo permite cambiar nombre y precio");
        if (rareza != null && !(p instanceof Carta))
            throw TiendaException.datosInvalidos("Solo las cartas tienen rareza");

        if (nombre != null) p.setNombre(nombre);
        if (precioUSD != null) p.setPrecioUSD(precioUSD);
        if (stock != null) p.setStock(stock);
        if (rareza != null) ((Carta) p).setRareza(rareza);
        guardar();
        return p;
    }

    public synchronized Booster crearBooster(Administrador admin, String nombre, double precioUSD) {
        Booster b = admin.crearBooster(nuevoIdProducto("BOO-"), nombre, precioUSD, inventario.listarCartas());
        inventario.agregar(b);
        guardar();
        return b;
    }

    //TORNEOS
    public synchronized Torneo crearTorneo(Administrador admin, String nombre, LocalDate fecha, int max) {
        List<String> ids = new ArrayList<>();
        for (Torneo t : torneos) ids.add(t.getId());
        Torneo torneo = admin.crearTorneo(siguienteId("TOR-", ids), nombre, fecha, max);
        torneos.add(torneo);
        guardar();
        return torneo;
    }

    public synchronized List<Torneo> listarTorneos() { return new ArrayList<>(torneos); }

    public synchronized Torneo buscarTorneo(String id) {
        for (Torneo t : torneos) if (t.getId().equals(id)) return t;
        throw TiendaException.noEncontrado("Torneo no encontrado: " + id);
    }

    public synchronized Torneo inscribirEnTorneo(Cliente cliente, String torneoId) {
        Torneo torneo = buscarTorneo(torneoId);
        torneo.inscribir(cliente.getId());
        cliente.agregarTorneo(torneoId);
        guardar();
        return torneo;
    }


}
