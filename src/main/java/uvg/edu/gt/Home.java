package uvg.edu.gt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

public class Home extends JFrame {

    private JPanel contentPane;
    private List<Map<String, Object>> productList;
    private int currentPage;
    private JTextField searchField;
    private DBManager dbManager;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Home frame = new Home();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Home() {
        String dbUri = "neo4j://localhost";
        String dbUser = "neo4j";
        String dbPassword = "12345678";

        // Crear una instancia de DBManager usando el método estático getInstance
        dbManager = DBManager.getInstance(dbUri, dbUser, dbPassword);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 980, 761);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 240, 240));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JPanel topPanel = new JPanel(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.NORTH);

        searchField = new JTextField();
        topPanel.add(searchField, BorderLayout.CENTER);
        searchField.setPreferredSize(new Dimension(300, 30));

        JButton searchButton = new JButton("Buscar");
        searchButton.setForeground(new Color(255, 255, 255));
        searchButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        searchButton.setBackground(new Color(0, 112, 192));
        topPanel.add(searchButton, BorderLayout.EAST);

        JPanel itemsPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        itemsPanel.setBackground(new Color(240, 240, 240));
        contentPane.add(itemsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(240, 240, 240));
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        JButton prevButton = new JButton("<<");
        prevButton.setBackground(new Color(0, 112, 192));
        prevButton.setForeground(new Color(255, 255, 255));
        bottomPanel.add(prevButton);

        JButton nextButton = new JButton(">>");
        nextButton.setBackground(new Color(0, 112, 192));
        nextButton.setForeground(new Color(255, 255, 255));
        bottomPanel.add(nextButton);

        
     // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
     // Evitar redimensionar la ventana
        setResizable(false); 
    }
}