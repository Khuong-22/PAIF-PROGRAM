package model;

import event.Event;
import impresario.IModel;
import impresario.IView;
import impresario.ModelRegistry;

import java.util.Properties;
import java.util.Vector;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Book implements IModel {
    // Common state
    private ModelRegistry myRegistry;
    private Properties persistentState;
    private Librarian myLibrarian;

    // Constructor for new book
    public Book() {
        myRegistry = new ModelRegistry("Book");
        persistentState = new Properties();
        persistentState.setProperty("bookId", ""); // Will be set when saved to database
        persistentState.setProperty("bookTitle", "");
        persistentState.setProperty("author", "");
        persistentState.setProperty("pubYear", "");
        persistentState.setProperty("status", "Active");

        setDependencies();
    }

    // Constructor for existing book
    public Book(Properties props) {
        myRegistry = new ModelRegistry("Book");
        persistentState = props;

        setDependencies();
    }

    // Set dependencies
    private void setDependencies() {
        Properties dependencies = new Properties();
        dependencies.setProperty("ProcessNewBook", "BookUpdateStatusMessage");
        dependencies.setProperty("InsertSuccessful", "");
        dependencies.setProperty("CancelAction", "");
        myRegistry.setDependencies(dependencies);
    }

    // Set the librarian reference
    public void setLibrarian(Librarian lib) {
        myLibrarian = lib;
    }

    // Method to update the book in database
    public void update() {
        // Code to insert/update book in database would go here

        // For now, simulate generating an ID
        if (persistentState.getProperty("bookId").equals("")) {
            // New book, generate an ID
            int randomId = (int)(Math.random() * 10000) + 1000;
            persistentState.setProperty("bookId", String.valueOf(randomId));
        }

        // Get the book data for the popup
        String bookId = persistentState.getProperty("bookId");
        String bookTitle = persistentState.getProperty("bookTitle");

        // Show success notification
        showSuccessNotification(bookTitle, bookId);

        // Return confirmation message for the status log
        String message = "Book saved successfully! Book ID: " + bookId;
        stateChangeRequest("BookUpdateStatusMessage", message);
    }

    // Show success notification popup
    private void showSuccessNotification(String bookTitle, String bookId) {
        // Use Platform.runLater to ensure this runs on the JavaFX thread
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Book Added");
            alert.setHeaderText("Book Added Successfully");
            alert.setContentText("Book \"" + bookTitle + "\" (ID: " + bookId + ") has been successfully added to the database.");

            // Replace OK button with a Done button
            ButtonType doneButton = new ButtonType("Done");
            alert.getButtonTypes().setAll(doneButton);

            // Show the alert and wait for response
            Optional<ButtonType> result = alert.showAndWait();

            // When Done button is clicked, return to main interface
            if (result.isPresent() && result.get() == doneButton) {
                // Return to main menu
                stateChangeRequest("InsertSuccessful", null);

                // If a librarian reference exists, return to the main screen
                if (myLibrarian != null) {
                    myLibrarian.stateChangeRequest("CancelAction", null);
                }
            }
        });
    }

    // Get a vector of the data for display in a table/list view
    public Vector<String> getEntryListView() {
        Vector<String> entryList = new Vector<String>();

        entryList.add(persistentState.getProperty("bookId"));
        entryList.add(persistentState.getProperty("bookTitle"));
        entryList.add(persistentState.getProperty("author"));
        entryList.add(persistentState.getProperty("pubYear"));
        entryList.add(persistentState.getProperty("status"));

        return entryList;
    }

    // Required by IModel interface
    public void subscribe(String key, IView subscriber) {
        myRegistry.subscribe(key, subscriber);
    }

    public void unSubscribe(String key, IView subscriber) {
        myRegistry.unSubscribe(key, subscriber);
    }

    public void stateChangeRequest(String key, Object value) {
        if (key.equals("ProcessNewBook")) {
            // Extract properties from the passed value
            Properties props = (Properties)value;
            persistentState.setProperty("bookTitle", props.getProperty("bookTitle"));
            persistentState.setProperty("author", props.getProperty("author"));
            persistentState.setProperty("pubYear", props.getProperty("pubYear"));
            persistentState.setProperty("status", props.getProperty("status"));

            // Update the book
            update();
        }
        else if (key.equals("CancelAction")) {
            if (myLibrarian != null) {
                myLibrarian.stateChangeRequest("CancelAction", null);
            }
        }

        myRegistry.updateSubscribers(key, this);
    }

    public Object getState(String key) {
        return persistentState.getProperty(key);
    }
}