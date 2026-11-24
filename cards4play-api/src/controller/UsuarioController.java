package controller;

import com.google.gson.JsonObject;
import model.Cliente;
import model.Tienda;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static spark.Spark.*;

public class UsuarioController extends BaseController {

    public UsuarioController(Tienda tienda) { super(tienda); }

    @Override
    public void rutas() {

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
}
