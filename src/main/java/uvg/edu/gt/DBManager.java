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
                Values.parameters("query", query)
            );
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