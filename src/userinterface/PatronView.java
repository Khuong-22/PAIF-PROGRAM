package userinterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Properties;

import impresario.IModel;

public class PatronView extends View {
    // GUI components
    private TextField nameField;
    private TextField addressField;
    private TextField cityField;
    private TextField stateCodeField;
    private TextField zipField;
    private TextField emailField;
    private TextField dateOfBirthField;
    private ComboBox<String> statusComboBox;

    // For showing error message
    private MessageView statusLog;

    // Constructor
    public PatronView(IModel model) {
        super(model, "PatronView");

        // Create a container for showing the contents
        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        // Create our GUI components, add them to this panel
        container.getChildren().add(createTitle("Patron Information"));
        container.getChildren().add(createFormContents());

        // Error message area
        container.getChildren().add(createStatusLog());

        getChildren().add(container);
    }

    // Create the main form contents - using a different name to avoid override conflicts
    private VBox createFormContents() {
        VBox vbox = new VBox(10);

        GridPane grid = createFormContent(); // Call the parent's createFormContent() method

        // Name
        Label nameLabel = new Label("Name:");
        grid.add(nameLabel, 0, 0);

        nameField = new TextField();
        nameField.setMinWidth(200);
        grid.add(nameField, 1, 0);

        // Address
        Label addressLabel = new Label("Address:");
        grid.add(addressLabel, 0, 1);

        addressField = new TextField();
        addressField.setMinWidth(200);
        grid.add(addressField, 1, 1);

        // City
        Label cityLabel = new Label("City:");
        grid.add(cityLabel, 0, 2);

        cityField = new TextField();
        cityField.setMinWidth(200);
        grid.add(cityField, 1, 2);

        // State Code
        Label stateCodeLabel = new Label("State Code:");
        grid.add(stateCodeLabel, 0, 3);

        stateCodeField = new TextField();
        stateCodeField.setMinWidth(200);
        grid.add(stateCodeField, 1, 3);

        // Zip
        Label zipLabel = new Label("Zip Code:");
        grid.add(zipLabel, 0, 4);

        zipField = new TextField();
        zipField.setMinWidth(200);
        grid.add(zipField, 1, 4);

        // Email
        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 5);

        emailField = new TextField();
        emailField.setMinWidth(200);
        grid.add(emailField, 1, 5);

        // Date of Birth
        Label dobLabel = new Label("Date of Birth (YYYY-MM-DD):");
        grid.add(dobLabel, 0, 6);

        dateOfBirthField = new TextField();
        dateOfBirthField.setMinWidth(200);
        grid.add(dateOfBirthField, 1, 6);

        // Status ComboBox
        Label statusLabel = new Label("Status:");
        grid.add(statusLabel, 0, 7);

        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Active", "Inactive");
        statusComboBox.setValue("Active"); // Default value
        statusComboBox.setMinWidth(200);
        grid.add(statusComboBox, 1, 7);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        var submitButton = new Button("Submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processPatronSubmit();
            }
        });

        var doneButton = new Button("Done");
        doneButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                clearErrorMessage();
                myModel.stateChangeRequest("ReturnToLibrarianView", null);
            }
        });

        buttonBox.getChildren().add(submitButton);
        buttonBox.getChildren().add(doneButton);

        vbox.getChildren().add(grid);
        vbox.getChildren().add(buttonBox);

        return vbox;
    }

    // Create the status log field
    private MessageView createStatusLog() {
        statusLog = new MessageView("");
        return statusLog;
    }

    // Process patron submission
    private void processPatronSubmit() {
        clearErrorMessage();

        String name = nameField.getText();
        String address = addressField.getText();
        String city = cityField.getText();
        String stateCode = stateCodeField.getText();
        String zip = zipField.getText();
        String email = emailField.getText();
        String dateOfBirth = dateOfBirthField.getText();
        String status = statusComboBox.getValue();

        // Validation
        if (name.isEmpty()) {
            displayErrorMessage("Name cannot be empty");
            return;
        }

        if (address.isEmpty()) {
            displayErrorMessage("Address cannot be empty");
            return;
        }

        if (city.isEmpty()) {
            displayErrorMessage("City cannot be empty");
            return;
        }

        if (stateCode.isEmpty()) {
            displayErrorMessage("State Code cannot be empty");
            return;
        }

        if (zip.isEmpty()) {
            displayErrorMessage("Zip Code cannot be empty");
            return;
        }

        if (email.isEmpty()) {
            displayErrorMessage("Email cannot be empty");
            return;
        }

        if (dateOfBirth.isEmpty()) {
            displayErrorMessage("Date of Birth cannot be empty");
            return;
        }

        // Validate date of birth (should be between 1920-01-01 and 2006-01-01)
        if (dateOfBirth.compareTo("1920-01-01") < 0 || dateOfBirth.compareTo("2006-01-01") > 0) {
            displayErrorMessage("Date of Birth must be between 1920-01-01 and 2006-01-01");
            return;
        }

        // Create properties object with patron data
        Properties patronData = new Properties();
        patronData.setProperty("name", name);
        patronData.setProperty("address", address);
        patronData.setProperty("city", city);
        patronData.setProperty("stateCode", stateCode);
        patronData.setProperty("zip", zip);
        patronData.setProperty("email", email);
        patronData.setProperty("dateOfBirth", dateOfBirth);
        patronData.setProperty("status", status);

        // Send data to model
        myModel.stateChangeRequest("ProcessNewPatron", patronData);
    }

    // Required by interface
    public void updateState(String key, Object value) {
        if (key.equals("PatronUpdateStatusMessage")) {
            displayMessage((String)value);
        }
    }

    // Display error message
    public void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    // Display message
    public void displayMessage(String message) {
        statusLog.displayMessage(message);
    }

    // Clear error message
    public void clearErrorMessage() {
        statusLog.clearErrorMessage();
    }
}