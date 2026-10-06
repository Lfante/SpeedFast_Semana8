package vista;

import dao.PedidoDAO;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * CRUD gráfico de pedidos.
 */
public class VentanaPedidos extends JFrame {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private JTextField campoDireccion;
    private JComboBox<TipoPedido> comboTipo;
    private JComboBox<EstadoPedido> comboEstado;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private int idSeleccionado = -1;

    public VentanaPedidos() {
        setTitle("SpeedFast - Pedidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(830, 520);
        setLocationRelativeTo(null);

        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Gestión de pedidos", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel formulario = new JPanel(new GridLayout(3, 2, 8, 8));

        formulario.add(new JLabel("Dirección:"));
        campoDireccion = new JTextField();
        formulario.add(campoDireccion);

        formulario.add(new JLabel("Tipo:"));
        comboTipo = new JComboBox<>(TipoPedido.values());
        formulario.add(comboTipo);

        formulario.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>(EstadoPedido.values());
        formulario.add(comboEstado);

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.add(titulo, BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Dirección", "Tipo", "Estado"},
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
        refrescar.addActionListener(e -> cargarTabla());

        principal.add(superior, BorderLayout.NORTH);
        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);
        principal.add(acciones, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private boolean validar() {
        if (campoDireccion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "La dirección es obligatoria.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        return comboTipo.getSelectedItem() != null
                && comboEstado.getSelectedItem() != null;
    }

    private Pedido obtenerFormulario(int id) {
        return new Pedido(
                id,
                campoDireccion.getText().trim(),
                (TipoPedido) comboTipo.getSelectedItem(),
                (EstadoPedido) comboEstado.getSelectedItem()
        );
    }

    private void crear() {
        if (!validar()) {
            return;
        }

        Pedido pedido = obtenerFormulario(0);

        try {
            pedidoDAO.create(pedido);
            JOptionPane.showMessageDialog(this, "Pedido registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo registrar el pedido.", e);
        }
    }

    private void actualizar() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un pedido de la tabla.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!validar()) {
            return;
        }

        try {
            pedidoDAO.update(obtenerFormulario(idSeleccionado));
            JOptionPane.showMessageDialog(this, "Pedido actualizado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo actualizar el pedido.", e);
        }
    }

    private void eliminar() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un pedido de la tabla.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el pedido seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            pedidoDAO.delete(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Pedido eliminado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError(
                    "No se pudo eliminar. Revisa si el pedido tiene entregas asociadas.",
                    e
            );
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);

        try {
            for (Pedido pedido : pedidoDAO.readAll()) {
                modeloTabla.addRow(new Object[]{
                        pedido.getId(),
                        pedido.getDireccion(),
                        pedido.getTipo(),
                        pedido.getEstado()
                });
            }
        } catch (SQLException e) {
            mostrarError("No se pudieron consultar los pedidos.", e);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }

        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        campoDireccion.setText(modeloTabla.getValueAt(fila, 1).toString());
        comboTipo.setSelectedItem(
                TipoPedido.valueOf(modeloTabla.getValueAt(fila, 2).toString())
        );
        comboEstado.setSelectedItem(
                EstadoPedido.valueOf(modeloTabla.getValueAt(fila, 3).toString())
        );
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        campoDireccion.setText("");
        comboTipo.setSelectedIndex(0);
        comboEstado.setSelectedItem(EstadoPedido.PENDIENTE);
        tabla.clearSelection();
        campoDireccion.requestFocus();
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
