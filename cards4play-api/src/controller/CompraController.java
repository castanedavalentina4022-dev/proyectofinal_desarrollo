package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import exception.TiendaException;
import model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static spark.Spark.*;

public class CompraController extends BaseController {

    public CompraController(Tienda tienda) { super(tienda); }

    @Override
    public void rutas() {

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
}
