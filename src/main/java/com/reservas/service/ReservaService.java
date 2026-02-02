package com.reservas.service;

import com.reservas.dao.ReservaDAO;
import com.reservas.models.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaService {
    private final ReservaDAO reservaDAO;
    
    public ReservaService(ReservaDAO reservaDAO) {
        this.reservaDAO = reservaDAO;
    }
    
    public boolean reservarHora(LocalDate fecha, LocalTime hora, String cliente, String email) {
        List<Reserva> reservas = reservaDAO.obtenerReservasPorFecha(fecha);
        
        for (Reserva reserva : reservas) {
            if (reserva.getHora().equals(hora)) {
                if (reserva.getEstado().equals("Disponible")) {
                    reserva.setEstado("Ocupado");
                    reserva.setClienteNombre(cliente);
                    reserva.setClienteEmail(email);
                    return reservaDAO.actualizarReserva(reserva);
                }
                return false; // Ya está ocupada
            }
        }
        return false; // No existe la hora
    }
    
    public boolean liberarHora(LocalDate fecha, LocalTime hora) {
        List<Reserva> reservas = reservaDAO.obtenerReservasPorFecha(fecha);
        
        for (Reserva reserva : reservas) {
            if (reserva.getHora().equals(hora) && reserva.getEstado().equals("Ocupado")) {
                reserva.setEstado("Disponible");
                reserva.setClienteNombre(null);
                reserva.setClienteEmail(null);
                return reservaDAO.actualizarReserva(reserva);
            }
        }
        return false;
    }
    
    public List<Reserva> obtenerHorariosDelDia(LocalDate fecha) {
        return reservaDAO.obtenerReservasPorFecha(fecha);
    }
    
    public String obtenerEstadisticas(LocalDate fecha) {
        int disponibles = reservaDAO.contarDisponibles(fecha);
        int ocupados = reservaDAO.contarOcupados(fecha);
        return String.format("Disponibles: %d, Ocupados: %d", disponibles, ocupados);
    }
    
    public double calcularPorcentajeOcupacion(LocalDate fecha) {
        int disponibles = reservaDAO.contarDisponibles(fecha);
        int ocupados = reservaDAO.contarOcupados(fecha);
        int total = disponibles + ocupados;
        
        if (total == 0) {
            return 0.0;
        }
        return (ocupados * 100.0) / total;
    }
    
    public double calcularPorcentajeDisponibilidad(LocalDate fecha) {
        int disponibles = reservaDAO.contarDisponibles(fecha);
        int ocupados = reservaDAO.contarOcupados(fecha);
        int total = disponibles + ocupados;
        
        if (total == 0) {
            return 0.0;
        }
        return (disponibles * 100.0) / total;
    }
}
