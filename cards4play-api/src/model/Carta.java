package model;

public class Carta extends Producto {
    private Rareza rareza;

    public Carta(String id, String nombre, double precioUSD, int stock, Rareza rareza) {
        super(id, nombre, precioUSD, stock, TipoProducto.CARTA);
        this.rareza = rareza;
    }

    public Rareza getRareza() { return rareza; }
    public void setRareza(Rareza rareza) { this.rareza = rareza; }
}
