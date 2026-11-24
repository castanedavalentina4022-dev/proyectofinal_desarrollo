package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import model.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GsonFactory {

    public static Gson crear() {
        JsonDeserializer<Producto> productoDeserializer = (json, tipo, contexto) -> {
            JsonObject obj = json.getAsJsonObject();
            TipoProducto t = TipoProducto.valueOf(obj.get("tipo").getAsString());
            switch (t) {
                case CARTA:     return contexto.deserialize(obj, Carta.class);
                case BOOSTER:   return contexto.deserialize(obj, Booster.class);
                case SELLADO:   return contexto.deserialize(obj, ProductoSellado.class);
                default:        return contexto.deserialize(obj, Accesorio.class);
            }
        };

        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Producto.class, productoDeserializer)
                .create();
    }

    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override public void write(JsonWriter out, LocalDate v) throws IOException {
            if (v == null) out.nullValue(); else out.value(v.toString());
        }
        @Override public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
            return LocalDate.parse(in.nextString());
        }
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override public void write(JsonWriter out, LocalDateTime v) throws IOException {
            if (v == null) out.nullValue(); else out.value(v.toString());
        }
        @Override public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
            return LocalDateTime.parse(in.nextString());
        }
    }
}
