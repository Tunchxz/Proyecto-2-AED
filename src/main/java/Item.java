import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;

/**
 * Clase Item que representa un componente visual para mostrar información de un producto.
 * Extiende de JPanel.
 */
public class Item extends JPanel {
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel category1Label;
    private JLabel category2Label;
    private JLabel brandLabel;
    private JLabel imageLabel;

    /**
     * Constructor de la clase Item.
     * @param id Identificador del producto.
     * @param nombre Nombre del producto.
     * @param precio Precio del producto.
     * @param categoria1 Primera categoría del producto.
     * @param categoria2 Segunda categoría del producto.
     * @param marca Marca del producto.
     * @param imagen Ruta de la imagen del producto.
     */
    public Item(String id, String nombre, double precio, String categoria1, String categoria2, String marca, String imagen) {
        setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        setBackground(new Color(255, 255, 255));
        initialize(id, nombre, precio, categoria1, categoria2, marca, imagen);
    }

    /**
     * Inicializa los componentes del panel Item con la información del producto.
     * @param id Identificador del producto.
     * @param nombre Nombre del producto.
     * @param precio Precio del producto.
     * @param categoria1 Primera categoría del producto.
     * @param categoria2 Segunda categoría del producto.
     * @param marca Marca del producto.
     * @param imagen Ruta de la imagen del producto.
     */
    private void initialize(String id, String nombre, double precio, String categoria1, String categoria2, String marca, String imagen) {
        setLayout(new BorderLayout());

        // Creamos los JLabels para mostrar la información del producto
        nameLabel = new JLabel(nombre);
        nameLabel.setBackground(new Color(255, 255, 255));
        nameLabel.setForeground(new Color(0, 0, 0));
        nameLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        priceLabel = new JLabel("Precio: " + precio);
        priceLabel.setForeground(new Color(0, 0, 0));
        priceLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        category1Label = new JLabel("Categoría 1: " + categoria1);
        category2Label = new JLabel("Categoría 2: " + categoria2);
        brandLabel = new JLabel("Marca: " + marca);
        imageLabel = new JLabel();

        // Configuramos el diseño de los JLabels
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        category1Label.setHorizontalAlignment(SwingConstants.CENTER);
        category2Label.setHorizontalAlignment(SwingConstants.CENTER);
        brandLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Agregamos los JLabels al panel
        add(nameLabel, BorderLayout.NORTH);
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.add(category1Label);
        infoPanel.add(category2Label);
        infoPanel.add(brandLabel);
        add(infoPanel, BorderLayout.SOUTH);
        add(priceLabel, BorderLayout.SOUTH);
        add(imageLabel, BorderLayout.CENTER);

        // Establecemos la imagen
        setImage(imagen);
    }

    /**
     * Establece la imagen del producto en el JLabel correspondiente.
     * @param imagePath Ruta de la imagen del producto.
     */
    private void setImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon imageIcon = new ImageIcon(imagePath);
            // Escalamos la imagen para que se ajuste al tamaño del JLabel
            Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        } else {
            imageLabel.setIcon(null);
        }
    }

    /**
     * Actualiza la información del producto mostrado en el Item.
     * @param id Identificador del producto.
     * @param nombre Nombre del producto.
     * @param precio Precio del producto.
     * @param categoria1 Primera categoría del producto.
     * @param categoria2 Segunda categoría del producto.
     * @param marca Marca del producto.
     * @param imagen Ruta de la imagen del producto.
     */
    public void setItemInfo(String id, String nombre, double precio, String categoria1, String categoria2, String marca, String imagen) {
        nameLabel.setText("Nombre: " + nombre);
        priceLabel.setText("Precio: " + precio);
        category1Label.setText("Categoría 1: " + categoria1);
        category2Label.setText("Categoría 2: " + categoria2);
        brandLabel.setText("Marca: " + marca);
        String path = imagen + id + ".jpg";
        System.out.println(path);
        imageLabel.setText(path);
        setImage(imagen);
    }
}
