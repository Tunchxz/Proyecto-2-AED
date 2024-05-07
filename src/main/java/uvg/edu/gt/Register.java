package uvg.edu.gt;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Register extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textCrearContra;
    private JTextField textCrearUsuario;
    private DBManager dbManager;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Register frame = new Register();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Register() {
        final String dbUri = "neo4j://localhost";
        final String dbUser = "neo4j";
        final String dbPassword = "12345678";

        dbManager = new DBManager(dbUri, dbUser, dbPassword);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 645, 760);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 705, 802);
        contentPane.add(panel);
        panel.setLayout(null);

        textCrearContra = new JTextField();
        textCrearContra.setFont(new Font("Consolas", Font.BOLD, 25));
        textCrearContra.setBounds(295, 406, 262, 44);
        panel.add(textCrearContra);
        textCrearContra.setColumns(10);

        textCrearUsuario = new JTextField();
        textCrearUsuario.setFont(new Font("Consolas", Font.BOLD, 25));
        textCrearUsuario.setColumns(10);
        textCrearUsuario.setBounds(295, 328, 262, 44);
        panel.add(textCrearUsuario);

        JLabel txtpnUsuario = new JLabel();
        txtpnUsuario.setForeground(new Color(255, 255, 255));
        txtpnUsuario.setFont(new Font("Consolas", Font.BOLD, 25));
        txtpnUsuario.setText("Usuario:");
        txtpnUsuario.setBounds(145, 332, 123, 36);
        panel.add(txtpnUsuario);

        JLabel txtpnContrasea = new JLabel();
        txtpnContrasea.setForeground(new Color(255, 255, 255));
        txtpnContrasea.setText("Contraseña:");
        txtpnContrasea.setFont(new Font("Consolas", Font.BOLD, 25));
        txtpnContrasea.setBounds(107, 410, 161, 36);
        panel.add(txtpnContrasea);

        JLabel txtpnTipoDeCliente = new JLabel();
        txtpnTipoDeCliente.setForeground(new Color(255, 255, 255));
        txtpnTipoDeCliente.setText("Tipo de Cliente:");
        txtpnTipoDeCliente.setFont(new Font("Consolas", Font.BOLD, 25));
        txtpnTipoDeCliente.setBounds(31, 484, 237, 44);
        panel.add(txtpnTipoDeCliente);

        JComboBox<String> comboBoxTipo = new JComboBox<>();
        comboBoxTipo.setFont(new Font("Consolas", Font.BOLD, 25));
        comboBoxTipo.setModel(new DefaultComboBoxModel<>(new String[] { "", "Mayorista", "Regular", "Novato" }));
        comboBoxTipo.setBounds(295, 484, 262, 44);
        panel.add(comboBoxTipo);

        JButton btnRegistrarse = new JButton("Registrarse");
        btnRegistrarse.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 20));
        btnRegistrarse.setBackground(UIManager.getColor("Button.background"));
        btnRegistrarse.setBounds(221, 580, 174, 61);
        panel.add(btnRegistrarse);

        // Agregar JLabel para la imagen de fondo
        JLabel lblBackground = new JLabel("");
        lblBackground.setIcon(new ImageIcon("src\\main\\resources\\register.png"));
        lblBackground.setBounds(0, 0, 645, 720);
        panel.add(lblBackground);

        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
        // Evitar redimensionar la ventana
        setResizable(false);
    }
}