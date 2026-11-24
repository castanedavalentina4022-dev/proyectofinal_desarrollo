package model;

import exception.TiendaException;
import service.TasaCambioService;
import service.TasaCambioService.Tasa;


public class Producto {


    private static final long VIGENCIA_MS = 60_000;          
    private static final TasaCambioService servicioTasa = new TasaCambioService();
    private static Tasa tasaActual;
    private static long ultimaConsulta = 0;


    public static synchronized Tasa tasaVigente() {
        long ahora = System.currentTimeMillis();
        if (tasaActual == null || ahora - ultimaConsulta > VIGENCIA_MS) {
            tasaActual = servicioTasa.obtener();
            ultimaConsulta = ahora;
        }
        return tasaActual;
    }


    private String id;
    private String nombre;
    private double precioUSD;
    private int stock;
    private TipoProducto tipo;

    public Producto(String id, String nombre, double precioUSD, int stock, TipoProducto tipo) {
        validarNombre(nombre);
        validarPrecio(precioUSD);
        validarStock(stock);
        this.id = id;
        this.nombre = nombre.trim();
        this.precioUSD = precioUSD;
        this.stock = stock;
        this.tipo = tipo;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecioUSD() { return precioUSD; }
    public int getStock() { return stock; }
    public TipoProducto getTipo() { return tipo; }

    public void setNombre(String nombre) { validarNombre(nombre); this.nombre = nombre.trim(); }
    public void setPrecioUSD(double precioUSD) { validarPrecio(precioUSD); this.precioUSD = precioUSD; }
    public void setStock(int stock) { validarStock(stock); this.stock = stock; }

    public void descontarStock(int cantidad) {
        if (cantidad > stock) throw TiendaException.conflicto("Stock insuficiente de " + nombre);
        stock -= cantidad;
    }


    public double calcularPrecioCOP() { return calcularPrecioCOP(tasaVigente().valor()); }

    public double calcularPrecioCOP(double tasa) {
        return Math.round(precioUSD * tasa * 100.0) / 100.0;
    }

    private static void validarNombre(String n) {
        if (n == null || n.isBlank()) throw TiendaException.datosInvalidos("El nombre es obligatorio");
    }
    private static void validarPrecio(double p) {
        if (p <= 0) throw TiendaException.datosInvalidos("El precio en USD debe ser mayor que 0");
    }
    private static void validarStock(int s) {
        if (s < 0) throw TiendaException.datosInvalidos("El stock no puede ser negativo");
    }
}
