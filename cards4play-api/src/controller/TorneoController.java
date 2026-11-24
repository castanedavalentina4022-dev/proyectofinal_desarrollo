package controller;

import com.google.gson.JsonObject;
import exception.TiendaException;
import model.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static spark.Spark.*;

public class TorneoController extends BaseController {

    public TorneoController(Tienda tienda) { super(tienda); }

    @Override
    public void rutas() {

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
}
