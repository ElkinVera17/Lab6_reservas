package com.reservas.service;

import com.reservas.dao.ReservaDAO;
import com.reservas.models.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de la clase ReservaService")
class ReservaServiceTest {
    
    @Mock
    private ReservaDAO reservaDAO;
    
    @InjectMocks
    private ReservaService reservaService;
    
    private Reserva reservaDisponible;
    private Reserva reservaOcupada;
    private LocalDate fechaPrueba;
    
    @BeforeEach
    void setUp() {
        reservaService = new ReservaService(reservaDAO);
        
        fechaPrueba = LocalDate.of(2024, 1, 16);
        
        reservaDisponible = new Reserva(
            fechaPrueba,
            LocalTime.of(9, 0),
            "Disponible"
        );
        
        reservaOcupada = new Reserva(
            fechaPrueba,
            LocalTime.of(10, 0),
            "Ocupado"
        );
        reservaOcupada.setClienteNombre("Cliente Prueba");
        reservaOcupada.setClienteEmail("cliente@prueba.com");
    }
    
    @Test
    @DisplayName("Reservar hora disponible exitosamente")
    void testReservarHoraDisponible() {
        // Arrange
        when(reservaDAO.obtenerReservasPorFecha(any(LocalDate.class)))
            .thenReturn(Arrays.asList(reservaDisponible));
        when(reservaDAO.actualizarReserva(any(Reserva.class))).thenReturn(true);
        
        // Act
        boolean resultado = reservaService.reservarHora(
            fechaPrueba,
            LocalTime.of(9, 0),
            "Nuevo Cliente",
            "nuevo@cliente.com"
        );
        
        // Assert
        assertTrue(resultado);
        verify(reservaDAO, times(1)).actualizarReserva(any(Reserva.class));
    }
    
    @Test
    @DisplayName("Fallar al reservar hora ya ocupada")
    void testReservarHoraYaOcupada() {
        // Arrange
        when(reservaDAO.obtenerReservasPorFecha(any(LocalDate.class)))
            .thenReturn(Arrays.asList(reservaOcupada));
        
        // Act
        boolean resultado = reservaService.reservarHora(
            fechaPrueba,
            LocalTime.of(10, 0),
            "Otro Cliente",
            "otro@cliente.com"
        );
        
        // Assert
        assertFalse(resultado);
        verify(reservaDAO, never()).actualizarReserva(any(Reserva.class));
    }
    
    @Test
    @DisplayName("Reservar hora cuando actualizacion falla")
    void testReservarHoraFallaEnActualizacion() {
        // Arrange
        when(reservaDAO.obtenerReservasPorFecha(any(LocalDate.class)))
            .thenReturn(Arrays.asList(reservaDisponible));
        when(reservaDAO.actualizarReserva(any(Reserva.class))).thenReturn(false);
        
        // Act
        boolean resultado = reservaService.reservarHora(
            fechaPrueba,
            LocalTime.of(9, 0),
            "Cliente Fallido",
            "fallido@cliente.com"
        );
        
        // Assert
        assertFalse(resultado);
        verify(reservaDAO, times(1)).actualizarReserva(any(Reserva.class));
    }
    
    @Test
    @DisplayName("Liberar hora ocupada exitosamente")
    void testLiberarHoraOcupada() {
        // Arrange
        when(reservaDAO.obtenerReservasPorFecha(any(LocalDate.class)))
            .thenReturn(Arrays.asList(reservaOcupada));
        when(reservaDAO.actualizarReserva(any(Reserva.class))).thenReturn(true);
        
        // Act
        boolean resultado = reservaService.liberarHora(
            fechaPrueba,
            LocalTime.of(10, 0)
        );
        
        // Assert
        assertTrue(resultado);
        verify(reservaDAO, times(1)).actualizarReserva(any(Reserva.class));
    }
    
    @Test
    @DisplayName("Fallar al liberar hora disponible")
    void testLiberarHoraDisponible() {
        // Arrange
        when(reservaDAO.obtenerReservasPorFecha(any(LocalDate.class)))
            .thenReturn(Arrays.asList(reservaDisponible));
        
        // Act
        boolean resultado = reservaService.liberarHora(
            fechaPrueba,
            LocalTime.of(9, 0)
        );
        
        // Assert
        assertFalse(resultado);
        verify(reservaDAO, never()).actualizarReserva(any(Reserva.class));
    }
    
    @Test
    @DisplayName("Fallar al liberar hora inexistente")
    void testLiberarHoraInexistente() {
        // Arrange
        when(reservaDAO.obtenerReservasPorFecha(any(LocalDate.class)))
            .thenReturn(Arrays.asList(reservaOcupada));
        
        // Act
        boolean resultado = reservaService.liberarHora(
            fechaPrueba,
            LocalTime.of(11, 0) // Hora diferente
        );
        
        // Assert
        assertFalse(resultado);
        verify(reservaDAO, never()).actualizarReserva(any(Reserva.class));
    }
    
    @Test
    @DisplayName("Obtener horarios del día")
    void testObtenerHorariosDelDia() {
        // Arrange
        List<Reserva> reservas = Arrays.asList(reservaDisponible, reservaOcupada);
        when(reservaDAO.obtenerReservasPorFecha(any(LocalDate.class)))
            .thenReturn(reservas);
        
        // Act
        List<Reserva> resultado = reservaService.obtenerHorariosDelDia(fechaPrueba);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Disponible", resultado.get(0).getEstado());
        assertEquals("Ocupado", resultado.get(1).getEstado());
    }
    
    @Test
    @DisplayName("Obtener horarios del día vacío")
    void testObtenerHorariosDelDiaVacio() {
        // Arrange
        when(reservaDAO.obtenerReservasPorFecha(any(LocalDate.class)))
            .thenReturn(new ArrayList<>());
        
        // Act
        List<Reserva> resultado = reservaService.obtenerHorariosDelDia(fechaPrueba);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
    
    @Test
    @DisplayName("Obtener estadísticas del día")
    void testObtenerEstadisticas() {
        // Arrange
        when(reservaDAO.contarDisponibles(any(LocalDate.class))).thenReturn(5);
        when(reservaDAO.contarOcupados(any(LocalDate.class))).thenReturn(3);
        
        // Act
        String estadisticas = reservaService.obtenerEstadisticas(fechaPrueba);
        
        // Assert
        assertNotNull(estadisticas);
        assertTrue(estadisticas.contains("5"));
        assertTrue(estadisticas.contains("3"));
        assertEquals("Disponibles: 5, Ocupados: 3", estadisticas);
    }
    
    @Test
    @DisplayName("Obtener estadísticas con cero reservas")
    void testObtenerEstadisticasCero() {
        // Arrange
        when(reservaDAO.contarDisponibles(any(LocalDate.class))).thenReturn(0);
        when(reservaDAO.contarOcupados(any(LocalDate.class))).thenReturn(0);
        
        // Act
        String estadisticas = reservaService.obtenerEstadisticas(fechaPrueba);
        
        // Assert
        assertNotNull(estadisticas);
        assertEquals("Disponibles: 0, Ocupados: 0", estadisticas);
    }
    
    @Test
    @DisplayName("Calcular porcentaje de ocupación")
    void testCalcularPorcentajeOcupacion() {
        // Arrange
        when(reservaDAO.contarDisponibles(any(LocalDate.class))).thenReturn(5);
        when(reservaDAO.contarOcupados(any(LocalDate.class))).thenReturn(5);
        
        // Act
        double porcentaje = reservaService.calcularPorcentajeOcupacion(fechaPrueba);
        
        // Assert
        assertEquals(50.0, porcentaje, 0.01);
    }
    
    @Test
    @DisplayName("Calcular porcentaje de ocupación con sin reservas")
    void testCalcularPorcentajeOcupacionCero() {
        // Arrange
        when(reservaDAO.contarDisponibles(any(LocalDate.class))).thenReturn(0);
        when(reservaDAO.contarOcupados(any(LocalDate.class))).thenReturn(0);
        
        // Act
        double porcentaje = reservaService.calcularPorcentajeOcupacion(fechaPrueba);
        
        // Assert
        assertEquals(0.0, porcentaje);
    }
    
    @Test
    @DisplayName("Calcular porcentaje de ocupación 100%")
    void testCalcularPorcentajeOcupacion100() {
        // Arrange
        when(reservaDAO.contarDisponibles(any(LocalDate.class))).thenReturn(0);
        when(reservaDAO.contarOcupados(any(LocalDate.class))).thenReturn(8);
        
        // Act
        double porcentaje = reservaService.calcularPorcentajeOcupacion(fechaPrueba);
        
        // Assert
        assertEquals(100.0, porcentaje, 0.01);
    }
    
    @Test
    @DisplayName("Calcular porcentaje de disponibilidad")
    void testCalcularPorcentajeDisponibilidad() {
        // Arrange
        when(reservaDAO.contarDisponibles(any(LocalDate.class))).thenReturn(6);
        when(reservaDAO.contarOcupados(any(LocalDate.class))).thenReturn(2);
        
        // Act
        double porcentaje = reservaService.calcularPorcentajeDisponibilidad(fechaPrueba);
        
        // Assert
        assertEquals(75.0, porcentaje, 0.01);
    }
    
    @Test
    @DisplayName("Calcular porcentaje de disponibilidad 0%")
    void testCalcularPorcentajeDisponibilidad0() {
        // Arrange
        when(reservaDAO.contarDisponibles(any(LocalDate.class))).thenReturn(0);
        when(reservaDAO.contarOcupados(any(LocalDate.class))).thenReturn(8);
        
        // Act
        double porcentaje = reservaService.calcularPorcentajeDisponibilidad(fechaPrueba);
        
        // Assert
        assertEquals(0.0, porcentaje, 0.01);
    }
    
    @Test
    @DisplayName("Calcular porcentajes suma 100%")
    void testCalcularPorcentajesSuma100() {
        // Arrange
        when(reservaDAO.contarDisponibles(any(LocalDate.class))).thenReturn(3);
        when(reservaDAO.contarOcupados(any(LocalDate.class))).thenReturn(7);
        
        // Act
        double ocupacion = reservaService.calcularPorcentajeOcupacion(fechaPrueba);
        double disponibilidad = reservaService.calcularPorcentajeDisponibilidad(fechaPrueba);
        
        // Assert
        assertEquals(100.0, ocupacion + disponibilidad, 0.01);
    }
}
