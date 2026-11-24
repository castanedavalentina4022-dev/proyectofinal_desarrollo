package api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import exception.TiendaException;
import model.*;
import service.TasaCambioService.Tasa;
import spark.Request;
import util.GsonFactory;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import static spark.Spark.*;


public class TiendaApi {

    private final Tienda tienda;
    private final Gson gson = GsonFactory.crear();

    public TiendaApi(Tienda tienda) { this.tienda = tienda; }

    public void iniciar() {
        int puerto = Integer.parseInt(System.getenv().getOrDefault("PORT", "4567"));
        port(puerto);
        configurar();
        rutasUsuarios();
        rutasProductos();
        rutasBoosters();
        rutasTorneos();
        rutasCompras();
        System.out.println("API corriendo en http://localhost:" + puerto);
    }

    private void configurar() {

        before((req, res) -> {
            res.type("application/json");
            if ("/health".equals(req.pathInfo())) return;
            req.attribute("usuario", autenticar(req.headers("Authorization")));
        });

        get("/health", (req, res) -> gson.toJson(Map.of("mensaje", "API funcionando")));

        notFound((req, res) -> {
            res.type("application/json");
            return gson.toJson(Map.of("error", "Ruta no encontrada"));
        });

        exception(TiendaException.class, (e, req, res) -> {
            res.status(e.getStatus());
            res.type("application/json");
            res.body(gson.toJson(Map.of("error", e.getMessage())));
        });

        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(500);
            res.type("application/json");
            res.body(gson.toJson(Map.of("error", "Error interno: " + e.getMessage())));
        });
    }


    private void rutasUsuarios() {

        post("/clientes", (req, res) -> {
            admin(req);
            JsonObject body = cuerpo(req);
            Cliente nuevo = tienda.registrarCliente(texto(body, "username"), texto(body, "password"));
            res.status(201);
            return json(Vistas.cliente(nuevo));
        });

        get("/clientes", (req, res) -> {
            admin(req);
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Cliente c : tienda.listarClientes()) lista.add(Vistas.cliente(c));
            return json(lista);
        });

        get("/clientes/:id", (req, res) -> {
            admin(req);
            return json(Vistas.cliente(tienda.buscarCliente(req.params(":id"))));
        });
    }


    private void rutasProductos() {

        get("/productos", (req, res) -> {
            Usuario u = usuario(req);
            String filtro = req.queryParams("tipo");
            List<Producto> lista = new ArrayList<>();
            for (Producto p : tienda.getInventario().listarCatalogo()) {
                if (filtro == null || p.getTipo() == TipoProducto.desdeTexto(filtro)) lista.add(p);
            }
            return json(Vistas.productos(lista, u.esAdmin()));
        });

        get("/productos/:id", (req, res) -> {
            Usuario u = usuario(req);
            Producto p = tienda.buscarProducto(req.params(":id"));
            if (p instanceof Booster && !((Booster) p).estaDisponible() && u.esAdmin() == false)
                throw TiendaException.noEncontrado("Producto no encontrado: " + p.getId());
            Tasa tasa = Producto.tasaVigente();
            return json(Vistas.producto(p, u.esAdmin(), tasa));
        });

        post("/productos", (req, res) -> {
            Administrador admin = admin(req);
            JsonObject body = cuerpo(req);
            TipoProducto tipo = TipoProducto.desdeTexto(texto(body, "tipo"));
            Rareza rareza = tipo == TipoProducto.CARTA ? Rareza.desdeTexto(texto(body, "rareza")) : null;
            Producto p = tienda.registrarProducto(admin, tipo, texto(body, "nombre"),
                    numero(body, "precioUSD"), entero(body, "stock"), rareza);
            res.status(201);
            return json(Vistas.producto(p, true, Producto.tasaVigente()));
        });

        patch("/productos/:id", (req, res) -> {
            admin(req);
            JsonObject body = cuerpo(req);
            String r = textoOpcional(body, "rareza");
            Producto p = tienda.actualizarProducto(req.params(":id"),
                    textoOpcional(body, "nombre"),
                    numeroOpcional(body, "precioUSD"),
                    enteroOpcional(body, "stock"),
                    r == null ? null : Rareza.desdeTexto(r));
            return json(Vistas.producto(p, true, Producto.tasaVigente()));
        });
    }

 
    private void rutasBoosters() {

        post("/boosters", (req, res) -> {
            Administrador admin = admin(req);
            JsonObject body = cuerpo(req);
            Booster b = tienda.crearBooster(admin, texto(body, "nombre"), numero(body, "precioUSD"));
            res.status(201);
            return json(Vistas.producto(b, true, Producto.tasaVigente()));
        });

        get("/boosters", (req, res) -> {
            Usuario u = usuario(req);
            return json(Vistas.productos(tienda.getInventario().listarBoostersDisponibles(), u.esAdmin()));
        });

        get("/boosters/mios", (req, res) -> {
            Cliente c = cliente(req);
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Booster b : tienda.getInventario().boostersDeCliente(c.getId())) lista.add(Vistas.boosterPropio(b));
            return json(lista);
        });

        post("/boosters/:id/abrir", (req, res) -> {
            Cliente c = cliente(req);
            List<Carta> cartas = tienda.abrirBooster(c, req.params(":id"));
            List<Map<String, Object>> vista = new ArrayList<>();
            for (Carta carta : cartas) vista.add(Vistas.carta(carta));
            return json(Map.of("boosterId", req.params(":id"), "cartas", vista));
        });
    }


    private void rutasTorneos() {

        post("/torneos", (req, res) -> {
            Administrador admin = admin(req);
            JsonObject body = cuerpo(req);
            LocalDate fecha;
            try {
                fecha = LocalDate.parse(texto(body, "fecha"));
            } catch (DateTimeParseException e) {
                throw TiendaException.datosInvalidos("La fecha debe tener formato yyyy-MM-dd");
            }
            Torneo t = tienda.crearTorneo(admin, texto(body, "nombre"), fecha, entero(body, "maxParticipantes"));
            res.status(201);
            return json(Vistas.torneo(t, true));
        });

        get("/torneos", (req, res) -> {
            Usuario u = usuario(req);
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Torneo t : tienda.listarTorneos()) lista.add(Vistas.torneo(t, u.esAdmin()));
            return json(lista);
        });

        get("/torneos/:id", (req, res) -> {
            Usuario u = usuario(req);
            return json(Vistas.torneo(tienda.buscarTorneo(req.params(":id")), u.esAdmin()));
        });

        post("/torneos/:id/inscripciones", (req, res) -> {
            Cliente c = cliente(req);
            Torneo t = tienda.inscribirEnTorneo(c, req.params(":id"));
            res.status(201);
            return json(Vistas.torneo(t, false));
        });
    }

    private void rutasCompras() {

        post("/compras", (req, res) -> {
            Cliente c = cliente(req);
            JsonObject body = cuerpo(req);
            if (!body.has("items") || !body.get("items").isJsonArray())
                throw TiendaException.datosInvalidos("Falta el campo 'items' (lista de productos)");

            List<Tienda.ItemCompra> items = new ArrayList<>();
            JsonArray arreglo = body.getAsJsonArray("items");
            for (JsonElement e : arreglo) {
                if (!e.isJsonObject()) throw TiendaException.datosInvalidos("Cada ítem debe ser un objeto");
                JsonObject item = e.getAsJsonObject();
                items.add(new Tienda.ItemCompra(texto(item, "productoId"), entero(item, "cantidad")));
            }
            Compra compra = tienda.comprar(c, items);
            res.status(201);
            return json(Vistas.compra(compra, tienda.getInventario(), false));
        });

        get("/compras/historial", (req, res) -> {
            Cliente c = cliente(req);
            return json(historial(c, false));
        });

        get("/clientes/:id/compras", (req, res) -> {
            admin(req);
            return json(historial(tienda.buscarCliente(req.params(":id")), true));
        });
    }

    private List<Map<String, Object>> historial(Cliente c, boolean admin) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Compra compra : c.getHistorial()) lista.add(Vistas.compra(compra, tienda.getInventario(), admin));
        return lista;
    }

    private Usuario autenticar(String cabecera) {
        if (cabecera == null || !cabecera.startsWith("Basic "))
            throw TiendaException.noAutenticado("Falta la autenticación (Basic Auth)");
        try {
            String decodificado = new String(Base64.getDecoder().decode(cabecera.substring(6).trim()),
                    StandardCharsets.UTF_8);
            int separador = decodificado.indexOf(':');
            if (separador < 0) throw new IllegalArgumentException();
            Usuario u = tienda.autenticar(decodificado.substring(0, separador),
                                          decodificado.substring(separador + 1));
            if (u == null) throw TiendaException.noAutenticado("Usuario o contraseña inválidos");
            return u;
        } catch (IllegalArgumentException e) {
            throw TiendaException.noAutenticado("Credenciales mal formadas");
        }
    }


    private String json(Object objeto) { return gson.toJson(objeto); }

    private Usuario usuario(Request req) {
        Usuario u = req.attribute("usuario");
        if (u == null) throw TiendaException.noAutenticado("Debe autenticarse");
        return u;
    }

    private Administrador admin(Request req) {
        Usuario u = usuario(req);
        if (!(u instanceof Administrador))
            throw TiendaException.prohibido("Acción reservada al administrador");
        return (Administrador) u;
    }

    private Cliente cliente(Request req) {
        Usuario u = usuario(req);
        if (!(u instanceof Cliente))
            throw TiendaException.prohibido("Acción reservada a los clientes");
        return (Cliente) u;
    }

    private JsonObject cuerpo(Request req) {
        try {
            JsonObject obj = gson.fromJson(req.body(), JsonObject.class);
            if (obj == null) throw TiendaException.datosInvalidos("El cuerpo de la petición está vacío");
            return obj;
        } catch (JsonParseException e) {
            throw TiendaException.datosInvalidos("El cuerpo no es un JSON válido");
        }
    }

    private JsonElement campo(JsonObject o, String nombre, boolean obligatorio) {
        JsonElement e = o.get(nombre);
        if (e == null || e.isJsonNull()) {
            if (obligatorio) throw TiendaException.datosInvalidos("Falta el campo '" + nombre + "'");
            return null;
        }
        return e;
    }

    private String texto(JsonObject o, String nombre) {
        try { return campo(o, nombre, true).getAsString(); }
        catch (TiendaException e) { throw e; }
        catch (RuntimeException e) { throw TiendaException.datosInvalidos("'" + nombre + "' debe ser texto"); }
    }

    private String textoOpcional(JsonObject o, String nombre) {
        JsonElement e = campo(o, nombre, false);
        return e == null ? null : texto(o, nombre);
    }

    private double numero(JsonObject o, String nombre) {
        try { return campo(o, nombre, true).getAsDouble(); }
        catch (TiendaException e) { throw e; }
        catch (RuntimeException e) { throw TiendaException.datosInvalidos("'" + nombre + "' debe ser un número"); }
    }

    private Double numeroOpcional(JsonObject o, String nombre) {
        return campo(o, nombre, false) == null ? null : numero(o, nombre);
    }

    private int entero(JsonObject o, String nombre) {
        try { return campo(o, nombre, true).getAsInt(); }
        catch (TiendaException e) { throw e; }
        catch (RuntimeException e) { throw TiendaException.datosInvalidos("'" + nombre + "' debe ser un entero"); }
    }

    private Integer enteroOpcional(JsonObject o, String nombre) {
        return campo(o, nombre, false) == null ? null : entero(o, nombre);
    }
}
