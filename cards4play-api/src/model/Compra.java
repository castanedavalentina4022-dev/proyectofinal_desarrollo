package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Compra {
    private String id;
    private String clienteId;
    private LocalDateTime fecha;
    private List<DetalleCompra> detalles = new ArrayList<>();
    private double totalUSD;
    private double totalCOP;
    private double tasaUsada;
    private String fuenteTasa;

    public Compra(String id, String clienteId, double tasaUsada, String fuenteTasa) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = LocalDateTime.now();
        this.tasaUsada = tasaUsada;
        this.fuenteTasa = fuenteTasa;
    }

    public void agregarDetalle(Producto producto, int cantidad) {
        DetalleCompra d = new DetalleCompra(producto, cantidad, tasaUsada);
        detalles.add(d);
        totalUSD = Math.round((totalUSD + d.getSubtotalUSD()) * 100.0) / 100.0;
        totalCOP = Math.round((totalCOP + d.getSubtotalCOP()) * 100.0) / 100.0;
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public LocalDateTime getFecha() { return fecha; }
    public List<DetalleCompra> getDetalles() { return detalles; }
    public double getTotalUSD() { return totalUSD; }
    public double getTotalCOP() { return totalCOP; }
    public double getTasaUsada() { return tasaUsada; }
    public String getFuenteTasa() { return fuenteTasa; }
}
