
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.sql.Connection;
import database.JDBCBroker;
import model.Librarian;
import userinterface.MainStageContainer;

/**
 * Main application class for the Library System
 */
public class Main extends Application {
    public JDBCBroker jdbcBroker;



    @Override
    public void start(Stage primaryStage) {
        System.out.println("Library System Starting...");

        try {
            // Initialize database connection
            System.out.println("Connecting to database...");
            jdbcBroker = JDBCBroker.getInstance();
            Connection connection = jdbcBroker.getConnection();

            if (connection != null) {
                System.out.println("Database connection established successfully");

                // Create the main stage container
                System.out.println("Setting up main window...");
                MainStageContainer.setStage(primaryStage, "Library System");

                // Set up event handling for window close
                primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        System.out.println("Shutting down library system");
                        System.exit(0);
                    }
                });

                // Create the Librarian object which will start the UI
                System.out.println("Starting library system...");
                var myLibrarian = new Librarian();
            } else {
                System.out.println("ERROR: Failed to connect to database");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void stop() {
        System.out.println("Library System Shutting Down...");
        // Add any cleanup code here if needed
    }
}