import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * Clase Home que representa la ventana principal de la aplicación.
 * Extiende de JFrame.
 */
public class Home extends JFrame {

    private JPanel contentPane;
    private Item[][] items;
    private List<Map<String, Object>> productList;
    private int currentPage;
    private JTextField searchField;
    private DBManager dbManager;
    
    /**
     * Método principal para ejecutar la aplicación.
     * @param args Argumentos de línea de comandos.
     */
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

    /**
     * Constructor de la clase Home.
     * Configura la ventana y sus componentes.
     */
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

        currentPage = 0;
        productList = dbManager.getAllProducts();
        updateItems(itemsPanel);

        /**
         * Añade un ActionListener al botón "Buscar" que maneja el evento de búsqueda de productos.
         */
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText().trim();
                productList = dbManager.searchProducts(query);
                currentPage = 0;
                updateItems(itemsPanel);
            }
        });

        /**
         * Añade un ActionListener al botón "<<" que maneja el evento de retroceder página.
         */
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 0) {
                    currentPage--;
                    updateItems(itemsPanel);
                }
            }
        });

        /**
         * Añade un ActionListener al botón ">>" que maneja el evento de avanzar página.
         */
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((currentPage + 1) * 9 < productList.size()) {
                    currentPage++;
                    updateItems(itemsPanel);
                }
            }
        });
        
        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
        
        // Evitar redimensionar la ventana
        setResizable(false); 
    }

    /**
     * Actualiza los elementos mostrados en el panel de productos.
     * @param itemsPanel Panel donde se muestran los productos.
     */
    private void updateItems(JPanel itemsPanel) {
        itemsPanel.removeAll();
        int startIndex = currentPage * 9;
        int endIndex = Math.min((currentPage + 1) * 9, productList.size());
        items = new Item[3][3];
        for (int i = startIndex; i < endIndex; i++) {
            int row = (i - startIndex) / 3;
            int col = (i - startIndex) % 3;
            Map<String, Object> product = productList.get(i);
            String id = product.get("id").toString();
            String nombre = product.get("nombre").toString();
            double precio = Double.parseDouble(product.get("precio").toString());
            String categoria1 = product.get("categoria1").toString();
            String categoria2 = product.get("categoria2").toString();
            String marca = product.get("marca").toString();
            String imagen = product.get("imagen").toString();
            
            Item item = new Item(id, nombre, precio, categoria1, categoria2, marca, imagen);
            item.setPreferredSize(new Dimension(300, 200)); // Establece el tamaño preferido de cada Item
            
            // Agrega el MouseListener al Item
            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Aquí abrimos la vista de detalles del producto
                    List<Map<String, Object>> relatedProducts = List.of(product); // Ejemplo de productos relacionados
                    Product productFrame = new Product(product, relatedProducts);
                    productFrame.setVisible(true);
                }
            });
            
            items[row][col] = item;
            itemsPanel.add(items[row][col]);
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
}
