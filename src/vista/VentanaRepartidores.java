package vista;

import dao.RepartidorDAO;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * CRUD gráfico de repartidores.
 */
public class VentanaRepartidores extends JFrame {

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private JTextField campoNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private int idSeleccionado = -1;

    public VentanaRepartidores() {
        setTitle("SpeedFast - Repartidores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 470);
        setLocationRelativeTo(null);

        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Gestión de repartidores", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel formulario = new JPanel(new BorderLayout(8, 8));
        formulario.add(new JLabel("Nombre:"), BorderLayout.WEST);
        campoNombre = new JTextField();
        formulario.add(campoNombre, BorderLayout.CENTER);

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.add(titulo, BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.SOUTH);

        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0) {
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

    private boolean validarNombre() {
        if (campoNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El nombre del repartidor es obligatorio.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private void crear() {
        if (!validarNombre()) {
            return;
        }

        Repartidor repartidor = new Repartidor(campoNombre.getText().trim());

        try {
            repartidorDAO.create(repartidor);
            JOptionPane.showMessageDialog(this, "Repartidor registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo registrar el repartidor.", e);
        }
    }

    private void actualizar() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un repartidor de la tabla.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!validarNombre()) {
            return;
        }

        try {
            repartidorDAO.update(
                    new Repartidor(idSeleccionado, campoNombre.getText().trim())
            );
            JOptionPane.showMessageDialog(this, "Repartidor actualizado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError("No se pudo actualizar el repartidor.", e);
        }
    }

    private void eliminar() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un repartidor de la tabla.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el repartidor seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            repartidorDAO.delete(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Repartidor eliminado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException e) {
            mostrarError(
                    "No se pudo eliminar. Revisa si el repartidor tiene entregas asociadas.",
                    e
            );
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);

        try {
            for (Repartidor repartidor : repartidorDAO.readAll()) {
                modeloTabla.addRow(new Object[]{
                        repartidor.getId(),
                        repartidor.getNombre()
                });
            }
        } catch (SQLException e) {
            mostrarError("No se pudieron consultar los repartidores.", e);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }

        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        campoNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        campoNombre.setText("");
        tabla.clearSelection();
        campoNombre.requestFocus();
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
