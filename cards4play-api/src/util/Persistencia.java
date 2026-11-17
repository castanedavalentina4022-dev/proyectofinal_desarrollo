package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Persistencia {

    private final Gson gson = GsonFactory.crear();
    private final Path carpeta;

    public Persistencia() {
        this(System.getenv().getOrDefault("DATA_DIR", "data"));
    }

    public Persistencia(String carpeta) {
        this.carpeta = Paths.get(carpeta);
    }

    public <T> List<T> leerLista(String archivo, Class<T> clase) {
        try {
            Path ruta = carpeta.resolve(archivo);
            if (!Files.exists(ruta)) return new ArrayList<>();
            String texto = Files.readString(ruta, StandardCharsets.UTF_8);
            if (texto.isBlank()) return new ArrayList<>();
            List<T> lista = gson.fromJson(texto, TypeToken.getParameterized(List.class, clase).getType());
            return lista == null ? new ArrayList<>() : lista;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo leer " + archivo + ": " + e.getMessage(), e);
        }
    }

    public void guardarLista(String archivo, List<?> lista) {
        try {
            Files.createDirectories(carpeta);
            Files.writeString(carpeta.resolve(archivo), gson.toJson(lista), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo guardar " + archivo + ": " + e.getMessage(), e);
        }
    }
}
