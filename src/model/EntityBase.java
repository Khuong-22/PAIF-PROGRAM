package model;

import java.util.Properties;
import java.util.Vector;

import database.Persistable;
import event.Event;
import impresario.ModelRegistry;
import impresario.IModel;
import impresario.IView;

public abstract class EntityBase extends Persistable implements IModel {
	protected ModelRegistry myRegistry;	// registry for entities interested in our events
	protected Properties persistentState;	// the field names and values from the database
	protected Properties mySchema;        // schema info about the table
	private String myTableName;          // The name of our database table

	// constructor for this class
	protected EntityBase(String tablename) {
		super();
		// save our table name for later
		myTableName = tablename;

		// create a place to hold our state from the database
		persistentState = new Properties();

		// create a new registry for subscribers
		myRegistry = new ModelRegistry("EntityBase." + tablename);

		// extract the schema from the database, calls methods in subclasses
		initializeSchema(tablename);
	}

	// Initialize the schema for the database table
	protected abstract void initializeSchema(String tableName);

	// Get a value from our persistent state by key
	public abstract Object getState(String key);

	// Handle state change requests
	public abstract void stateChangeRequest(String key, Object value);

	/** Register objects to receive state updates. */
	public void subscribe(String key, IView subscriber) {
		myRegistry.subscribe(key, subscriber);
	}

	/** Unregister previously registered objects. */
	public void unSubscribe(String key, IView subscriber) {
		myRegistry.unSubscribe(key, subscriber);
	}

	/** Update all subscribers */
	protected void stateChanged(String key) {
		myRegistry.updateSubscribers(key, this);
	}

	// Utility method to ensure required fields have values
	protected String validateRequiredFields(Properties props, Vector<String> requiredFields) {
		StringBuilder missingFields = new StringBuilder();

		for (String field : requiredFields) {
			String value = props.getProperty(field);
			if (value == null || value.trim().isEmpty()) {
				missingFields.append(field).append(", ");
			}
		}

		if (missingFields.length() > 0) {
			return "Required fields missing: " +
					missingFields.substring(0, missingFields.length() - 2);
		}
		return null;
	}

	// Method to create a Properties object from a vector of field names and values
	protected Properties createProperties(Vector<String> fieldNames, Vector<String> fieldValues) {
		Properties props = new Properties();

		if (fieldNames.size() != fieldValues.size()) {
			new Event(Event.getLeafLevelClassName(this), "createProperties",
					"Number of field names and values don't match", Event.ERROR);
			return null;
		}

		for (int i = 0; i < fieldNames.size(); i++) {
			props.setProperty(fieldNames.elementAt(i), fieldValues.elementAt(i));
		}

		return props;
	}

	// Get all property names from the schema
	protected Vector<String> getSchemaFields() {
		Vector<String> fields = new Vector<String>();
		if (mySchema != null) {
			for (String key : mySchema.stringPropertyNames()) {
				if (!key.equals("TableName")) {
					fields.add(key);
				}
			}
		}
		return fields;
	}

	// Get the name of our table
	protected String getTableName() {
		return myTableName;
	}

	// Set the name of our table
	protected void setTableName(String tablename) {
		myTableName = tablename;
	}

	// Check if a field exists in the schema
	protected boolean schemaContainsField(String fieldName) {
		return mySchema != null && mySchema.containsKey(fieldName);
	}

	// Get the type of field from the schema
	protected String getFieldType(String fieldName) {
		if (mySchema != null) {
			return mySchema.getProperty(fieldName);
		}

		return null;
	}
}