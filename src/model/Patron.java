package model;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.Optional;

import exception.InvalidPrimaryKeyException;
import database.*;
import impresario.IView;
import impresario.ModelRegistry;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Patron extends EntityBase implements IView {
    private static final String myTableName = "Patron";
    protected Properties dependencies;
    private String updateStatusMessage = "";

    // Reference to the Librarian
    private Librarian myLibrarian;

    // Constructor for creating a new Patron object with empty data
    public Patron() {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();
        persistentState.setProperty("status", "Active"); // default status
    }

    // Constructor for creating a new Patron object
    public Patron(Properties props) {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();
        Enumeration allKeys = props.propertyNames();
        while (allKeys.hasMoreElements()) {
            String nextKey = (String)allKeys.nextElement();
            String nextValue = props.getProperty(nextKey);
            if (nextValue != null) {
                persistentState.setProperty(nextKey, nextValue);
            }
        }
    }

    // Constructor for retrieving an existing Patron
    public Patron(String patronId) throws InvalidPrimaryKeyException {
        super(myTableName);
        setDependencies();
        String query = "SELECT * FROM " + myTableName + " WHERE (patronId = " + patronId + ")";
        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if (allDataRetrieved != null) {
            int size = allDataRetrieved.size();
            // There should be EXACTLY one patron. More than that is an error
            if (size != 1) {
                throw new InvalidPrimaryKeyException("Multiple patrons matching id : " + patronId + " found.");
            } else {
                // copy all the retrieved data into persistent state
                Properties retrievedPatronData = allDataRetrieved.elementAt(0);
                persistentState = new Properties();

                Enumeration allKeys = retrievedPatronData.propertyNames();
                while (allKeys.hasMoreElements()) {
                    String nextKey = (String)allKeys.nextElement();
                    String nextValue = retrievedPatronData.getProperty(nextKey);
                    if (nextValue != null) {
                        persistentState.setProperty(nextKey, nextValue);
                    }
                }
            }
        } else {
            throw new InvalidPrimaryKeyException("No patron matching id : " + patronId + " found.");
        }
    }

    // Set the librarian reference
    public void setLibrarian(Librarian librarian) {
        myLibrarian = librarian;
    }

    private void setDependencies() {
        dependencies = new Properties();

        // Key-value pairs for dependencies
        dependencies.setProperty("ProcessNewPatron", "UpdateStatusMessage");
        dependencies.setProperty("PatronUpdateStatusMessage", "UpdateStatusMessage");
        dependencies.setProperty("CancelAction", "UpdateStatusMessage");
        dependencies.setProperty("ReturnToLibrarianView", "UpdateStatusMessage");
        dependencies.setProperty("InsertSuccessful", "UpdateStatusMessage");

        myRegistry.setDependencies(dependencies);
    }

    public Object getState(String key) {
        if (key.equals("UpdateStatusMessage")) {
            return updateStatusMessage;
        }
        return persistentState.getProperty(key);
    }

    public void stateChangeRequest(String key, Object value) {
        System.out.println("Patron received request: " + key);

        if (key.equals("ProcessNewPatron")) {
            processNewPatron((Properties)value);
        } else if (key.equals("CancelAction") || key.equals("ReturnToLibrarianView")) {
            // Forward to the Librarian if we have a reference
            if (myLibrarian != null) {
                System.out.println("Patron forwarding " + key + " to Librarian");
                myLibrarian.stateChangeRequest(key, value);
            } else {
                // Try to get the Librarian instance
                try {
                    Librarian librarian = Librarian.getInstance();
                    if (librarian != null) {
                        System.out.println("Patron forwarding " + key + " to Librarian singleton");
                        librarian.stateChangeRequest(key, value);
                    } else {
                        System.out.println("Cannot find Librarian reference");
                    }
                } catch (Exception e) {
                    System.out.println("Error getting Librarian: " + e.getMessage());
                }
            }
        }

        myRegistry.updateSubscribers(key, this);
    }

    // Method to process data for a new patron
    public void processNewPatron(Properties props) {
        if (props != null) {
            // Set all properties from the input
            Enumeration allKeys = props.propertyNames();
            while (allKeys.hasMoreElements()) {
                String nextKey = (String)allKeys.nextElement();
                String nextValue = props.getProperty(nextKey);

                if (nextValue != null) {
                    persistentState.setProperty(nextKey, nextValue);
                }
            }

            // Now insert into database
            update();
        }
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    // Called to update or insert the patron into the database
    public void update() {
        updateStateInDatabase();
    }

    private void updateStateInDatabase() {
        try {
            if (persistentState.getProperty("patronId") != null && !persistentState.getProperty("patronId").isEmpty()) {
                // update existing patron
                Properties whereClause = new Properties();
                whereClause.setProperty("patronId", persistentState.getProperty("patronId"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Patron data for patron ID : " + persistentState.getProperty("patronId") + " updated successfully in database!";

                // Show update success popup
                showSuccessNotification(persistentState.getProperty("name"), persistentState.getProperty("patronId"), false);

            } else {
                // insert new patron
                Integer patronId = insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("patronId", "" + patronId.intValue());
                updateStatusMessage = "Patron data for new patron : " + persistentState.getProperty("patronId") + " installed successfully in database!";

                // Show insert success popup
                showSuccessNotification(persistentState.getProperty("name"), persistentState.getProperty("patronId"), true);
            }

            // Update subscribers with THIS object, not a String
            myRegistry.updateSubscribers("PatronUpdateStatusMessage", this);

        } catch (SQLException ex) {
            updateStatusMessage = "Error in installing patron data in database! " + ex.getMessage();
            System.out.println("SQL Exception: " + ex.getMessage());

            // Update subscribers with THIS object, not a String
            myRegistry.updateSubscribers("PatronUpdateStatusMessage", this);
        }
    }

    // Show success notification popup
    private void showSuccessNotification(String patronName, String patronId, boolean isNew) {
        // Use Platform.runLater to ensure this runs on the JavaFX thread
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(isNew ? "Patron Added" : "Patron Updated");
            alert.setHeaderText(isNew ? "Patron Added Successfully" : "Patron Updated Successfully");
            alert.setContentText("Patron " + patronName + " (ID: " + patronId + ") has been successfully " +
                    (isNew ? "added to" : "updated in") + " the database.");

            // Replace OK button with a Done button
            ButtonType doneButton = new ButtonType("Done");
            alert.getButtonTypes().setAll(doneButton);

            // Show the alert and wait for response
            Optional<ButtonType> result = alert.showAndWait();

            // When Done button is clicked, return to main interface
            if (result.isPresent() && result.get() == doneButton) {
                // Notify subscribers that insertion/update was successful
                myRegistry.updateSubscribers("InsertSuccessful", this);

                // Return to main menu/librarian view
                if (myLibrarian != null) {
                    myLibrarian.stateChangeRequest("CancelAction", null);
                } else {
                    try {
                        Librarian librarian = Librarian.getInstance();
                        if (librarian != null) {
                            librarian.stateChangeRequest("CancelAction", null);
                        }
                    } catch (Exception e) {
                        System.out.println("Error getting Librarian: " + e.getMessage());
                    }
                }
            }
        });
    }

    public Vector<String> getEntryListView() {
        Vector<String> v = new Vector<String>();
        v.addElement(persistentState.getProperty("patronId"));
        v.addElement(persistentState.getProperty("name"));
        v.addElement(persistentState.getProperty("address"));
        v.addElement(persistentState.getProperty("city"));
        v.addElement(persistentState.getProperty("stateCode"));
        v.addElement(persistentState.getProperty("zip"));
        v.addElement(persistentState.getProperty("email"));
        v.addElement(persistentState.getProperty("dateOfBirth"));
        v.addElement(persistentState.getProperty("status"));
        return v;
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}