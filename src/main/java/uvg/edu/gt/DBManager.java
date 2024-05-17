package uvg.edu.gt;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.MapAccessor;
import org.neo4j.driver.Result;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {

    private static DBManager instance;
    private final Driver driver;
    private String loggedInUser;

    public DBManager(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public static synchronized DBManager getInstance(String uri, String user, String password) {
        if (instance == null) {
            instance = new DBManager(uri, user, password);
        }
        return instance;
    }

    public void close() {
        driver.close();
    }

    public void loadProductsFromCSV(String csvFilePath) {
        try (Session session = driver.session();
                BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Saltar Encabezado
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
                String similarityQuery =
                        "MATCH (u1:User {username: $username})-[:COMPRA]->(p:Producto {id: $productId}), (u2:User)-[:COMPRA]->(p) " +
                        "WHERE u1 <> u2 " +
                        "AND (" +
                        "  (u1.tipo = 'Mayorista' AND u2.tipo IN ['Regular', 'Mayorista']) " +
                        "  OR (u1.tipo = 'Regular' AND u2.tipo IN ['Mayorista', 'Novato', 'Regular']) " +
                        "  OR (u1.tipo = 'Novato' AND u2.tipo IN ['Regular', 'Novato'])" +
                        ") " +
                        "MERGE (u1)-[:SIMILAR]->(u2) " +
                        "MERGE (u2)-[:SIMILAR]->(u1)";
    
                session.run(similarityQuery, Map.of("username", loggedInUser, "productId", productId));
    
                // Crear relaciones de RECOMENDACION para productos no comprados por cualquier usuario
                String recommendationQuery =
                        "MATCH (u1:User)-[:SIMILAR]-(u2:User)-[:COMPRA]->(p:Producto) " +
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

    public String getLoggedInUser() {
        return loggedInUser;
    }

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