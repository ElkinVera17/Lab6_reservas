package com.reservas.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private String id;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado; // "Disponible" o "Ocupado"
    private String clienteNombre;
    private String clienteEmail;
    
    public Reserva() {}
    
    public Reserva(LocalDate fecha, LocalTime hora, String estado) {
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    
    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }
    
    @Override
    public String toString() {
        return hora.toString() + " - " + estado;
    }
}