package uvg.edu.gt;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textUsuario;
    private JPasswordField passwordField;
    private DBManager dbManager;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login frame = new Login();
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
    public Login() {
        String dbUri = "neo4j://localhost";
        String dbUser = "neo4j";
        String dbPassword = "12345678";

        // Crear una instancia de DBManager usando el método estático getInstance
        dbManager = DBManager.getInstance(dbUri, dbUser, dbPassword);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 645, 760);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Crear un panel para contener los componentes de la interfaz de usuario
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 128, 0)); // Transparente
        panel.setBounds(0, 0, 772, 785);
        contentPane.add(panel);
        panel.setLayout(null);

        textUsuario = new JTextField();
        textUsuario.setFont(new Font("Consolas", Font.BOLD, 30));
        textUsuario.setBounds(193, 420, 298, 49);
        panel.add(textUsuario);
        textUsuario.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 30));
        passwordField.setBounds(193, 524, 298, 49);
        panel.add(passwordField);

        JLabel txtpnUsuario = new JLabel();
        txtpnUsuario.setForeground(new Color(255, 255, 255));
        txtpnUsuario.setFont(new Font("Consolas", Font.BOLD, 23));
        txtpnUsuario.setText("Contraseña");
        txtpnUsuario.setBounds(269, 485, 136, 49);
        panel.add(txtpnUsuario);

        JLabel txtpnUsuario_1 = new JLabel();
        txtpnUsuario_1.setForeground(new Color(255, 255, 255));
        txtpnUsuario_1.setText("Usuario");
        txtpnUsuario_1.setFont(new Font("Lucida Console", Font.BOLD, 23));
        txtpnUsuario_1.setBounds(269, 378, 119, 49);
        panel.add(txtpnUsuario_1);

        
        
        // Agregar JLabel para la imagen de fondo
        JLabel lblBackground = new JLabel("");
        lblBackground.setIcon(new ImageIcon("src\\main\\resources\\fondo.png"));
        lblBackground.setBounds(0, 0, 645, 720);
        contentPane.add(lblBackground);

        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
        // Evitar redimensionar la ventana
        setResizable(false); 
    }
}