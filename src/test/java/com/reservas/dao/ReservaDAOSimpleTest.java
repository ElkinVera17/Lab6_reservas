package com.reservas.dao;

import com.reservas.models.Reserva;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas avanzadas para ReservaDAO")
class ReservaDAOSimpleTest {
    
    @Mock
    private MongoCollection<Document> mockCollection;
    
    @Mock
    private FindIterable<Document> mockFindIterable;
    
    private ReservaDAO reservaDAO;
    private Reserva testReserva;
    
    @BeforeEach
    void setUp() {
        reservaDAO = new ReservaDAO(mockCollection);
        
        testReserva = new Reserva(
            LocalDate.of(2024, 2, 15),
            LocalTime.of(14, 30),
            "Disponible"
        );
        testReserva.setClienteNombre("Juan García");
        testReserva.setClienteEmail("juan.garcia@example.com");
        testReserva.setId(new ObjectId().toString());
    }
    
    
    
    @Test
    @DisplayName("Actualizar reserva existente")
    void testActualizarReserva() {
        // Arrange
        testReserva.setEstado("Ocupado");
        
        com.mongodb.client.result.UpdateResult mockUpdateResult = 
            mock(com.mongodb.client.result.UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(1L);
        when(mockCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockUpdateResult);
        
        // Act
        boolean resultado = reservaDAO.actualizarReserva(testReserva);
        
        // Assert
        assertTrue(resultado);
        verify(mockCollection, times(1)).updateOne(any(Bson.class), any(Bson.class));
    }
    
    @Test
    @DisplayName("Fallar al actualizar reserva no encontrada")
    void testActualizarReservaNoEncontrada() {
        // Arrange
        com.mongodb.client.result.UpdateResult mockUpdateResult = 
            mock(com.mongodb.client.result.UpdateResult.class);
        when(mockUpdateResult.getModifiedCount()).thenReturn(0L);
        when(mockCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockUpdateResult);
        
        // Act
        boolean resultado = reservaDAO.actualizarReserva(testReserva);
        
        // Assert
        assertFalse(resultado);
    }
    
    @Test
    @DisplayName("Manejar excepcion al actualizar")
    void testActualizarReservaConExcepcion() {
        // Arrange
        when(mockCollection.updateOne(any(Bson.class), any(Bson.class))).thenThrow(new RuntimeException("Error"));
        
        // Act
        boolean resultado = reservaDAO.actualizarReserva(testReserva);
        
        // Assert
        assertFalse(resultado);
    }
    
    @Test
    @DisplayName("Eliminar reserva existente")
    void testEliminarReserva() {
        // Arrange
        com.mongodb.client.result.DeleteResult mockDeleteResult = 
            mock(com.mongodb.client.result.DeleteResult.class);
        when(mockDeleteResult.getDeletedCount()).thenReturn(1L);
        when(mockCollection.deleteOne(any(Bson.class))).thenReturn(mockDeleteResult);
        
        // Act
        boolean resultado = reservaDAO.eliminarReserva(testReserva.getId());
        
        // Assert
        assertTrue(resultado);
        verify(mockCollection, times(1)).deleteOne(any(Bson.class));
    }
    
   
    
    @Test
    @DisplayName("Manejar excepcion al eliminar")
    void testEliminarReservaConExcepcion() {
        // Arrange
        when(mockCollection.deleteOne(any(Bson.class))).thenThrow(new RuntimeException("Error"));
        
        // Act
        boolean resultado = reservaDAO.eliminarReserva(testReserva.getId());
        
        // Assert
        assertFalse(resultado);
    }
    
    @Test
    @DisplayName("Contar disponibles en fecha")
    void testContarDisponibles() {
        // Arrange
        when(mockCollection.countDocuments(any(Bson.class))).thenReturn(5L);
        
        // Act
        int resultado = reservaDAO.contarDisponibles(LocalDate.of(2024, 2, 15));
        
        // Assert
        assertEquals(5, resultado);
        verify(mockCollection, times(1)).countDocuments(any(Bson.class));
    }
    
    @Test
    @DisplayName("Contar ocupados en fecha")
    void testContarOcupados() {
        // Arrange
        when(mockCollection.countDocuments(any(Bson.class))).thenReturn(3L);
        
        // Act
        int resultado = reservaDAO.contarOcupados(LocalDate.of(2024, 2, 15));
        
        // Assert
        assertEquals(3, resultado);
        verify(mockCollection, times(1)).countDocuments(any(Bson.class));
    }
}
