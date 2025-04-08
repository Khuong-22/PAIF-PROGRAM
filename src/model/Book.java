package model;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import exception.InvalidPrimaryKeyException;
import impresario.IView;

public class Book extends EntityBase implements IView {
    private static final String myTableName = "Book";
    protected Properties dependencies;
    private String updateStatusMessage = "";

    // Reference to the Librarian
    private Librarian myLibrarian;

    // Constructor for creating a new Book object with empty data
    public Book() {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();
        persistentState.setProperty("status", "Active"); // default status
    }

    // Constructor for creating a new Book object
    public Book(Properties props) {
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

    // Constructor for retrieving an existing Book
    public Book(String bookId) throws InvalidPrimaryKeyException {
        super(myTableName);
        setDependencies();
        String query = "SELECT * FROM " + myTableName + " WHERE (bookId = " + bookId + ")";
        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if (allDataRetrieved != null) {
            int size = allDataRetrieved.size();
            // There should be EXACTLY one book. More than that is an error
            if (size != 1) {
                throw new InvalidPrimaryKeyException("Multiple books matching id : " + bookId + " found.");
            } else {
                // copy all the retrieved data into persistent state
                Properties retrievedBookData = allDataRetrieved.elementAt(0);
                persistentState = new Properties();

                Enumeration allKeys = retrievedBookData.propertyNames();
                while (allKeys.hasMoreElements()) {
                    String nextKey = (String)allKeys.nextElement();
                    String nextValue = retrievedBookData.getProperty(nextKey);
                    if (nextValue != null) {
                        persistentState.setProperty(nextKey, nextValue);
                    }
                }
            }
        } else {
            throw new InvalidPrimaryKeyException("No book matching id : " + bookId + " found.");
        }
    }

    // Set the librarian reference
    public void setLibrarian(Librarian librarian) {
        myLibrarian = librarian;
    }

    private void setDependencies() {
        dependencies = new Properties();

        // Key-value pairs for dependencies
        dependencies.setProperty("ProcessNewBook", "UpdateStatusMessage");
        dependencies.setProperty("BookUpdateStatusMessage", "UpdateStatusMessage");
        dependencies.setProperty("CancelAction", "UpdateStatusMessage");
        dependencies.setProperty("ReturnToLibrarianView", "UpdateStatusMessage");

        myRegistry.setDependencies(dependencies);
    }

    public Object getState(String key) {
        if (key.equals("UpdateStatusMessage")) {
            return updateStatusMessage;
        }
        return persistentState.getProperty(key);
    }

    public void stateChangeRequest(String key, Object value) {
        System.out.println("Book received request: " + key);

        if (key.equals("ProcessNewBook")) {
            processNewBook((Properties)value);
        } else if (key.equals("CancelAction") || key.equals("ReturnToLibrarianView")) {
            // Forward to the Librarian if we have a reference
            if (myLibrarian != null) {
                System.out.println("Book forwarding " + key + " to Librarian");
                myLibrarian.stateChangeRequest(key, value);
            } else {
                // Try to get the Librarian instance
                try {
                    Librarian librarian = Librarian.getInstance();
                    if (librarian != null) {
                        System.out.println("Book forwarding " + key + " to Librarian singleton");
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

    // Method to process data for a new book
    public void processNewBook(Properties props) {
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

    // Called to update or insert the book into the database
    public void update() {
        updateStateInDatabase();
    }

    private void updateStateInDatabase() {
        try {
            if (persistentState.getProperty("bookId") != null) {
                // update existing book
                Properties whereClause = new Properties();
                whereClause.setProperty("bookId", persistentState.getProperty("bookId"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Book data for book ID : " + persistentState.getProperty("bookId") + " updated successfully in database!";
            } else {
                // insert new book
                Integer bookId = insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("bookId", "" + bookId.intValue());
                updateStatusMessage = "Book data for new book : " + persistentState.getProperty("bookId") + " installed successfully in database!";
            }

            // Update subscribers with THIS object, not a String
            myRegistry.updateSubscribers("BookUpdateStatusMessage", this);
        } catch (SQLException ex) {
            updateStatusMessage = "Error in installing book data in database!";

            // Update subscribers with THIS object, not a String
            myRegistry.updateSubscribers("BookUpdateStatusMessage", this);
        }
    }

    public Vector<String> getEntryListView() {
        Vector<String> v = new Vector<String>();
        v.addElement(persistentState.getProperty("bookId"));
        v.addElement(persistentState.getProperty("bookTitle"));
        v.addElement(persistentState.getProperty("author"));
        v.addElement(persistentState.getProperty("pubYear"));
        v.addElement(persistentState.getProperty("status"));
        return v;
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}