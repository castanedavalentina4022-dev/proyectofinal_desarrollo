package model;

public class ProductoSellado extends Producto {
    public ProductoSellado(String id, String nombre, double precioUSD, int stock) {
        super(id, nombre, precioUSD, stock, TipoProducto.SELLADO);
    }
}
