package uvg.edu.gt;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DBManager {
    public static void main(String... args) {

        final String dbUri = "neo4j://localhost";
        final String dbUser = "neo4j";
        final String dbPassword = "12345678";

        try (Driver driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))) {
            driver.verifyConnectivity();
            System.out.println("Conexión verificada satisfacotriamente.");
            try (Session session = driver.session()) {
                try (BufferedReader br = new BufferedReader(new FileReader("compradores.csv"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(",");
                        if (!values[0].equals("nombre")) {
                            String nombre = values[0];
                            String tipo = values[1];
                            session.run("CREATE (c:Comprador {nombre: $nombre, tipo: $tipo})",
                                    Map.of("nombre", nombre, "tipo", tipo));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Map<String, Boolean> productos = new HashMap<>();
                try (BufferedReader br = new BufferedReader(new FileReader("compras.csv"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(",");
                        if (!values[0].equals("comprador")) {
                            String comprador = values[0];
                            String producto = values[1];
                            if (!productos.containsKey(producto)) {
                                session.run("CREATE (p:Producto {nombre: $nombre})", Map.of("nombre", producto));
                                productos.put(producto, true);
                            }
                            session.run("MATCH (c:Comprador {nombre: $comprador}), (p:Producto {nombre: $producto}) " +
                                    "CREATE (c)-[:COMPRA]->(p)",
                                    Map.of("comprador", comprador, "producto", producto));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                session.run("MATCH (c1:Comprador)-[:COMPRA]->(p:Producto)<-[:COMPRA]-(c2:Comprador) " +
                        "WHERE c1 <> c2 " +
                        "MERGE (c1)-[:SIMILITUD {producto: p.nombre}]->(c2)");
                session.run("MATCH (c1:Comprador)-[:SIMILITUD]->(c2:Comprador)-[:COMPRA]->(p:Producto) " +
                        "WHERE NOT (c1)-[:COMPRA]->(p) " +
                        "MERGE (c1)-[:RECOMENDACION]->(p)");
                System.out.println("Data imported and queries executed successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}