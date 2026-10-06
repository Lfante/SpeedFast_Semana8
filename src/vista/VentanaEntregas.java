package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * CRUD gráfico de entregas.
 */
public class VentanaEntregas extends JFrame {

    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private JComboBox<Pedido> comboPedido;
    private JComboBox<Repartidor> comboRepartidor;
    private JTextField campoFecha;
    private JTextField campoHora;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private int idSeleccionado = -1;

    public VentanaEntregas() {
        setTitle("SpeedFast - Entregas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(930, 560);
        setLocationRelativeTo(null);

        inicializarComponentes();
        cargarReferencias();
        cargarTabla();

        /*
         * Si se crean, editan o eliminan pedidos/repartidores en otra ventana,
         * al volver a esta ventana se recargan los JComboBox desde la BD.
         */
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                cargarReferencias();
                cargarTabla();
            }
        });
    }

    private void inicializarComponentes() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Gestión de entregas", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));

        formulario.add(new JLabel("Pedido:"));
        comboPedido = new JComboBox<>();
        formulario.add(comboPedido);

        formulario.add(new JLabel("Repartidor:"));
        comboRepartidor = new JComboBox<>();
        formulario.add(comboRepartidor);

        formulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        campoFecha = new JTextField(LocalDate.now().toString());
        formulario.add(campoFecha);

        formulario.add(new JLabel("Hora (HH:mm o HH:mm:ss):"));
        campoHora = new JTextField(
                LocalTime.now().withNano(0).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
        formulario.add(campoHora);

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.add(titulo, BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Pedido", "Repartidor", "Fecha", "Hora"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        JButton crear = new JButton("Crear");
        JButton actualizar = new JButton("Actualizar");
        JButton eliminar = new JButton("Eliminar");
        JButton limpiar = new JButton("Limpiar");
        JButton refrescar = new JButton("Refrescar");

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        acciones.add(crear);
        acciones.add(actualizar);
        acciones.add(eliminar);
        acciones.add(limpiar);
        acciones.add(refrescar);

        crear.addActionListener(e -> crear());
        actualizar.addActionListener(e -> actualizar());
        eliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiarFormulario());
        refrescar.addActionListener(e -> {
            cargarReferencias();
            cargarTabla();
        });

        principal.add(superior, BorderLayout.NORTH);
        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);
        principal.add(acciones, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private void cargarReferencias() {
        Integer pedidoSeleccionado = obtenerIdPedidoSeleccionado();
        Integer repartidorSeleccionado = obtenerIdRepartidorSeleccionado();

        comboPedido.removeAllItems();
        comboRepartidor.removeAllItems();

        try {
            for (Pedido pedido : pedidoDAO.readAll()) {
                comboPedido.addItem(pedido);
            }

            for (Repartidor repartidor : repartidorDAO.readAll()) {
                comboRepartidor.addItem(repartidor);
            }

            if (pedidoSeleccionado != null) {
                seleccionarPedidoPorId(pedidoSeleccionado);
            }

            if (repartidorSeleccionado != null) {
                seleccionarRepartidorPorId(repartidorSeleccionado);
            }

        } catch (SQLException e) {
            mostrarError("No se pudieron cargar pedidos o repartidores.", e);
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);

        try {
            Map<Integer, String> pedidos = new HashMap<>();
            for (Pedido pedido : pedidoDAO.readAll()) {
                pedidos.put(pedido.getId(), pedido.toString());
            }

            Map<Integer, String> repartidores = new HashMap<>();
            for (Repartidor repartidor : repartidorDAO.readAll()) {
                repartidores.put(repartidor.getId(), repartidor.toString());
            }

            for (Entrega entrega : entregaDAO.readAll()) {
                modeloTabla.addRow(new Object[]{
                        entrega.getId(),
                        pedidos.getOrDefault(
                                entrega.getIdPedido(),
                                String.valueOf(entrega.getIdPedido())
                        ),
                        repartidores.getOrDefault(
                                entrega.getIdRepartidor(),
                                String.valueOf(entrega.getIdRepartidor())
                        ),
                        entrega.getFecha(),
                        entrega.getHora()
                });
            }

        } catch (SQLException e) {
            mostrarError("No se pudieron consultar las entregas.", e);
        }
    }

    private boolean validarFormulario() {
        if (comboPedido.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un pedido.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (comboRepartidor.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un repartidor.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (campoFecha.getText().trim().isEmpty()
                || campoHora.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Fecha y hora son obligatorias.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        try {
            LocalDate.parse(campoFecha.getText().trim());
            parseHora(campoHora.getText().trim());
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Usa fecha AAAA-MM-DD y hora HH:mm o HH:mm:ss.",
                    "Formato inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
    }

    private Entrega obtenerFormulario(int id) {
        Pedido pedido = (Pedido) comboPedido.getSelectedItem();
        Repartidor repartidor = (Repartidor) comboRepartidor.getSelectedItem();

        return new Entrega(
                id,
                pedido.getId(),
                repartidor.getId(),
                LocalDate.parse(campoFecha.getText().trim()),
                parseHora(campoHora.getText().trim())
        );
    }

    private LocalTime parseHora(String texto) {
        try {
            return LocalTime.parse(texto, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return LocalTime.parse(texto, DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    private void crear() {
        if (!validarFormulario()) {
            return;
        }

        Entrega entrega = obtenerFormulario(0);

        try {
            entregaDAO.create(entrega);
            JOptionPane.showMessageDialog(this, "Entrega registrada correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo registrar la entrega.", e);
        }
    }

    private void actualizar() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una entrega de la tabla.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        try {
            entregaDAO.update(obtenerFormulario(idSeleccionado));
            JOptionPane.showMessageDialog(this, "Entrega actualizada correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo actualizar la entrega.", e);
        }
    }

    private void eliminar() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una entrega de la tabla.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la entrega seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            entregaDAO.delete(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Entrega eliminada correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo eliminar la entrega.", e);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }

        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);

        String pedidoTexto = modeloTabla.getValueAt(fila, 1).toString();
        String repartidorTexto = modeloTabla.getValueAt(fila, 2).toString();

        seleccionarPedidoPorId(extraerId(pedidoTexto));
        seleccionarRepartidorPorId(extraerId(repartidorTexto));

        campoFecha.setText(modeloTabla.getValueAt(fila, 3).toString());
        campoHora.setText(modeloTabla.getValueAt(fila, 4).toString());
    }

    private int extraerId(String texto) {
        int separador = texto.indexOf(" - ");
        if (separador < 0) {
            return Integer.parseInt(texto.trim());
        }
        return Integer.parseInt(texto.substring(0, separador).trim());
    }

    private void seleccionarPedidoPorId(int id) {
        for (int i = 0; i < comboPedido.getItemCount(); i++) {
            Pedido pedido = comboPedido.getItemAt(i);
            if (pedido.getId() == id) {
                comboPedido.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarRepartidorPorId(int id) {
        for (int i = 0; i < comboRepartidor.getItemCount(); i++) {
            Repartidor repartidor = comboRepartidor.getItemAt(i);
            if (repartidor.getId() == id) {
                comboRepartidor.setSelectedIndex(i);
                return;
            }
        }
    }

    private Integer obtenerIdPedidoSeleccionado() {
        Pedido pedido = (Pedido) comboPedido.getSelectedItem();
        return pedido == null ? null : pedido.getId();
    }

    private Integer obtenerIdRepartidorSeleccionado() {
        Repartidor repartidor = (Repartidor) comboRepartidor.getSelectedItem();
        return repartidor == null ? null : repartidor.getId();
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        tabla.clearSelection();

        if (comboPedido.getItemCount() > 0) {
            comboPedido.setSelectedIndex(0);
        }

        if (comboRepartidor.getItemCount() > 0) {
            comboRepartidor.setSelectedIndex(0);
        }

        campoFecha.setText(LocalDate.now().toString());
        campoHora.setText(
                LocalTime.now().withNano(0).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
    }

    private void mostrarError(String mensaje, SQLException e) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + "\n\nDetalle: " + e.getMessage(),
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
