package model;

import exception.TiendaException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import util.Fechas;

public class Torneo {
    private String id;
    private String nombre;
    private LocalDate fecha;
    private int maxParticipantes;
    private List<String> participantesIds = new ArrayList<>();

    public Torneo(String id, String nombre, LocalDate fecha, int maxParticipantes) {
        if (nombre == null || nombre.isBlank())
            throw TiendaException.datosInvalidos("El nombre del torneo es obligatorio");
        if (fecha == null)
            throw TiendaException.datosInvalidos("La fecha del torneo es obligatoria (formato yyyy-MM-dd)");
        if (fecha.isBefore(Fechas.hoy()))
            throw TiendaException.datosInvalidos("No se puede crear un torneo con fecha pasada");
        if (maxParticipantes <= 0)
            throw TiendaException.datosInvalidos("El máximo de participantes debe ser mayor que 0");
        this.id = id;
        this.nombre = nombre.trim();
        this.fecha = fecha;
        this.maxParticipantes = maxParticipantes;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDate getFecha() { return fecha; }
    public int getMaxParticipantes() { return maxParticipantes; }
    public List<String> getParticipantesIds() { return participantesIds; }

    public boolean estaVigente() { return !fecha.isBefore(Fechas.hoy()); }

    public boolean tieneCupo() { return participantesIds.size() < maxParticipantes; }

    public boolean estaInscrito(String clienteId) { return participantesIds.contains(clienteId); }

    public int cuposDisponibles() { return maxParticipantes - participantesIds.size(); }

    public void inscribir(String clienteId) {
        if (!estaVigente())
            throw TiendaException.yaOcurrio("El torneo " + nombre + " ya se realizó (" + fecha + ")");
        if (estaInscrito(clienteId))
            throw TiendaException.conflicto("El cliente ya está inscrito en este torneo");
        if (!tieneCupo())
            throw TiendaException.conflicto("El torneo no tiene cupos disponibles");
        participantesIds.add(clienteId);
    }
}
