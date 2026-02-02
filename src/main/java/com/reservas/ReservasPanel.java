package com.reservas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.reservas.dao.ReservaDAO;
import com.reservas.models.Reserva;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import com.reservas.db.MongoDBConnection;

public class ReservasPanel extends JPanel {
    private MainApplication mainApp;
    private JTable tablaReservas;
    private DefaultTableModel tableModel;
    private ReservaDAO reservaDAO;
    private JComboBox<String> cmbFiltroEstado;
    private JTextField txtFiltroFecha;
    private JButton btnFiltrar, btnLimpiarFiltro;
    
    public ReservasPanel(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.reservaDAO = new ReservaDAO();
        
        setLayout(new BorderLayout());
        initUI();
        cargarTodasLasReservas();
    }
    
    private void initUI() {
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel lblTitulo = new JLabel("Administración de Reservas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(lblTitulo, BorderLayout.WEST);
        
        // Botón para volver
        JButton btnVolver = new JButton("Volver a Disponibilidad");
        btnVolver.setBackground(new Color(70, 130, 180));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showPanel("disponibilidad");
            }
        });
        topPanel.add(btnVolver, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Panel de filtros
        JPanel filtrosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtrosPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        filtrosPanel.setBackground(Color.WHITE);
        
        filtrosPanel.add(new JLabel("Filtrar por:"));
        
        // Filtro por estado
        filtrosPanel.add(new JLabel("Estado:"));
        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Disponible", "Ocupado"});
        filtrosPanel.add(cmbFiltroEstado);
        
        // Filtro por fecha
        filtrosPanel.add(new JLabel("Fecha (YYYY-MM-DD):"));
        txtFiltroFecha = new JTextField(10);
        filtrosPanel.add(txtFiltroFecha);
        
        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setBackground(new Color(50, 205, 50));
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        filtrosPanel.add(btnFiltrar);
        
        btnLimpiarFiltro = new JButton("Limpiar Filtros");
        btnLimpiarFiltro.setBackground(new Color(220, 20, 60));
        btnLimpiarFiltro.setForeground(Color.WHITE);
        btnLimpiarFiltro.addActionListener(e -> limpiarFiltros());
        filtrosPanel.add(btnLimpiarFiltro);
        
        add(filtrosPanel, BorderLayout.CENTER);
        
        // Panel central con tabla
        String[] columnas = {"ID", "Fecha", "Hora", "Estado", "Cliente", "Email", "Acciones"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo la columna de acciones es editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 6 ? JButton.class : String.class;
            }
        };
        
        tablaReservas = new JTable(tableModel);
        tablaReservas.setRowHeight(40);
        tablaReservas.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        tablaReservas.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(tablaReservas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(240, 240, 240));
        
        JButton btnNueva = new JButton("Nueva Reserva");
        btnNueva.setBackground(new Color(30, 144, 255));
        btnNueva.setForeground(Color.WHITE);
        btnNueva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearNuevaReserva();
            }
        });
        
        JButton btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.setBackground(new Color(255, 165, 0));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarTodasLasReservas();
            }
        });
        
        JButton btnExportar = new JButton("Exportar a CSV");
        btnExportar.setBackground(new Color(60, 179, 113));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.addActionListener(e -> exportarCSV());
        
        bottomPanel.add(btnNueva);
        bottomPanel.add(btnActualizar);
        bottomPanel.add(btnExportar);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
 // Cambia de private a public
    public void cargarTodasLasReservas() {
        // Limpiar tabla
        tableModel.setRowCount(0);
        
        try {
            // Obtener todas las reservas de la colección
            MongoCollection<Document> collection = MongoDBConnection.getCollection("reservas");
            
            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    
                    String id = doc.getObjectId("_id").toString();
                    String fecha = doc.getString("fecha");
                    String hora = doc.getString("hora");
                    String estado = doc.getString("estado");
                    String clienteNombre = doc.getString("clienteNombre");
                    String clienteEmail = doc.getString("clienteEmail");
                    
                    if (clienteNombre == null) clienteNombre = "";
                    if (clienteEmail == null) clienteEmail = "";
                    
                    Object[] fila = {
                        id,
                        fecha,
                        hora,
                        estado,
                        clienteNombre,
                        clienteEmail,
                        "Editar/Eliminar"
                    };
                    tableModel.addRow(fila);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar reservas: " + e.getMessage());
        }
    }
    
    private void aplicarFiltros() {
        String estado = (String) cmbFiltroEstado.getSelectedItem();
        String fecha = txtFiltroFecha.getText().trim();
        
        // Limpiar tabla
        tableModel.setRowCount(0);
        
        try {
            MongoCollection<Document> collection = MongoDBConnection.getCollection("reservas");
            Document filtro = new Document();
            
            if (!estado.equals("Todos")) {
                filtro.append("estado", estado);
            }
            
            if (!fecha.isEmpty()) {
                filtro.append("fecha", fecha);
            }
            
            try (MongoCursor<Document> cursor = collection.find(filtro).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    
                    String id = doc.getObjectId("_id").toString();
                    String fechaReserva = doc.getString("fecha");
                    String hora = doc.getString("hora");
                    String estadoReserva = doc.getString("estado");
                    String clienteNombre = doc.getString("clienteNombre");
                    String clienteEmail = doc.getString("clienteEmail");
                    
                    if (clienteNombre == null) clienteNombre = "";
                    if (clienteEmail == null) clienteEmail = "";
                    
                    Object[] fila = {
                        id,
                        fechaReserva,
                        hora,
                        estadoReserva,
                        clienteNombre,
                        clienteEmail,
                        "Editar/Eliminar"
                    };
                    tableModel.addRow(fila);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar: " + e.getMessage());
        }
    }
    
    private void limpiarFiltros() {
        cmbFiltroEstado.setSelectedIndex(0);
        txtFiltroFecha.setText("");
        cargarTodasLasReservas();
    }
    
    private void crearNuevaReserva() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva Reserva", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Fecha actual por defecto
        LocalDate hoy = LocalDate.now();
        
        JTextField txtFecha = new JTextField(hoy.toString());
        JTextField txtHora = new JTextField("09:00");
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Disponible", "Ocupado"});
        JTextField txtNombre = new JTextField();
        JTextField txtEmail = new JTextField();
        
        formPanel.add(new JLabel("Fecha (YYYY-MM-DD):"));
        formPanel.add(txtFecha);
        formPanel.add(new JLabel("Hora (HH:MM):"));
        formPanel.add(txtHora);
        formPanel.add(new JLabel("Estado:"));
        formPanel.add(cmbEstado);
        formPanel.add(new JLabel("Nombre Cliente:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Email Cliente:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = new JButton("Guardar Reserva");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.setBackground(new Color(50, 205, 50));
        btnGuardar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(220, 20, 60));
        btnCancelar.setForeground(Color.WHITE);
        
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (txtFecha.getText().trim().isEmpty() || txtHora.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Fecha y hora son obligatorios");
                        return;
                    }
                    
                    Reserva reserva = new Reserva();
                    reserva.setFecha(LocalDate.parse(txtFecha.getText()));
                    reserva.setHora(java.time.LocalTime.parse(txtHora.getText()));
                    reserva.setEstado((String) cmbEstado.getSelectedItem());
                    
                    if (reserva.getEstado().equals("Ocupado")) {
                        if (txtNombre.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
                            JOptionPane.showMessageDialog(dialog, "Para reservas ocupadas, nombre y email son obligatorios");
                            return;
                        }
                        reserva.setClienteNombre(txtNombre.getText().trim());
                        reserva.setClienteEmail(txtEmail.getText().trim());
                    }
                    
                    reservaDAO.crearReserva(reserva);
                    JOptionPane.showMessageDialog(dialog, "Reserva creada exitosamente!");
                    dialog.dispose();
                    cargarTodasLasReservas();
                    
                    // Si es para hoy, actualizar el panel de disponibilidad
                    if (reserva.getFecha().equals(LocalDate.now())) {
                        mainApp.showPanel("disponibilidad");
                        ((DisponibilidadPanel) mainApp.getContentPane().getComponent(0)).actualizarFecha(reserva.getFecha());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void editarReserva(String id) {
        try {
            MongoCollection<Document> collection = MongoDBConnection.getCollection("reservas");
            Document doc = collection.find(new Document("_id", new org.bson.types.ObjectId(id))).first();
            
            if (doc != null) {
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Reserva", true);
                dialog.setLayout(new BorderLayout());
                dialog.setSize(450, 350);
                dialog.setLocationRelativeTo(this);
                
                JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
                formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                JTextField txtFecha = new JTextField(doc.getString("fecha"));
                JTextField txtHora = new JTextField(doc.getString("hora"));
                JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Disponible", "Ocupado"});
                cmbEstado.setSelectedItem(doc.getString("estado"));
                
                String clienteNombre = doc.getString("clienteNombre");
                String clienteEmail = doc.getString("clienteEmail");
                
                JTextField txtNombre = new JTextField(clienteNombre != null ? clienteNombre : "");
                JTextField txtEmail = new JTextField(clienteEmail != null ? clienteEmail : "");
                
                formPanel.add(new JLabel("Fecha (YYYY-MM-DD):"));
                formPanel.add(txtFecha);
                formPanel.add(new JLabel("Hora (HH:MM):"));
                formPanel.add(txtHora);
                formPanel.add(new JLabel("Estado:"));
                formPanel.add(cmbEstado);
                formPanel.add(new JLabel("Nombre Cliente:"));
                formPanel.add(txtNombre);
                formPanel.add(new JLabel("Email Cliente:"));
                formPanel.add(txtEmail);
                formPanel.add(new JLabel("ID:"));
                formPanel.add(new JLabel(id));
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                JButton btnGuardar = new JButton("Guardar Cambios");
                JButton btnEliminar = new JButton("Eliminar");
                JButton btnCancelar = new JButton("Cancelar");
                
                btnGuardar.setBackground(new Color(50, 205, 50));
                btnGuardar.setForeground(Color.WHITE);
                btnEliminar.setBackground(new Color(220, 20, 60));
                btnEliminar.setForeground(Color.WHITE);
                btnCancelar.setBackground(new Color(169, 169, 169));
                btnCancelar.setForeground(Color.WHITE);
                
                btnGuardar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Document update = new Document();
                            update.append("fecha", txtFecha.getText());
                            update.append("hora", txtHora.getText());
                            update.append("estado", cmbEstado.getSelectedItem());
                            update.append("clienteNombre", txtNombre.getText().trim());
                            update.append("clienteEmail", txtEmail.getText().trim());
                            
                            collection.updateOne(
                                new Document("_id", new org.bson.types.ObjectId(id)),
                                new Document("$set", update)
                            );
                            
                            JOptionPane.showMessageDialog(dialog, "Reserva actualizada exitosamente!");
                            dialog.dispose();
                            cargarTodasLasReservas();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                        }
                    }
                });
                
                btnEliminar.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(dialog, 
                        "¿Está seguro de eliminar esta reserva?", "Confirmar", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        collection.deleteOne(new Document("_id", new org.bson.types.ObjectId(id)));
                        JOptionPane.showMessageDialog(dialog, "Reserva eliminada exitosamente!");
                        dialog.dispose();
                        cargarTodasLasReservas();
                    }
                });
                
                btnCancelar.addActionListener(e -> dialog.dispose());
                
                buttonPanel.add(btnGuardar);
                buttonPanel.add(btnEliminar);
                buttonPanel.add(btnCancelar);
                
                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar reserva: " + e.getMessage());
        }
    }
    
    private void exportarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar CSV");
        fileChooser.setSelectedFile(new java.io.File("reservas.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                // Escribir encabezados
                writer.write("ID,Fecha,Hora,Estado,Cliente,Email\n");
                
                // Escribir datos
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        tableModel.getValueAt(i, 0),
                        tableModel.getValueAt(i, 1),
                        tableModel.getValueAt(i, 2),
                        tableModel.getValueAt(i, 3),
                        tableModel.getValueAt(i, 4),
                        tableModel.getValueAt(i, 5)
                    ));
                }
                
                JOptionPane.showMessageDialog(this, "Datos exportados exitosamente a: " + 
                    fileChooser.getSelectedFile().getPath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage());
            }
        }
    }
    
    // Clases internas para botones en la tabla
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    String id = (String) tableModel.getValueAt(currentRow, 0);
                    editarReserva(id);
                }
            });
        }
        
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }
        
        public Object getCellEditorValue() {
            if (isPushed) {
                // Acción ya realizada en el ActionListener
            }
            isPushed = false;
            return label;
        }
        
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}