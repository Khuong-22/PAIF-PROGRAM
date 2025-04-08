package model;

import java.util.Properties;
import java.util.Vector;
import exception.InvalidPrimaryKeyException;
import database.*;
import impresario.IView;

public class PatronCollection extends EntityBase implements IView {
    private static final String myTableName = "Patron";
    private Vector<Patron> patronList;

    // Constructor - creates an empty collection
    public PatronCollection() {
        super(myTableName);
        patronList = new Vector<Patron>();
    }

    // Find patrons with date of birth before the specified date
    public void findPatronsOlderThan(String date) {
        String query = "SELECT * FROM " + myTableName +
                " WHERE (dateOfBirth < '" + date + "')";
        populatePatronList(query);
    }

    // Find patrons with date of birth after the specified date
    public void findPatronsYoungerThan(String date) {
        String query = "SELECT * FROM " + myTableName +
                " WHERE (dateOfBirth >= '" + date + "')";
        populatePatronList(query);
    }

    // Find patrons with matching zip code
    public void findPatronsAtZipCode(String zip) {
        String query = "SELECT * FROM " + myTableName +
                " WHERE (zip = '" + zip + "')";
        populatePatronList(query);
    }

    // Find patrons with names containing the search string
    public void findPatronsWithNameLike(String name) {
        String query = "SELECT * FROM " + myTableName +
                " WHERE (name LIKE '%" + name + "%')";
        populatePatronList(query);
    }

    // Helper method to populate the patronList from a query
    private void populatePatronList(String query) {
        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        patronList = new Vector<Patron>();

        if (allDataRetrieved != null) {
            for (int cnt = 0; cnt < allDataRetrieved.size(); cnt++) {
                Properties nextPatronData = allDataRetrieved.elementAt(cnt);
                Patron patron = new Patron(nextPatronData);

                if (patron != null) {
                    patronList.add(patron);
                }
            }
        }
    }

    // Required by IView interface
    public Object getState(String key) {
        if (key.equals("Patrons"))
            return patronList;
        else
        if (key.equals("PatronList"))
            return this;
        return null;
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    // Method to get direct access to patronList
    public Vector<Patron> getPatrons() {
        return patronList;
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}