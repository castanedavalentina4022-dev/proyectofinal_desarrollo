package model;

public class Accesorio extends Producto {
    public Accesorio(String id, String nombre, double precioUSD, int stock) {
        super(id, nombre, precioUSD, stock, TipoProducto.ACCESORIO);
    }
}
