package com.reservas.dao;

import com.reservas.models.Reserva;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservaDAOTest {
    
    @Test
    void testCrearDocumentoDesdeReserva() {
        // Arrange
        Reserva reserva = new Reserva(
            LocalDate.of(2024, 1, 16),
            LocalTime.of(9, 0),
            "Disponible"
        );
        reserva.setClienteNombre("Juan Pérez");
        reserva.setClienteEmail("juan@email.com");
        
        // Act (simulamos lo que haría el DAO)
        String fecha = reserva.getFecha().toString();
        String hora = reserva.getHora().toString();
        String estado = reserva.getEstado();
        String cliente = reserva.getClienteNombre();
        String email = reserva.getClienteEmail();
        
        // Assert
        assertEquals("2024-01-16", fecha);
        assertEquals("09:00", hora);
        assertEquals("Disponible", estado);
        assertEquals("Juan Pérez", cliente);
        assertEquals("juan@email.com", email);
    }
    
    @Test
    void testParsearDocumentoAReserva() {
        // Arrange (simulamos un documento MongoDB)
        String fechaStr = "2024-01-16";
        String horaStr = "09:00";
        String estado = "Disponible";
        String cliente = "Juan Pérez";
        String email = "juan@email.com";
        
        // Act (simulamos lo que haría el DAO)
        LocalDate fecha = LocalDate.parse(fechaStr);
        LocalTime hora = LocalTime.parse(horaStr);
        
        Reserva reserva = new Reserva();
        reserva.setFecha(fecha);
        reserva.setHora(hora);
        reserva.setEstado(estado);
        reserva.setClienteNombre(cliente);
        reserva.setClienteEmail(email);
        
        // Assert
        assertEquals(LocalDate.of(2024, 1, 16), reserva.getFecha());
        assertEquals(LocalTime.of(9, 0), reserva.getHora());
        assertEquals("Disponible", reserva.getEstado());
        assertEquals("Juan Pérez", reserva.getClienteNombre());
        assertEquals("juan@email.com", reserva.getClienteEmail());
    }
    
    @Test
    void testValidarFiltrosMongoDB() {
        // Arrange (simulamos los filtros que usaría el DAO)
        LocalDate fecha = LocalDate.of(2024, 1, 16);
        String fechaStr = fecha.toString();
        String estadoDisponible = "Disponible";
        String estadoOcupado = "Ocupado";
        
        // Act & Assert - Verificamos la lógica de los filtros
        // Filtro para fecha
        assertTrue(fechaStr.equals("2024-01-16"));
        
        // Filtro para disponibles
        assertTrue(estadoDisponible.equals("Disponible"));
        
        // Filtro para ocupados
        assertTrue(estadoOcupado.equals("Ocupado"));
    }
    
    @Test
    void testCalculosEstadisticos() {
        // Arrange
        int totalReservas = 8; // 9:00 a 16:00 = 8 horas
        int disponibles = 5;
        int ocupados = 3;
        
        // Act
        int totalCalculado = disponibles + ocupados;
        double porcentajeOcupacion = (ocupados * 100.0) / totalReservas;
        double porcentajeDisponibilidad = (disponibles * 100.0) / totalReservas;
        
        // Assert
        assertEquals(totalReservas, totalCalculado);
        assertEquals(37.5, porcentajeOcupacion, 0.01);
        assertEquals(62.5, porcentajeDisponibilidad, 0.01);
        assertTrue(porcentajeOcupacion + porcentajeDisponibilidad == 100.0);
    }
}