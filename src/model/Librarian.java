package model;

import event.Event;
import impresario.IModel;
import impresario.IView;
import impresario.ModelRegistry;
import userinterface.MainStageContainer;
import userinterface.View;
import userinterface.ViewFactory;
import userinterface.WindowPosition;

import javafx.stage.Stage;
import javafx.scene.Scene;
import java.util.Properties;
import java.util.Vector;

public class Librarian implements IModel {
    // Main Stage for the GUI
    private Stage myStage;

    // Current view being displayed
    private Scene currentScene;

    // Book and Patron objects
    private Book myBook;
    private Patron myPatron;

    // Model registry for subscriber-publisher pattern
    private ModelRegistry myRegistry;

    // Singleton instance
    private static Librarian instance;

    // Constructor
    public Librarian() {
        myStage = MainStageContainer.getInstance();
        myRegistry = new ModelRegistry("Librarian");

        // Set up the singleton instance
        instance = this;

        // Register for CancelAction events
        setDependencies();

        // Show the initial view
        createAndShowLibrarianView();
    }

    // Get the singleton instance
    public static Librarian getInstance() {
        if (instance == null) {
            instance = new Librarian();
        }
        return instance;
    }

    // Set up dependencies
    private void setDependencies() {
        Properties dependencies = new Properties();
        dependencies.setProperty("CancelAction", "UpdateStatusMessage");
        dependencies.setProperty("ReturnToLibrarianView", "UpdateStatusMessage");
        myRegistry.setDependencies(dependencies);
    }

    // Method to display the main librarian view
    public void createAndShowLibrarianView() {
        try {
            System.out.println("Creating LibrarianView");

            // Create the view
            View newView = ViewFactory.createView("LibrarianView", this);
            currentScene = new Scene(newView);

            // Set the scene on the stage
            myStage.setScene(currentScene);
            myStage.sizeToScene();

            // Place the stage in the center of the screen
            WindowPosition.placeCenter(myStage);

            // Show the stage
            myStage.show();
        } catch (Exception e) {
            System.out.println("Error in creating LibrarianView: " + e.getMessage());
            e.printStackTrace();
            new Event(Event.getLeafLevelClassName(this), "createAndShowLibrarianView",
                    "Error in creating LibrarianView: " + e.getMessage(), Event.ERROR);
        }
    }

    // Method to create a new book
    public void createNewBook() {
        try {
            System.out.println("Creating BookView");

            // Create an empty Book object
            myBook = new Book();

            // Make the Book aware of this Librarian to handle returns
            myBook.setLibrarian(this);

            // Display the book view for data entry
            View newView = ViewFactory.createView("InsertBookView", myBook);
            currentScene = new Scene(newView);

            // Set the scene on the stage
            myStage.setScene(currentScene);
            myStage.sizeToScene();

            // Place the stage in the center of the screen
            WindowPosition.placeCenter(myStage);
        } catch (Exception e) {
            System.out.println("Error in creating BookView: " + e.getMessage());
            e.printStackTrace();
            new Event(Event.getLeafLevelClassName(this), "createNewBook",
                    "Error in creating BookView: " + e.getMessage(), Event.ERROR);
        }
    }

    // Method to create a new patron
    public void createNewPatron() {
        try {
            System.out.println("Creating PatronView");

            // Create an empty Patron object
            myPatron = new Patron();

            // Make the Patron aware of this Librarian to handle returns
            myPatron.setLibrarian(this);

            // Display the patron view for data entry
            View newView = ViewFactory.createView("PatronView", myPatron);
            currentScene = new Scene(newView);

            // Set the scene on the stage
            myStage.setScene(currentScene);
            myStage.sizeToScene();

            // Place the stage in the center of the screen
            WindowPosition.placeCenter(myStage);
        } catch (Exception e) {
            System.out.println("Error in creating PatronView: " + e.getMessage());
            e.printStackTrace();
            new Event(Event.getLeafLevelClassName(this), "createNewPatron",
                    "Error in creating PatronView: " + e.getMessage(), Event.ERROR);
        }
    }

    // Method to handle search books
    public void searchBooks() {
        try {
            System.out.println("Creating SearchBooksView");

            // Display the search books view
            View newView = ViewFactory.createView("SearchBooksView", this);
            currentScene = new Scene(newView);

            // Set the scene on the stage
            myStage.setScene(currentScene);
            myStage.sizeToScene();

            // Place the stage in the center of the screen
            WindowPosition.placeCenter(myStage);
        } catch (Exception e) {
            System.out.println("Error in creating SearchBooksView: " + e.getMessage());
            e.printStackTrace();
            new Event(Event.getLeafLevelClassName(this), "searchBooks",
                    "Error in creating SearchBooksView: " + e.getMessage(), Event.ERROR);
        }
    }

    // Method to handle search patrons
    public void searchPatrons() {
        try {
            System.out.println("Creating SearchPatronsView");

            // Display the search patrons view
            View newView = ViewFactory.createView("SearchPatronsView", this);
            currentScene = new Scene(newView);

            // Set the scene on the stage
            myStage.setScene(currentScene);
            myStage.sizeToScene();

            // Place the stage in the center of the screen
            WindowPosition.placeCenter(myStage);
        } catch (Exception e) {
            System.out.println("Error in creating SearchPatronsView: " + e.getMessage());
            e.printStackTrace();
            new Event(Event.getLeafLevelClassName(this), "searchPatrons",
                    "Error in creating SearchPatronsView: " + e.getMessage(), Event.ERROR);
        }
    }

    // Method to handle done (exit) request
    public void done() {
        System.exit(0);
    }

    // Required by IModel interface - Subscriber-Publisher Pattern
    @Override
    public void subscribe(String key, IView subscriber) {
        myRegistry.subscribe(key, subscriber);
    }

    @Override
    public void unSubscribe(String key, IView subscriber) {
        myRegistry.unSubscribe(key, subscriber);
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        System.out.println("Librarian received request: " + key);

        if (key.equals("InsertBook")) {
            createNewBook();
        } else if (key.equals("InsertPatron")) {
            createNewPatron();
        } else if (key.equals("SearchBooks")) {
            searchBooks();
        } else if (key.equals("SearchPatrons")) {
            searchPatrons();
        } else if (key.equals("Done")) {
            done();
        } else if (key.equals("CancelAction") || key.equals("ReturnToLibrarianView")) {
            System.out.println("Returning to LibrarianView");
            createAndShowLibrarianView();
        }

        myRegistry.updateSubscribers(key, this);
    }

    @Override
    public Object getState(String key) {
        // No state to return in this class, but required by interface
        return null;
    }
}