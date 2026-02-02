package com.reservas;

import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DisponibilidadPanel disponibilidadPanel;
    private ReservasPanel reservasPanel;
    
    public MainApplication() {
        setTitle("Sistema de Reservas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Inicializar CardLayout y panel principal
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Crear los diferentes paneles
        disponibilidadPanel = new DisponibilidadPanel(this);
        reservasPanel = new ReservasPanel(this);
        
        // Agregar paneles al mainPanel con nombres identificadores
        mainPanel.add(disponibilidadPanel, "disponibilidad");
        mainPanel.add(reservasPanel, "reservas");
        
        // CORRECCIÓN: Establecer mainPanel como content pane del JFrame
        setContentPane(mainPanel);
        
        // Mostrar el panel de disponibilidad primero
        showPanel("disponibilidad");
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        
        // Actualizar datos si es necesario
        if (panelName.equals("reservas")) {
            reservasPanel.cargarTodasLasReservas();
        }
    }
    
    public DisponibilidadPanel getDisponibilidadPanel() {
        return disponibilidadPanel;
    }
    
    public ReservasPanel getReservasPanel() {
        return reservasPanel;
    }
    
    public static void main(String[] args) {
        // Conectar a MongoDB al inicio
        com.reservas.db.MongoDBConnection.connect();
        
        SwingUtilities.invokeLater(() -> {
            MainApplication app = new MainApplication();
            app.setVisible(true);
        });
        
        // Cerrar conexión al salir
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            com.reservas.db.MongoDBConnection.close();
        }));
    }
}