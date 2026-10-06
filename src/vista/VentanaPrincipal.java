package vista;

import dao.ConexionDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ventana principal para navegar entre los módulos CRUD.
 */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("SpeedFast - CRUD con JDBC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 390);
        setLocationRelativeTo(null);
        setResizable(false);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(22, 35, 28, 35));

        JLabel titulo = new JLabel("SpeedFast - Gestión completa", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitulo = new JLabel(
                "Pedidos, repartidores y entregas",
                SwingConstants.CENTER
        );

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 5));
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        JButton botonRepartidores = new JButton("Gestionar repartidores");
        JButton botonPedidos = new JButton("Gestionar pedidos");
        JButton botonEntregas = new JButton("Gestionar entregas");
        JButton botonConexion = new JButton("Probar conexión a MySQL");

        JPanel botones = new JPanel(new GridLayout(4, 1, 10, 10));
        botones.setBorder(new EmptyBorder(10, 45, 0, 45));
        botones.add(botonRepartidores);
        botones.add(botonPedidos);
        botones.add(botonEntregas);
        botones.add(botonConexion);

        botonRepartidores.addActionListener(e ->
                new VentanaRepartidores().setVisible(true)
        );

        botonPedidos.addActionListener(e ->
                new VentanaPedidos().setVisible(true)
        );

        botonEntregas.addActionListener(e ->
                new VentanaEntregas().setVisible(true)
        );

        botonConexion.addActionListener(e -> probarConexion());

        panel.add(encabezado, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.CENTER);
        setContentPane(panel);
    }

    private void probarConexion() {
        try (Connection conexion = ConexionDB.conectar()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Conexión a MySQL realizada correctamente.",
                    "Conexión exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible conectar con MySQL.\n\n" + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
