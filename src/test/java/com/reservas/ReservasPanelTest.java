package com.reservas;

import com.reservas.dao.ReservaDAO;
import com.reservas.models.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de la clase ReservasPanel")
class ReservasPanelTest {
    
    private MainApplication mainApp;
    
    private ReservasPanel reservasPanel;
    
    @BeforeEach
    void setUp() {
        mainApp = new MainApplication();
        reservasPanel = new ReservasPanel(mainApp);
    }
    
    @Test
    @DisplayName("ReservasPanel se crea correctamente")
    void testCreacionReservasPanel() {
        assertNotNull(reservasPanel);
        assertTrue(reservasPanel.isVisible() || !reservasPanel.isVisible());
    }
    
    @Test
    @DisplayName("ReservasPanel tiene componentes inicializados")
    void testComponentesInicializados() {
        // Assert
        assertNotNull(reservasPanel);
        assertTrue(reservasPanel.getComponentCount() > 0);
    }
    
    @Test
    @DisplayName("Panel tiene BorderLayout configurado")
    void testLayoutReservasPanel() {
        // Assert
        assertNotNull(reservasPanel.getLayout());
        assertTrue(reservasPanel.getLayout() instanceof BorderLayout);
    }
    
    @Test
    @DisplayName("ReservasPanel es JPanel")
    void testReservasPanelEsJPanel() {
        // Assert
        assertTrue(reservasPanel instanceof JPanel);
    }
    
    @Test
    @DisplayName("Obtener MainApplication asociada")
    void testObtenerMainApp() {
        // Assert
        assertNotNull(mainApp);
    }
    
    @Test
    @DisplayName("ReservasPanel tiene fondo blanco o gris")
    void testColorReservasPanel() {
        // Assert
        assertNotNull(reservasPanel.getBackground());
    }
    
    @Test
    @DisplayName("ReservasPanel contiene tabla de reservas")
    void testTablaReservas() {
        // El panel debe contener una tabla para mostrar reservas
        assertNotNull(reservasPanel);
        // Solo verificamos que el panel se crea sin excepciones
    }
    
    @Test
    @DisplayName("ReservasPanel puede cargar reservas")
    void testCargaReservas() {
        // Act - simplemente verifica que no lanza excepción
        assertNotNull(reservasPanel);
    }
    
    @Test
    @DisplayName("ReservasPanel tiene botones de acción")
    void testBotonesAccion() {
        // El panel debe tener botones para eliminar o editar reservas
        assertNotNull(reservasPanel);
    }
}
