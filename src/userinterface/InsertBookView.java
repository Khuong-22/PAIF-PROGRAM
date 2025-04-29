package userinterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

public class InsertBookView extends View {
    // GUI components
    private TextField bookTitleField;
    private TextField authorField;
    private TextField pubYearField;
    private ComboBox<String> statusComboBox;

    private Button submitButton;
    private Button doneButton;

    // For showing error message
    private MessageView statusLog;

    // Constructor
    public InsertBookView(IModel model) {
        super(model, "InsertBookView");

        // Create a container for showing the contents
        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        // Create our GUI components, add them to this panel
        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContents());

        // Error message area
        container.getChildren().add(createStatusLog(""));

        getChildren().add(container);

        // Register this view as a subscriber to the model
        myModel.subscribe("BookUpdateStatusMessage", this);
        myModel.subscribe("InsertSuccessful", this);
    }

    // Create the title container
    private Node createTitle() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        Text titleText = new Text("Insert New Book");
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

        // Book Title
        Label bookTitleLabel = new Label("Book Title:");
        grid.add(bookTitleLabel, 0, 0);

        bookTitleField = new TextField();
        bookTitleField.setMinWidth(200);
        grid.add(bookTitleField, 1, 0);

        // Author
        Label authorLabel = new Label("Author:");
        grid.add(authorLabel, 0, 1);

        authorField = new TextField();
        authorField.setMinWidth(200);
        grid.add(authorField, 1, 1);

        // Publication Year
        Label pubYearLabel = new Label("Publication Year:");
        grid.add(pubYearLabel, 0, 2);

        pubYearField = new TextField();
        pubYearField.setMinWidth(200);
        grid.add(pubYearField, 1, 2);

        // Status ComboBox
        Label statusLabel = new Label("Status:");
        grid.add(statusLabel, 0, 3);

        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Active", "Inactive");
        statusComboBox.setValue("Active"); // Default value
        statusComboBox.setMinWidth(200);
        grid.add(statusComboBox, 1, 3);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        submitButton = new Button("Submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processBookSubmit(statusComboBox.getValue());
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
        statusLog = new MessageView("");
        return statusLog;
    }

    // Process book submission
    private void processBookSubmit(String status) {
        clearErrorMessage();

        String bookTitle = bookTitleField.getText();
        String author = authorField.getText();
        String pubYear = pubYearField.getText();

        if (bookTitle.length() == 0 || author.length() == 0 || pubYear.length() == 0) {
            displayErrorMessage("Please fill in all fields");
            return;
        }

        try {
            int year = Integer.parseInt(pubYear);
            if (year < 1800 || year > 2024) {
                displayErrorMessage("Publication Year must be between 1800 and 2024");
                return;
            }

            // Create properties object for new book
            Properties props = new Properties();
            props.setProperty("bookTitle", bookTitle);
            props.setProperty("author", author);
            props.setProperty("pubYear", pubYear);
            props.setProperty("status", status);

            // Send the data to the model instead of creating the book here
            myModel.stateChangeRequest("ProcessNewBook", props);

            // Fields will be cleared if insertion is successful
            // This happens after the "Done" button is clicked in the notification

        } catch (NumberFormatException e) {
            displayErrorMessage("Publication Year must be a valid number");
        } catch (Exception e) {
            displayErrorMessage("Error adding book: " + e.getMessage());
        }
    }

    // Process cancel request
    private void processCancel() {
        clearErrorMessage();
        myModel.stateChangeRequest("CancelAction", null);
    }

    // Clear form fields
    private void clearFields() {
        bookTitleField.clear();
        authorField.clear();
        pubYearField.clear();
        statusComboBox.setValue("Active");
    }

    // Required by interface
    public void updateState(String key, Object value) {
        // Display any status updates from the model
        if (key.equals("BookUpdateStatusMessage")) {
            displayMessage((String)value);
        }
        else if (key.equals("InsertSuccessful")) {
            // Book was successfully inserted
            // Clear the fields for the next entry
            clearFields();
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