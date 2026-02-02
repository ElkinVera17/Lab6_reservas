package com.reservas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.util.List;
import com.reservas.dao.ReservaDAO;
import com.reservas.models.Reserva;

public class DisponibilidadPanel extends JPanel {
    private MainApplication mainApp;
    private JLabel lblFecha, lblDisponibles, lblOcupados;
    private JPanel horariosPanel;
    private JButton btnAnterior, btnSiguiente, btnHoy;
    private LocalDate fechaActual;
    private ReservaDAO reservaDAO;
    private JScrollPane scrollPane;
    
    public DisponibilidadPanel(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.reservaDAO = new ReservaDAO();
        this.fechaActual = LocalDate.now();
        
        setLayout(new BorderLayout());
        initUI();
        cargarHorarios();
    }
    
    private void initUI() {
        // Panel superior con título (CENTRADO)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel tituloContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JPanel tituloContent = new JPanel();
        tituloContent.setLayout(new BoxLayout(tituloContent, BoxLayout.Y_AXIS));
        tituloContent.setBackground(new Color(240, 240, 240));
        
        JLabel lblTitulo = new JLabel("Disponibilidad de Horarios");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Selecciona un día y un horario disponible para tu reserva");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        tituloContent.add(lblTitulo);
        tituloContent.add(Box.createRigidArea(new Dimension(0, 5)));
        tituloContent.add(lblSubtitulo);
        
        tituloContainer.add(tituloContent);
        topPanel.add(tituloContainer, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Contenedor principal centrado
        JPanel mainContainer = new JPanel(new BorderLayout());
        
        // Panel central con contenido
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        
        // Panel de navegación (centrado)
        JPanel navegacionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navegacionPanel.setBackground(Color.WHITE);
        
        btnAnterior = new JButton("◀ Anterior");
        btnHoy = new JButton("Hoy");
        btnSiguiente = new JButton("Siguiente ▶");
        
        btnAnterior.setBackground(new Color(70, 130, 180));
        btnAnterior.setForeground(Color.WHITE);
        btnHoy.setBackground(new Color(60, 179, 113));
        btnHoy.setForeground(Color.WHITE);
        btnSiguiente.setBackground(new Color(70, 130, 180));
        btnSiguiente.setForeground(Color.WHITE);
        
        btnAnterior.addActionListener(e -> cambiarDia(-1));
        btnHoy.addActionListener(e -> irAHoy());
        btnSiguiente.addActionListener(e -> cambiarDia(1));
        
        navegacionPanel.add(btnAnterior);
        navegacionPanel.add(btnHoy);
        navegacionPanel.add(btnSiguiente);
        
        centerPanel.add(navegacionPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Panel de información de fecha (centrado)
        JPanel infoFechaPanel = new JPanel();
        infoFechaPanel.setLayout(new BoxLayout(infoFechaPanel, BoxLayout.Y_AXIS));
        infoFechaPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        infoFechaPanel.setBackground(new Color(240, 248, 255));
        infoFechaPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblFecha = new JLabel();
        lblFecha.setFont(new Font("Arial", Font.BOLD, 20));
        lblFecha.setForeground(new Color(25, 25, 112));
        lblFecha.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblDisponibles = new JLabel();
        lblDisponibles.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDisponibles.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblOcupados = new JLabel();
        lblOcupados.setFont(new Font("Arial", Font.PLAIN, 14));
        lblOcupados.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoFechaPanel.add(lblFecha);
        infoFechaPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoFechaPanel.add(lblDisponibles);
        infoFechaPanel.add(lblOcupados);
        
        centerPanel.add(infoFechaPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Título "Horarios del día" (centrado)
        JLabel lblHorariosDia = new JLabel("Horarios del día");
        lblHorariosDia.setFont(new Font("Arial", Font.BOLD, 18));
        lblHorariosDia.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblHorariosDia);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Panel para los horarios (centrado)
        horariosPanel = new JPanel();
        horariosPanel.setLayout(new GridLayout(0, 1, 0, 10));
        horariosPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        horariosPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE)); // Ancho máximo
        
        centerPanel.add(horariosPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Panel de botones de acción (centrado)
        JPanel botonesAccionPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        botonesAccionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesAccionPanel.setMaximumSize(new Dimension(600, 100));
        
        JButton btnVerReservas = crearBotonAccion("Ver Todas las Reservas", 
            new Color(30, 144, 255), e -> mainApp.showPanel("reservas"));
        
        JButton btnSeleccionarFecha = crearBotonAccion("Seleccionar Fecha",
            new Color(147, 112, 219), e -> seleccionarFecha());
        
        JButton btnCrearReserva = crearBotonAccion("Crear Reserva",
            new Color(46, 204, 113), e -> crearReservaManual());
        
        JButton btnRecargar = crearBotonAccion("Recargar",
            new Color(52, 152, 219), e -> cargarHorarios());
        
        JButton btnLimpiar = crearBotonAccion("Liberar Ocupados",
            new Color(231, 76, 60), e -> liberarTodosOcupados());
        
        JButton btnCrearDisponibles = crearBotonAccion("Crear Disponibles",
            new Color(155, 89, 182), e -> crearHorariosDisponibles());
        
        botonesAccionPanel.add(btnVerReservas);
        botonesAccionPanel.add(btnSeleccionarFecha);
        botonesAccionPanel.add(btnCrearReserva);
        botonesAccionPanel.add(btnRecargar);
        botonesAccionPanel.add(btnLimpiar);
        botonesAccionPanel.add(btnCrearDisponibles);
        
        centerPanel.add(botonesAccionPanel);
        
        // Agregar espacio flexible en los lados para centrar
        mainContainer.add(Box.createHorizontalGlue(), BorderLayout.WEST);
        mainContainer.add(centerPanel, BorderLayout.CENTER);
        mainContainer.add(Box.createHorizontalGlue(), BorderLayout.EAST);
        
        // Scroll pane
        scrollPane = new JScrollPane(mainContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottomPanel.setBackground(new Color(240, 240, 240));
        
        JLabel lblInfo = new JLabel("Sistema de Reservas v1.0 - © 2024");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        bottomPanel.add(lblInfo);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JButton crearBotonAccion(String texto, Color color, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.addActionListener(listener);
        return boton;
    }
    
    private void actualizarInfoFecha() {
        // Actualizar label de fecha
        String nombreDia = obtenerNombreDia(fechaActual.getDayOfWeek());
        String nombreMes = obtenerNombreMes(fechaActual.getMonthValue());
        lblFecha.setText(nombreDia + " " + fechaActual.getDayOfMonth() + " " + nombreMes + " " + fechaActual.getYear());
        
        // Actualizar contadores
        int disponibles = reservaDAO.contarDisponibles(fechaActual);
        int ocupados = reservaDAO.contarOcupados(fechaActual);
        
        // Actualizar labels de contadores
        lblDisponibles.setText(disponibles + " Disponibles");
        lblOcupados.setText(ocupados + " Ocupados");
        
        // Colores para los contadores
        if (disponibles > 0) {
            lblDisponibles.setForeground(new Color(0, 150, 0));
        } else {
            lblDisponibles.setForeground(Color.RED);
        }
        
        if (ocupados > 0) {
            lblOcupados.setForeground(new Color(200, 0, 0));
        } else {
            lblOcupados.setForeground(Color.DARK_GRAY);
        }
    }
    
    private void cargarHorarios() {
        horariosPanel.removeAll();
        
        // Horarios predefinidos de 9:00 a 16:00
        String[] horas = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
        
        // Cargar reservas de la base de datos para esta fecha
        List<Reserva> reservas = reservaDAO.obtenerReservasPorFecha(fechaActual);
        
        // Si no hay reservas para esta fecha, crear algunas por defecto
        if (reservas.isEmpty()) {
            crearReservasPorDefecto(fechaActual);
            reservas = reservaDAO.obtenerReservasPorFecha(fechaActual);
        }
        
        for (int i = 0; i < horas.length; i++) {
            String hora = horas[i];
            String estado = "Disponible";
            Reserva reservaExistente = null;
            
            // Buscar si existe una reserva para esta hora
            for (Reserva reserva : reservas) {
                if (reserva.getHora().toString().equals(hora)) {
                    estado = reserva.getEstado();
                    reservaExistente = reserva;
                    break;
                }
            }
            
            JPanel horaPanel = crearPanelHora(i + 1, hora, estado, reservaExistente);
            horariosPanel.add(horaPanel);
        }
        
        horariosPanel.revalidate();
        horariosPanel.repaint();
        actualizarInfoFecha();
        
        // Scroll al inicio
        SwingUtilities.invokeLater(() -> {
            JViewport viewport = scrollPane.getViewport();
            viewport.setViewPosition(new Point(0, 0));
        });
    }
    
    private JPanel crearPanelHora(int numero, String hora, String estado, Reserva reserva) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setBackground(Color.WHITE);
        
        // Número y hora
        JLabel lblNumeroHora = new JLabel(numero + ". " + hora);
        lblNumeroHora.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Estado con color
        JLabel lblEstado = new JLabel(estado);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 14));
        
        if (estado.equals("Disponible")) {
            lblEstado.setForeground(new Color(0, 150, 0));
        } else {
            lblEstado.setForeground(new Color(200, 0, 0));
        }
        
        // Botones de acción
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        if (estado.equals("Disponible")) {
            JButton btnReservar = new JButton("Reservar");
            btnReservar.setBackground(new Color(50, 205, 50));
            btnReservar.setForeground(Color.WHITE);
            btnReservar.addActionListener(e -> reservarHora(hora));
            botonesPanel.add(btnReservar);
        } else {
            JButton btnLiberar = new JButton("Liberar");
            btnLiberar.setBackground(new Color(220, 20, 60));
            btnLiberar.setForeground(Color.WHITE);
            btnLiberar.addActionListener(e -> liberarHora(hora, reserva));
            botonesPanel.add(btnLiberar);
            
            JButton btnEditar = new JButton("Editar");
            btnEditar.setBackground(new Color(255, 140, 0));
            btnEditar.setForeground(Color.WHITE);
            btnEditar.addActionListener(e -> editarReserva(hora, reserva));
            botonesPanel.add(btnEditar);
            
            // Mostrar información del cliente si existe
            if (reserva != null && reserva.getClienteNombre() != null) {
                JLabel lblCliente = new JLabel("Cliente: " + reserva.getClienteNombre());
                lblCliente.setFont(new Font("Arial", Font.ITALIC, 12));
                lblCliente.setForeground(Color.DARK_GRAY);
                panel.add(lblCliente, BorderLayout.SOUTH);
            }
        }
        
        panel.add(lblNumeroHora, BorderLayout.WEST);
        panel.add(lblEstado, BorderLayout.CENTER);
        panel.add(botonesPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void reservarHora(String hora) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva Reserva", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblInfo = new JLabel("Reservando: " + fechaActual + " " + hora);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField txtNombre = new JTextField();
        JTextField txtEmail = new JTextField();
        
        formPanel.add(new JLabel("Fecha/Hora:"));
        formPanel.add(lblInfo);
        formPanel.add(new JLabel("Nombre Cliente:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Email Cliente:"));
        formPanel.add(txtEmail);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = new JButton("Guardar Reserva");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.setBackground(new Color(50, 205, 50));
        btnGuardar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(220, 20, 60));
        btnCancelar.setForeground(Color.WHITE);
        
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Por favor, complete todos los campos");
                return;
            }
            
            try {
                Reserva reserva = new Reserva();
                reserva.setFecha(fechaActual);
                reserva.setHora(java.time.LocalTime.parse(hora));
                reserva.setEstado("Ocupado");
                reserva.setClienteNombre(txtNombre.getText().trim());
                reserva.setClienteEmail(txtEmail.getText().trim());
                
                // Buscar si ya existe una reserva para esta hora
                List<Reserva> reservas = reservaDAO.obtenerReservasPorFecha(fechaActual);
                boolean existe = false;
                
                for (Reserva r : reservas) {
                    if (r.getHora().toString().equals(hora)) {
                        // Actualizar reserva existente
                        r.setEstado("Ocupado");
                        r.setClienteNombre(txtNombre.getText().trim());
                        r.setClienteEmail(txtEmail.getText().trim());
                        reservaDAO.actualizarReserva(r);
                        existe = true;
                        break;
                    }
                }
                
                if (!existe) {
                    reservaDAO.crearReserva(reserva);
                }
                
                JOptionPane.showMessageDialog(dialog, "Reserva creada exitosamente!");
                dialog.dispose();
                cargarHorarios();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void liberarHora(String hora, Reserva reserva) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de liberar esta hora?\nCliente: " + 
            (reserva != null ? reserva.getClienteNombre() : "N/A"), 
            "Confirmar Liberación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (reserva != null) {
                reserva.setEstado("Disponible");
                reserva.setClienteNombre(null);
                reserva.setClienteEmail(null);
                reservaDAO.actualizarReserva(reserva);
                cargarHorarios();
                JOptionPane.showMessageDialog(this, "Hora liberada exitosamente!");
            }
        }
    }
    
    private void editarReserva(String hora, Reserva reserva) {
        if (reserva == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Reserva", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblInfo = new JLabel("Editando: " + fechaActual + " " + hora);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField txtNombre = new JTextField(reserva.getClienteNombre() != null ? reserva.getClienteNombre() : "");
        JTextField txtEmail = new JTextField(reserva.getClienteEmail() != null ? reserva.getClienteEmail() : "");
        
        formPanel.add(new JLabel("Fecha/Hora:"));
        formPanel.add(lblInfo);
        formPanel.add(new JLabel("Nombre Cliente:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Email Cliente:"));
        formPanel.add(txtEmail);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = new JButton("Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Por favor, complete todos los campos");
                return;
            }
            
            reserva.setClienteNombre(txtNombre.getText().trim());
            reserva.setClienteEmail(txtEmail.getText().trim());
            
            if (reservaDAO.actualizarReserva(reserva)) {
                JOptionPane.showMessageDialog(dialog, "Reserva actualizada exitosamente!");
                dialog.dispose();
                cargarHorarios();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al actualizar la reserva");
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void crearReservasPorDefecto(LocalDate fecha) {
    	String[] horas = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
        
        // Solo marcar algunos como ocupados por defecto (3 máximo)
        for (int i = 0; i < horas.length; i++) {
            Reserva reserva = new Reserva();
            reserva.setFecha(fecha);
            reserva.setHora(java.time.LocalTime.parse(horas[i]));
            
            // Marcar solo 10:00, 13:00 y 14:00 como ocupados (como en el ejemplo)
            if (horas[i].equals("10:00") || horas[i].equals("13:00") || horas[i].equals("14:00")) {
                reserva.setEstado("Ocupado");
                reserva.setClienteNombre("Cliente Ejemplo");
                reserva.setClienteEmail("cliente@ejemplo.com");
            } else {
                reserva.setEstado("Disponible");
            }
            
            reservaDAO.crearReserva(reserva);
        }
    }
    
    private void cambiarDia(int dias) {
        fechaActual = fechaActual.plusDays(dias);
        cargarHorarios();
    }
    
    private void irAHoy() {
        fechaActual = LocalDate.now();
        cargarHorarios();
    }
    
    private void seleccionarFecha() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Seleccionar Fecha", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblAnio = new JLabel("Año:");
        JTextField txtAnio = new JTextField(String.valueOf(fechaActual.getYear()));
        
        JLabel lblMes = new JLabel("Mes (1-12):");
        JTextField txtMes = new JTextField(String.valueOf(fechaActual.getMonthValue()));
        
        JLabel lblDia = new JLabel("Día:");
        JTextField txtDia = new JTextField(String.valueOf(fechaActual.getDayOfMonth()));
        
        panel.add(lblAnio);
        panel.add(txtAnio);
        panel.add(lblMes);
        panel.add(txtMes);
        panel.add(lblDia);
        panel.add(txtDia);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAceptar = new JButton("Ir a Fecha");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(e -> {
            try {
                int anio = Integer.parseInt(txtAnio.getText());
                int mes = Integer.parseInt(txtMes.getText());
                int dia = Integer.parseInt(txtDia.getText());
                
                fechaActual = LocalDate.of(anio, mes, dia);
                dialog.dispose();
                cargarHorarios();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Fecha inválida. Formato: Año Mes Día");
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnAceptar);
        buttonPanel.add(btnCancelar);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void crearReservaManual() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Crear Reserva Manual", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtFecha = new JTextField(fechaActual.toString());
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
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = new JButton("Crear Reserva");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.setBackground(new Color(50, 205, 50));
        btnGuardar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(220, 20, 60));
        btnCancelar.setForeground(Color.WHITE);
        
        btnGuardar.addActionListener(e -> {
            try {
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
                
                // Si es la fecha actual, recargar horarios
                if (reserva.getFecha().equals(fechaActual)) {
                    cargarHorarios();
                }
                
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void liberarTodosOcupados() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de liberar TODAS las horas ocupadas de " + fechaActual + "?",
            "Confirmar Liberación Masiva", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<Reserva> reservas = reservaDAO.obtenerReservasPorFecha(fechaActual);
            int liberadas = 0;
            
            for (Reserva reserva : reservas) {
                if (reserva.getEstado().equals("Ocupado")) {
                    reserva.setEstado("Disponible");
                    reserva.setClienteNombre(null);
                    reserva.setClienteEmail(null);
                    reservaDAO.actualizarReserva(reserva);
                    liberadas++;
                }
            }
            
            cargarHorarios();
            JOptionPane.showMessageDialog(this, 
                "Se liberaron " + liberadas + " horas ocupadas exitosamente!");
        }
    }
    
    private void crearHorariosDisponibles() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Crear todos los horarios disponibles (9:00-16:00) para " + fechaActual + "?\n" +
            "Nota: Esto no afectará las horas ya existentes.",
            "Crear Horarios Disponibles", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String[] horas = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
            int creados = 0;
            
            // Obtener reservas existentes
            List<Reserva> reservas = reservaDAO.obtenerReservasPorFecha(fechaActual);
            
            for (String hora : horas) {
                boolean existe = false;
                
                // Verificar si ya existe esta hora
                for (Reserva reserva : reservas) {
                    if (reserva.getHora().toString().equals(hora)) {
                        existe = true;
                        break;
                    }
                }
                
                // Crear si no existe
                if (!existe) {
                    Reserva reserva = new Reserva();
                    reserva.setFecha(fechaActual);
                    reserva.setHora(java.time.LocalTime.parse(hora));
                    reserva.setEstado("Disponible");
                    reservaDAO.crearReserva(reserva);
                    creados++;
                }
            }
            
            cargarHorarios();
            JOptionPane.showMessageDialog(this, 
                "Se crearon " + creados + " nuevos horarios disponibles exitosamente!");
        }
    }
    
    private String obtenerNombreDia(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "Lunes";
            case TUESDAY: return "Martes";
            case WEDNESDAY: return "Miércoles";
            case THURSDAY: return "Jueves";
            case FRIDAY: return "Viernes";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return "";
        }
    }
    
    private String obtenerNombreMes(int mes) {
        String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                         "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes];
    }
    
    public void actualizarFecha(LocalDate nuevaFecha) {
        this.fechaActual = nuevaFecha;
        cargarHorarios();
    }
}