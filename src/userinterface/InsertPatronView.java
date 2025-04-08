package userinterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Properties;

import impresario.IModel;
import model.Patron;

public class InsertPatronView extends View {
    // GUI components
    private TextField nameField;
    private TextField addressField;
    private TextField cityField;
    private TextField stateCodeField;
    private TextField zipField;
    private TextField emailField;
    private TextField dateOfBirthField;

    private Button submitButton;
    private Button doneButton;

    // For showing error message
    private MessageView statusLog;

    // Flag to track if patron was successfully submitted
    private boolean patronSubmitted = false;

    // Constructor
    public InsertPatronView(IModel model) {
        super(model, "InsertPatronView");

        // Create a container for showing the contents
        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        // Create our GUI components, add them to this panel
        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContents());

        // Error message area
        container.getChildren().add(createStatusLog(""));

        getChildren().add(container);
    }

    // Create the title container
    private Node createTitle() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        Text titleText = new Text("Insert New Patron");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setWrappingWidth(300);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.BLACK);
        container.getChildren().add(titleText);

        return container;
    }

    // Create the main form content
    private VBox createFormContents() {
        VBox vbox = new VBox(10);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

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
        Label dobLabel = new Label("Date of Birth:");
        grid.add(dobLabel, 0, 6);

        dateOfBirthField = new TextField();
        dateOfBirthField.setMinWidth(200);
        grid.add(dateOfBirthField, 1, 6);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        submitButton = new Button("Submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processPatronSubmit();
            }
        });

        doneButton = new Button("Done");
        doneButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processCancel();
            }
        });

        buttonBox.getChildren().add(submitButton);
        buttonBox.getChildren().add(doneButton);

        vbox.getChildren().add(grid);
        vbox.getChildren().add(buttonBox);

        return vbox;
    }

    // Create the status log field
    private MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
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

        if (name.length() == 0 || address.length() == 0 || city.length() == 0 ||
                stateCode.length() == 0 || zip.length() == 0 || email.length() == 0 ||
                dateOfBirth.length() == 0) {
            displayErrorMessage("Please fill in all fields");
            return;
        }

        try {
            // Validate date of birth (should be between 1920-01-01 and 2006-01-01)
            if (dateOfBirth.compareTo("1920-01-01") < 0 || dateOfBirth.compareTo("2006-01-01") > 0) {
                displayErrorMessage("Date of Birth must be between 1920-01-01 and 2006-01-01");
                return;
            }

            // Create properties object for new patron
            Properties props = new Properties();
            props.setProperty("name", name);
            props.setProperty("address", address);
            props.setProperty("city", city);
            props.setProperty("stateCode", stateCode);
            props.setProperty("zip", zip);
            props.setProperty("email", email);
            props.setProperty("dateOfBirth", dateOfBirth);
            props.setProperty("status", "Active");  // Default status for new patrons

            // Create and save the patron
            Patron newPatron = new Patron(props);
            newPatron.update();

            // Set flag that patron was submitted successfully
            patronSubmitted = true;

            // Display success message
            displayMessage("Patron added successfully! Patron ID: " + newPatron.getState("patronId") +
                    "\nClick 'Done' to return to main menu.");

            // Clear the fields for next entry
            nameField.clear();
            addressField.clear();
            cityField.clear();
            stateCodeField.clear();
            zipField.clear();
            emailField.clear();
            dateOfBirthField.clear();

        } catch (Exception e) {
            displayErrorMessage("Error adding patron: " + e.getMessage());
        }
    }

    // Process cancel request
    private void processCancel() {
        clearErrorMessage();
        myModel.stateChangeRequest("CancelAction", null);
    }

    // Required by interface
    public void updateState(String key, Object value) {
        // Nothing to update in this view
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