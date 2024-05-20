import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.Result;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DBManager es una clase singleton que gestiona la conexión a una base de datos
 * Neo4j
 * y proporciona métodos para interactuar con los nodos y relaciones en la base
 * de datos.
 */
public class DBManager {

    private static DBManager instance;
    private final Driver driver;
    private String loggedInUser;

    /**
     * Constructor privado que inicializa el controlador de la base de datos Neo4j.
     *
     * @param uri      URI de la base de datos Neo4j
     * @param user     Nombre de usuario para la autenticación
     * @param password Contraseña para la autenticación
     */
    private DBManager(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    /**
     * Obtiene la instancia singleton de DBManager.
     *
     * @param uri      URI de la base de datos Neo4j
     * @param user     Nombre de usuario para la autenticación
     * @param password Contraseña para la autenticación
     * @return Instancia de DBManager
     */
    public static synchronized DBManager getInstance(String uri, String user, String password) {
        if (instance == null) {
            instance = new DBManager(uri, user, password);
        }
        return instance;
    }

    /**
     * Cierra la conexión con la base de datos Neo4j.
     */
    public void close() {
        driver.close();
    }

    /**
     * Carga productos desde un archivo CSV a la base de datos Neo4j.
     *
     * @param csvFilePath Ruta del archivo CSV
     */
    public void loadProductsFromCSV(String csvFilePath) {
        try (Session session = driver.session();
                BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Saltar encabezado
                }
                String[] values = line.split(",");
                if (values.length == 7) {
                    String id = values[0];
                    String name = values[1];
                    String price = values[2];
                    String category1 = values[3];
                    String category2 = values[4];
                    String brand = values[5];
                    String image = values[6] + id + ".jpg";

                    session.run("CREATE (p:Producto {id: $id, nombre: $name, precio: $price, " +
                            "categoria1: $category1, categoria2: $category2, marca: $brand, imagen: $image})",
                            Map.of("id", id, "name", name, "price", price, "category1", category1,
                                    "category2", category2, "brand", brand, "image", image));
                }
            }
            System.out.println("Products loaded from CSV successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene todos los productos de la base de datos.
     *
     * @return Lista de productos
     */
    public List<Map<String, Object>> getAllProducts() {
        List<Map<String, Object>> products = new ArrayList<>();
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (p:Producto) RETURN p");
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                Map<String, Object> product = record.get("p").asMap();
                products.add(product);
            }
        }
        return products;
    }

    /**
     * Busca productos en la base de datos por nombre o marca.
     *
     * @param query Texto de búsqueda
     * @return Lista de productos que coinciden con la búsqueda
     */
    public List<Map<String, Object>> searchProducts(String query) {
        List<Map<String, Object>> products = new ArrayList<>();
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (p:Producto) " +
                            "WHERE p.nombre CONTAINS $query OR p.marca CONTAINS $query " +
                            "RETURN p",
                    Values.parameters("query", query));
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                Map<String, Object> product = record.get("p").asMap();
                products.add(product);
            }
        }
        return products;
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param tipo     Tipo de usuario
     */
    public void registerUser(String username, String password, String tipo) {
        try (Session session = driver.session()) {
            session.run("CREATE (u:User {username: $username, password: $password, tipo: $tipo})",
                    Map.of("username", username, "password", password, "tipo", tipo));
            System.out.println("User registered successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error registering user.");
        }
    }

    /**
     * Inicia sesión un usuario en la base de datos.
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return true si el inicio de sesión es exitoso, false en caso contrario
     */
    public boolean loginUser(String username, String password) {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (u:User {username: $username, password: $password}) RETURN u",
                    Map.of("username", username, "password", password));
            if (result.hasNext()) {
                loggedInUser = username; // Guardar el nombre de usuario como identificador de sesión
                System.out.println("Usuario logueado: " + loggedInUser);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Realiza una compra de un producto para el usuario logueado.
     *
     * @param productDetails Detalles del producto a comprar
     * @return true si la compra es exitosa, false en caso contrario
     */
    public boolean realizarCompra(Map<String, Object> productDetails) {
        System.out.println("ID del usuario logueado: " + loggedInUser);
        if (loggedInUser != null) { // Verificar si hay un usuario logueado
            try (Session session = driver.session()) {
                // Obtener el ID del producto
                String productId = productDetails.get("id").toString();

                // Crear la relación de compra en la base de datos
                session.run("MATCH (u:User {username: $username}), (p:Producto {id: $productId}) " +
                        "CREATE (u)-[:COMPRA]->(p)",
                        Map.of("username", loggedInUser, "productId", productId));

                // Obtener el tipo de usuario logueado
                String userType = session.run("MATCH (u:User {username: $username}) RETURN u.tipo AS tipo",
                        Map.of("username", loggedInUser)).single().get("tipo").asString();

                // Crear relaciones de similitud según las reglas especificadas
                String similarityQuery = "MATCH (u1:User {username: $username})-[:COMPRA]->(p:Producto {id: $productId}), (u2:User)-[:COMPRA]->(p) "
                        +
                        "WHERE u1 <> u2 " +
                        "AND (" +
                        "  (u1.tipo = 'Mayorista' AND u2.tipo IN ['Regular', 'Mayorista']) " +
                        "  OR (u1.tipo = 'Regular' AND u2.tipo IN ['Mayorista', 'Novato', 'Regular']) " +
                        "  OR (u1.tipo = 'Novato' AND u2.tipo IN ['Regular', 'Novato'])" +
                        ") " +
                        "MERGE (u1)-[:SIMILAR]->(u2) " +
                        "MERGE (u2)-[:SIMILAR]->(u1)";

                session.run(similarityQuery, Map.of("username", loggedInUser, "productId", productId));

                // Crear relaciones de RECOMENDACION para productos no comprados por cualquier
                // usuario
                String recommendationQuery = "MATCH (u1:User)-[:SIMILAR]-(u2:User)-[:COMPRA]->(p:Producto) " +
                        "WHERE NOT (u1)-[:COMPRA]->(p) " +
                        "AND (" +
                        "  (u1.tipo = 'Mayorista' AND u2.tipo = 'Regular') " +
                        "  OR (u1.tipo = 'Regular' AND u2.tipo IN ['Mayorista', 'Novato']) " +
                        "  OR (u1.tipo = 'Novato' AND u2.tipo = 'Regular')" +
                        "  OR (u1.tipo = u2.tipo) " + // Similitud entre usuarios del mismo tipo de comprador
                        ") " +
                        "MERGE (u1)<-[:RECOMENDACION]-(p)";

                session.run(recommendationQuery, Map.of("username", loggedInUser));

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Obtiene los productos recomendados para un usuario específico.
     *
     * @param username Nombre de usuario
     * @return Lista de productos recomendados
     */
    public List<Map<String, Object>> getRecommendedProducts(String username) {
        List<Map<String, Object>> recommendedProducts = new ArrayList<>();
        try (Session session = driver.session()) {
            String query = "MATCH (u1:User {username: $username})-[:SIMILAR]-(u2:User)-[:COMPRA]->(p:Producto) " +
                    "WHERE NOT (u1)-[:COMPRA]->(p) " +
                    "RETURN DISTINCT p";
            Result result = session.run(query, Map.of("username", username));
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                Map<String, Object> product = record.get("p").asMap();
                recommendedProducts.add(product);
            }
        }
        return recommendedProducts;
    }

    /**
     * Obtiene el nombre de usuario del usuario actualmente logueado.
     *
     * @return Nombre de usuario del usuario logueado
     */
    public String getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Método principal para ejecutar ejemplos de operaciones con la base de datos.
     *
     * @param args Argumentos de la línea de comandos
     */
    public static void main(String... args) {
        String dbUri = "neo4j://localhost";
        String dbUser = "neo4j";
        String dbPassword = "12345678";

        // Crear una instancia de DBManager usando el método estático getInstance
        DBManager dbManager = DBManager.getInstance(dbUri, dbUser, dbPassword);

        dbManager.loadProductsFromCSV("Productos.csv");

        boolean loginSuccess = dbManager.loginUser("newuser", "password123");
        System.out.println("Login successful: " + loginSuccess);

        // Agregar un shutdown hook para cerrar el DBManager cuando la JVM se apague
        Runtime.getRuntime().addShutdownHook(new Thread(dbManager::close));
    }
}