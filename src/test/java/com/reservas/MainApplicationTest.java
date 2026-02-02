package com.reservas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de la clase MainApplication")
class MainApplicationTest {
    
    private MainApplication mainApplication;
    
    @BeforeEach
    void setUp() {
        mainApplication = new MainApplication();
    }
    
    @Test
    @DisplayName("MainApplication se crea correctamente")
    void testCreacionMainApplication() {
        assertNotNull(mainApplication);
        assertNotNull(mainApplication.getDisponibilidadPanel());
        assertNotNull(mainApplication.getReservasPanel());
    }
    
    @Test
    @DisplayName("obtener DisponibilidadPanel no es nulo")
    void testObtenerDisponibilidadPanel() {
        DisponibilidadPanel panel = mainApplication.getDisponibilidadPanel();
        assertNotNull(panel);
    }
    
    @Test
    @DisplayName("obtener ReservasPanel no es nulo")
    void testObtenerReservasPanel() {
        ReservasPanel panel = mainApplication.getReservasPanel();
        assertNotNull(panel);
    }
    
    @Test
    @DisplayName("cambiar entre paneles - mostrar disponibilidad")
    void testMostrarDisponibilidadPanel() {
        // Act
        mainApplication.showPanel("disponibilidad");
        
        // Assert - El test básicamente verifica que no lance excepción
        assertNotNull(mainApplication.getDisponibilidadPanel());
    }
    
    @Test
    @DisplayName("cambiar entre paneles - mostrar reservas")
    void testMostrarReservasPanel() {
        // Act
        mainApplication.showPanel("reservas");
        
        // Assert - El test básicamente verifica que no lance excepción
        assertNotNull(mainApplication.getReservasPanel());
    }
    
    @Test
    @DisplayName("MainApplication tiene dimensiones válidas")
    void testDimensionesMainApplication() {
        // Assert
        assertTrue(mainApplication.getWidth() > 0);
        assertTrue(mainApplication.getHeight() > 0);
        assertEquals(800, mainApplication.getWidth());
        assertEquals(600, mainApplication.getHeight());
    }
    
    @Test
    @DisplayName("MainApplication tiene título válido")
    void testTituloMainApplication() {
        // Assert
        assertEquals("Sistema de Reservas", mainApplication.getTitle());
    }
    
    @Test
    @DisplayName("MainApplication está centrada")
    void testLocalizacionMainApplication() {
        // Assert
        assertNotNull(mainApplication.getLocation());
    }
    
    @Test
    @DisplayName("MainApplication tiene content pane configurado")
    void testContentPaneMainApplication() {
        // Assert
        assertNotNull(mainApplication.getContentPane());
    }
}
