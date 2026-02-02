package com.reservas.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalTime;

@DisplayName("Pruebas de la clase Reserva")
class ReservaTest {
    
    private Reserva reserva;
    
    @BeforeEach
    void setUp() {
        reserva = new Reserva();
    }
    
    @Test
    @DisplayName("Crear Reserva con constructor default")
    void testCreacionReservaDefault() {
        // Arrange & Act
        Reserva nuevaReserva = new Reserva();
        
        // Assert
        assertNotNull(nuevaReserva);
        assertNull(nuevaReserva.getId());
        assertNull(nuevaReserva.getFecha());
        assertNull(nuevaReserva.getHora());
        assertNull(nuevaReserva.getEstado());
        assertNull(nuevaReserva.getClienteNombre());
        assertNull(nuevaReserva.getClienteEmail());
    }
    
    @Test
    @DisplayName("Crear Reserva con constructor parametrizado")
    void testCreacionReservaConParametros() {
        // Arrange
        LocalDate fecha = LocalDate.of(2024, 1, 16);
        LocalTime hora = LocalTime.of(9, 0);
        String estado = "Disponible";
        
        // Act
        Reserva nuevaReserva = new Reserva(fecha, hora, estado);
        
        // Assert
        assertNotNull(nuevaReserva);
        assertEquals(fecha, nuevaReserva.getFecha());
        assertEquals(hora, nuevaReserva.getHora());
        assertEquals(estado, nuevaReserva.getEstado());
        assertNull(nuevaReserva.getId());
        assertNull(nuevaReserva.getClienteNombre());
        assertNull(nuevaReserva.getClienteEmail());
    }
    
    @Test
    @DisplayName("Establecer y obtener ID")
    void testSetterGetterID() {
        // Arrange
        String id = "507f1f77bcf86cd799439011";
        
        // Act
        reserva.setId(id);
        String resultado = reserva.getId();
        
        // Assert
        assertEquals(id, resultado);
    }
    
    @Test
    @DisplayName("Establecer y obtener Fecha")
    void testSetterGetterFecha() {
        // Arrange
        LocalDate fecha = LocalDate.of(2024, 2, 15);
        
        // Act
        reserva.setFecha(fecha);
        LocalDate resultado = reserva.getFecha();
        
        // Assert
        assertEquals(fecha, resultado);
    }
    
    @Test
    @DisplayName("Establecer y obtener Hora")
    void testSetterGetterHora() {
        // Arrange
        LocalTime hora = LocalTime.of(14, 30);
        
        // Act
        reserva.setHora(hora);
        LocalTime resultado = reserva.getHora();
        
        // Assert
        assertEquals(hora, resultado);
    }
    
    @Test
    @DisplayName("Establecer y obtener Estado")
    void testSetterGetterEstado() {
        // Arrange
        String estado = "Ocupado";
        
        // Act
        reserva.setEstado(estado);
        String resultado = reserva.getEstado();
        
        // Assert
        assertEquals(estado, resultado);
    }
    
    @Test
    @DisplayName("Establecer y obtener Nombre Cliente")
    void testSetterGetterClienteNombre() {
        // Arrange
        String nombre = "Juan García López";
        
        // Act
        reserva.setClienteNombre(nombre);
        String resultado = reserva.getClienteNombre();
        
        // Assert
        assertEquals(nombre, resultado);
    }
    
    @Test
    @DisplayName("Establecer y obtener Email Cliente")
    void testSetterGetterClienteEmail() {
        // Arrange
        String email = "juan.garcia@example.com";
        
        // Act
        reserva.setClienteEmail(email);
        String resultado = reserva.getClienteEmail();
        
        // Assert
        assertEquals(email, resultado);
    }
    
    @Test
    @DisplayName("Establecer todos los atributos")
    void testSettersYGetters() {
        // Arrange
        String id = "507f1f77bcf86cd799439011";
        LocalDate fecha = LocalDate.of(2024, 1, 17);
        LocalTime hora = LocalTime.of(10, 30);
        String estado = "Ocupado";
        String nombre = "Juan Pérez";
        String email = "juan@email.com";
        
        // Act
        reserva.setId(id);
        reserva.setFecha(fecha);
        reserva.setHora(hora);
        reserva.setEstado(estado);
        reserva.setClienteNombre(nombre);
        reserva.setClienteEmail(email);
        
        // Assert
        assertEquals(id, reserva.getId());
        assertEquals(fecha, reserva.getFecha());
        assertEquals(hora, reserva.getHora());
        assertEquals(estado, reserva.getEstado());
        assertEquals(nombre, reserva.getClienteNombre());
        assertEquals(email, reserva.getClienteEmail());
    }
    
    @Test
    @DisplayName("toString con hora disponible")
    void testToStringDisponible() {
        // Arrange
        reserva.setFecha(LocalDate.of(2024, 1, 16));
        reserva.setHora(LocalTime.of(9, 0));
        reserva.setEstado("Disponible");
        
        // Act
        String resultado = reserva.toString();
        
        // Assert
        assertTrue(resultado.contains("09:00"));
        assertTrue(resultado.contains("Disponible"));
    }
    
    @Test
    @DisplayName("toString con hora ocupada")
    void testToStringOcupado() {
        // Arrange
        reserva.setFecha(LocalDate.of(2024, 1, 16));
        reserva.setHora(LocalTime.of(14, 30));
        reserva.setEstado("Ocupado");
        
        // Act
        String resultado = reserva.toString();
        
        // Assert
        assertTrue(resultado.contains("14:30"));
        assertTrue(resultado.contains("Ocupado"));
    }
    
    @Test
    @DisplayName("toString con diferentes horas")
    void testToStringDiferentesHoras() {
        // Arrange
        LocalTime[] horas = {
            LocalTime.of(8, 0),
            LocalTime.of(12, 15),
            LocalTime.of(18, 45),
            LocalTime.of(23, 59)
        };
        
        // Act & Assert
        for (LocalTime hora : horas) {
            reserva.setHora(hora);
            reserva.setEstado("Disponible");
            String resultado = reserva.toString();
            assertTrue(resultado.contains(hora.toString()));
        }
    }
    
    @Test
    @DisplayName("ReservaVacia con valores por defecto")
    void testReservaVacia() {
        // Arrange & Act
        Reserva nuevaReserva = new Reserva();
        
        // Assert
        assertNull(nuevaReserva.getId());
        assertNull(nuevaReserva.getFecha());
        assertNull(nuevaReserva.getHora());
        assertNull(nuevaReserva.getEstado());
        assertNull(nuevaReserva.getClienteNombre());
        assertNull(nuevaReserva.getClienteEmail());
    }
    
    @Test
    @DisplayName("Cambiar estado de Disponible a Ocupado")
    void testCambiarEstado() {
        // Arrange
        reserva.setEstado("Disponible");
        assertEquals("Disponible", reserva.getEstado());
        
        // Act
        reserva.setEstado("Ocupado");
        
        // Assert
        assertEquals("Ocupado", reserva.getEstado());
    }
    
    @Test
    @DisplayName("Cambiar cliente en reserva")
    void testCambiarCliente() {
        // Arrange
        reserva.setClienteNombre("Cliente A");
        reserva.setClienteEmail("clientea@example.com");
        assertEquals("Cliente A", reserva.getClienteNombre());
        
        // Act
        reserva.setClienteNombre("Cliente B");
        reserva.setClienteEmail("clienteb@example.com");
        
        // Assert
        assertEquals("Cliente B", reserva.getClienteNombre());
        assertEquals("clienteb@example.com", reserva.getClienteEmail());
    }
    
    @Test
    @DisplayName("Verificar que reserva con misma hora y fecha son diferentes objetos")
    void testReservasDiferentes() {
        // Arrange
        Reserva reserva1 = new Reserva(LocalDate.of(2024, 1, 16), LocalTime.of(9, 0), "Disponible");
        Reserva reserva2 = new Reserva(LocalDate.of(2024, 1, 16), LocalTime.of(9, 0), "Disponible");
        
        // Act & Assert
        assertNotSame(reserva1, reserva2);
        assertEquals(reserva1.getFecha(), reserva2.getFecha());
        assertEquals(reserva1.getHora(), reserva2.getHora());
    }
    
    @Test
    @DisplayName("Establecer null en cliente")
    void testClienteNull() {
        // Arrange
        reserva.setClienteNombre("Juan");
        reserva.setClienteEmail("juan@example.com");
        
        // Act
        reserva.setClienteNombre(null);
        reserva.setClienteEmail(null);
        
        // Assert
        assertNull(reserva.getClienteNombre());
        assertNull(reserva.getClienteEmail());
    }
}