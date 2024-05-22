import java.awt.EventQueue;
import java.util.Scanner;

/**
 * Clase principal MainApp que inicia la aplicación de e-commerce.
 */
public class MainApp {

    /**
     * Método principal que inicia la aplicación.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Preguntar al usuario si es la primera vez que ejecuta el programa
        System.out.println("¿Es primera vez que ejecuta el Programa?");
        System.out.println("¿Desea cargar todos los datos a la base de datos? (s/n)");
        String respuesta = scanner.nextLine();

        if (respuesta.equalsIgnoreCase("s")) {
            String dbUri = "neo4j://localhost";
            String dbUser = "neo4j";
            String dbPassword = "12345678";

            // Crear una instancia de DBManager y cargar los datos
            DBManager dbManager = DBManager.getInstance(dbUri, dbUser, dbPassword);
            dbManager.main(args);
        }

        // Continuar con la creación y visualización de la ventana de Login
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login loginFrame = new Login();
                    loginFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
