package service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TasaCambioService {

    public record Tasa(double valor, String fuente) {}

    private static final String URL = "https://open.er-api.com/v6/latest/USD";
    private static final int TIMEOUT_SEGUNDOS = 3;
    private static final double TASA_DEFECTO_POR_OMISION = 4000.0;

    private final HttpClient cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SEGUNDOS))
            .build();

    public Tasa obtener() {
        try {
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(URL))
                    .timeout(Duration.ofSeconds(TIMEOUT_SEGUNDOS))
                    .GET()
                    .build();
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) throw new RuntimeException("status " + respuesta.statusCode());

            JsonObject json = JsonParser.parseString(respuesta.body()).getAsJsonObject();
            double cop = json.getAsJsonObject("rates").get("COP").getAsDouble();
            if (cop <= 0) throw new RuntimeException("tasa inválida");
            return new Tasa(cop, "API");
        } catch (Exception e) {
            double defecto = tasaPorDefecto();
            System.out.println("La API no respondió, por lo que se toma un valor por defecto de " + defecto);
            return new Tasa(defecto, "DEFECTO");
        }
    }

    public static double tasaPorDefecto() {
        try {
            return Double.parseDouble(System.getenv("TASA_DEFECTO"));
        } catch (Exception e) {
            return TASA_DEFECTO_POR_OMISION;
        }
    }
}
