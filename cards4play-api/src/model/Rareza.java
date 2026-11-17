package model;

import exception.TiendaException;
import java.util.Random;


public enum Rareza {
    N("N", 50),
    UC("UC", 25),
    R("R", 15),
    SR("SR", 7),
    UR("UR", 3);

    private static final Random RANDOM = new Random();

    private final String codigo;
    private final int probabilidad;

    Rareza(String codigo, int probabilidad) {
        this.codigo = codigo;
        this.probabilidad = probabilidad;
    }

    static {
        int suma = 0;
        for (Rareza r : values()) suma += r.probabilidad;
        if (suma != 100) throw new IllegalStateException("Las probabilidades deben sumar 100 y suman " + suma);
    }

    public String getCodigo() { return codigo; }
    public int getProbabilidad() { return probabilidad; }


    public static Rareza sortear() {
        int numero = RANDOM.nextInt(100) + 1;
        int acumulado = 0;
        for (Rareza r : values()) {
            acumulado += r.probabilidad;
            if (numero <= acumulado) return r;
        }
        return N;
    }


    public Rareza inferior() {
        int i = ordinal() - 1;
        return i >= 0 ? values()[i] : null;
    }


    public Rareza superior() {
        int i = ordinal() + 1;
        return i < values().length ? values()[i] : null;
    }

    public static Rareza desdeTexto(String texto) {
        for (Rareza r : values()) {
            if (r.codigo.equalsIgnoreCase(texto == null ? "" : texto.trim())) return r;
        }
        throw TiendaException.datosInvalidos("Rareza inválida: " + texto + " (use N, UC, R, SR o UR)");
    }
}
