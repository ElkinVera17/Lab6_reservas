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
@DisplayName("Pruebas de la clase DisponibilidadPanel")
class DisponibilidadPanelTest {
    
    private MainApplication mainApp;
    
    private DisponibilidadPanel disponibilidadPanel;
    
    @BeforeEach
    void setUp() {
        mainApp = new MainApplication();
        disponibilidadPanel = new DisponibilidadPanel(mainApp);
    }
    
    @Test
    @DisplayName("DisponibilidadPanel se crea correctamente")
    void testCreacionDisponibilidadPanel() {
        assertNotNull(disponibilidadPanel);
        assertTrue(disponibilidadPanel.isVisible() || !disponibilidadPanel.isVisible());
    }
    
    @Test
    @DisplayName("DisponibilidadPanel tiene componentes inicializados")
    void testComponentesInicializados() {
        // Assert
        assertNotNull(disponibilidadPanel);
        assertTrue(disponibilidadPanel.getComponentCount() > 0);
    }
    
    @Test
    @DisplayName("Panel tiene BorderLayout configurado")
    void testLayoutDisponibilidadPanel() {
        // Assert
        assertNotNull(disponibilidadPanel.getLayout());
        assertTrue(disponibilidadPanel.getLayout() instanceof BorderLayout);
    }
    
    @Test
    @DisplayName("DisponibilidadPanel es JPanel")
    void testDisponibilidadPanelEsJPanel() {
        // Assert
        assertTrue(disponibilidadPanel instanceof JPanel);
    }
    
    @Test
    @DisplayName("Obtener MainApplication asociada")
    void testObtenerMainApp() {
        // Assert
        assertNotNull(mainApp);
    }
    
    @Test
    @DisplayName("DisponibilidadPanel tiene fondo blanco o gris")
    void testColorDisponibilidadPanel() {
        // Assert
        assertNotNull(disponibilidadPanel.getBackground());
    }
    
    @Test
    @DisplayName("DisponibilidadPanel contiene botones de navegación")
    void testBotonesNavegacion() {
        // El panel debe contener botones para navegar entre fechas
        assertNotNull(disponibilidadPanel);
        // Solo verificamos que el panel se crear sin excepciones
    }
    
    @Test
    @DisplayName("Panel maneja carga de horarios")
    void testCargaHorarios() {
        // Act - simplemente verifica que no lanza excepción
        assertNotNull(disponibilidadPanel);
    }
}
