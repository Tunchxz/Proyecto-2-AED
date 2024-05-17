package uvg.edu.gt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class Product extends JFrame {
    private JPanel contentPane;
    private JTextField searchField;
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel category1Label;
    private JLabel category2Label;
    private JLabel brandLabel;
    private JTable recommendedProductsTable;
    private DBManager dbManager;

    public Product(Map<String, Object> productDetails, List<Map<String, Object>> relatedProducts) {
        String dbUri = "neo4j://localhost";
        String dbUser = "neo4j";
        String dbPassword = "12345678";

        // Crear una instancia de DBManager usando el método estático getInstance
        dbManager = DBManager.getInstance(dbUri, dbUser, dbPassword);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        JPanel topPanel = new JPanel(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.NORTH);

        searchField = new JTextField();
        topPanel.add(searchField, BorderLayout.CENTER);
        searchField.setPreferredSize(new Dimension(300, 30));

        JButton backButton = new JButton("Volver");
        backButton.setBackground(new Color(0, 112, 192));
        backButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        backButton.setForeground(new Color(255, 255, 255));
        topPanel.add(backButton, BorderLayout.WEST);
        backButton.addActionListener(e -> {
            // Regresar a Home
            this.dispose();
        });

        JButton buyButton = new JButton("Comprar");
        buyButton.setBackground(new Color(0, 112, 192));
        buyButton.setForeground(new Color(255, 255, 255));
        buyButton.setFont(new Font("Tahoma", Font.BOLD, 11));
        topPanel.add(buyButton, BorderLayout.EAST);
        buyButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas comprar este producto?",
                    "Confirmar compra", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Realizar la acción de compra
                realizarCompra(productDetails);
            }
        });

        JPanel productPanel = new JPanel();
        contentPane.add(productPanel, BorderLayout.CENTER);
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.X_AXIS));

        imageLabel = new JLabel();
        productPanel.add(imageLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        productPanel.add(infoPanel);

        nameLabel = new JLabel();
        infoPanel.add(nameLabel);

        priceLabel = new JLabel();
        infoPanel.add(priceLabel);

        category1Label = new JLabel();
        infoPanel.add(category1Label);

        category2Label = new JLabel();
        infoPanel.add(category2Label);

        brandLabel = new JLabel();
        infoPanel.add(brandLabel);

        // Set product details
        nameLabel.setText("Nombre: " + productDetails.get("nombre"));
        priceLabel.setText("Precio: " + productDetails.get("precio"));
        category1Label.setText("Categoría 1: " + productDetails.get("categoria1"));
        category2Label.setText("Categoría 2: " + productDetails.get("categoria2"));
        brandLabel.setText("Marca: " + productDetails.get("marca"));
        setImage(productDetails.get("imagen").toString());

        // Obtain and display recommended products
        List<Map<String, Object>> recommendedProducts = dbManager.getRecommendedProducts(dbManager.getLoggedInUser());
        String[] recommendedColumnNames = {"Nombre", "Precio", "Marca"};
        Object[][] recommendedData = new Object[recommendedProducts.size()][3];
        for (int i = 0; i < recommendedProducts.size(); i++) {
            recommendedData[i][0] = recommendedProducts.get(i).get("nombre");
            recommendedData[i][1] = recommendedProducts.get(i).get("precio");
            recommendedData[i][2] = recommendedProducts.get(i).get("marca");
        }

        recommendedProductsTable = new JTable(recommendedData, recommendedColumnNames);
        JScrollPane recommendedScrollPane = new JScrollPane(recommendedProductsTable);
        recommendedScrollPane.setPreferredSize(new Dimension(100, 100));

        // Panel para el título y la tabla
        JPanel recommendedPanel = new JPanel(new BorderLayout());
        JLabel recommendedLabel = new JLabel("Productos Recomendados");
        recommendedLabel.setForeground(new Color(0, 112, 192));
        recommendedLabel.setBackground(new Color(0, 112, 192));
        recommendedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        recommendedLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        recommendedPanel.add(recommendedLabel, BorderLayout.NORTH);
        recommendedPanel.add(recommendedScrollPane, BorderLayout.CENTER);

        contentPane.add(recommendedPanel, BorderLayout.SOUTH);

        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
        // Evitar redimensionar la ventana
        setResizable(false);
    }

    private void setImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon imageIcon = new ImageIcon(imagePath);
            // Scale the image to fit the label
            Image image = imageIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        } else {
            imageLabel.setIcon(null);
        }
    }

    private void realizarCompra(Map<String, Object> productDetails) {
        // Realizar la compra en la base de datos
        boolean compraExitosa = dbManager.realizarCompra(productDetails);

        if (compraExitosa) {
            JOptionPane.showMessageDialog(this, "¡Compra realizada con éxito!");
        } else {
            JOptionPane.showMessageDialog(this, "Hubo un problema al realizar la compra. Por favor, inténtalo de nuevo.");
        }
    }
}