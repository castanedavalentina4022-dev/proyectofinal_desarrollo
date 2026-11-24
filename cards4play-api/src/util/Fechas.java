package util;

import java.time.LocalDate;
import java.time.ZoneId;

public class Fechas {
    public static LocalDate hoy() {
        return LocalDate.now(ZoneId.of("America/Bogota"));
    }
}
