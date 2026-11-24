package controller;

import com.google.gson.JsonObject;
import model.*;
import java.util.ArrayList;
import java.util.List;
import service.TasaCambioService.Tasa;
import static spark.Spark.*;

public class ProductoController extends BaseController {

    public ProductoController(Tienda tienda) { super(tienda); }

    @Override
    public void rutas() {

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
                throw exception.TiendaException.noEncontrado("Producto no encontrado: " + p.getId());
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
}
