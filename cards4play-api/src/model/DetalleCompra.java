package model;

public class DetalleCompra {
    private String productoId;
    private String nombreProducto;
    private TipoProducto tipo;
    private int cantidad;
    private double precioUnitarioUSD;
    private double precioUnitarioCOP;

    public DetalleCompra(Producto producto, int cantidad, double tasa) {
        this.productoId = producto.getId();
        this.nombreProducto = producto.getNombre();
        this.tipo = producto.getTipo();
        this.cantidad = cantidad;
        this.precioUnitarioUSD = producto.getPrecioUSD();
        this.precioUnitarioCOP = producto.calcularPrecioCOP(tasa);
    }

    public String getProductoId() { return productoId; }
    public String getNombreProducto() { return nombreProducto; }
    public TipoProducto getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitarioUSD() { return precioUnitarioUSD; }
    public double getPrecioUnitarioCOP() { return precioUnitarioCOP; }

    public double getSubtotalUSD() { return redondear(precioUnitarioUSD * cantidad); }
    public double getSubtotalCOP() { return redondear(precioUnitarioCOP * cantidad); }

    private static double redondear(double v) { return Math.round(v * 100.0) / 100.0; }
}
