package model;

import exception.TiendaException;

public enum TipoProducto {
    CARTA, BOOSTER, SELLADO, ACCESORIO;

    public static TipoProducto desdeTexto(String texto) {
        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (Exception e) {
            throw TiendaException.datosInvalidos("Tipo de producto inválido: " + texto
                    + " (use CARTA, BOOSTER, SELLADO o ACCESORIO)");
        }
    }
}
