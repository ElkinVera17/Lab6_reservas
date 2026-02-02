package com.reservas.dao;

import com.reservas.models.Reserva;
import com.reservas.db.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {
    private MongoCollection<Document> collection;
    
    // Constructor para inyección en tests
    public ReservaDAO(MongoCollection<Document> collection) {
        this.collection = collection;
    }
    
    public ReservaDAO() {
        collection = MongoDBConnection.getCollection("reservas");
    }
    
    // CREATE
    public String crearReserva(Reserva reserva) {
        Document doc = new Document()
                .append("fecha", reserva.getFecha().toString())
                .append("hora", reserva.getHora().toString())
                .append("estado", reserva.getEstado())
                .append("clienteNombre", reserva.getClienteNombre())
                .append("clienteEmail", reserva.getClienteEmail());
        
        collection.insertOne(doc);
        return doc.getObjectId("_id").toString();
    }
    
    // READ
    public List<Reserva> obtenerReservasPorFecha(LocalDate fecha) {
        List<Reserva> reservas = new ArrayList<>();
        
        for (Document doc : collection.find(Filters.eq("fecha", fecha.toString()))) {
            Reserva reserva = new Reserva();
            reserva.setId(doc.getObjectId("_id").toString());
            reserva.setFecha(LocalDate.parse(doc.getString("fecha")));
            reserva.setHora(LocalTime.parse(doc.getString("hora")));
            reserva.setEstado(doc.getString("estado"));
            reserva.setClienteNombre(doc.getString("clienteNombre"));
            reserva.setClienteEmail(doc.getString("clienteEmail"));
            reservas.add(reserva);
        }
        
        return reservas;
    }
    
    // UPDATE
    public boolean actualizarReserva(Reserva reserva) {
        try {
            UpdateResult result = collection.updateOne(
                Filters.eq("_id", new ObjectId(reserva.getId())),
                Updates.combine(
                    Updates.set("estado", reserva.getEstado()),
                    Updates.set("clienteNombre", reserva.getClienteNombre()),
                    Updates.set("clienteEmail", reserva.getClienteEmail())
                )
            );
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // DELETE
    public boolean eliminarReserva(String id) {
        try {
            DeleteResult result = collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Obtener estadísticas del día
    public int contarDisponibles(LocalDate fecha) {
        long count = collection.countDocuments(
            Filters.and(
                Filters.eq("fecha", fecha.toString()),
                Filters.eq("estado", "Disponible")
            )
        );
        return (int) count;
    }
    
    public int contarOcupados(LocalDate fecha) {
        long count = collection.countDocuments(
            Filters.and(
                Filters.eq("fecha", fecha.toString()),
                Filters.eq("estado", "Ocupado")
            )
        );
        return (int) count;
    }
}