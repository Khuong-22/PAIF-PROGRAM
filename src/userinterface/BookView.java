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

public class BookView extends View {
    // GUI components
    private TextField bookTitleField;
    private TextField authorField;
    private TextField pubYearField;
    private ComboBox<String> statusComboBox;

    private final Button doneButton = new Button("Done");

    // For showing error message
    private MessageView statusLog;

    // Constructor
    public BookView(IModel model) {
        super(model, "BookView");

        // Create a container for showing the contents
        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        // Create our GUI components, add them to this panel
        container.getChildren().add(createTitle("Book Information"));
        container.getChildren().add(createFormContents());

        // Error message area
        container.getChildren().add(createStatusLog());

        getChildren().add(container);
    }

    // Create the main form contents - using a different name to avoid override conflicts
    private VBox createFormContents() {
        VBox vbox = new VBox(10);

        GridPane grid = createFormContent(); // Call the parent's createFormContent() method

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

        var submitButton = new Button("Submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processBookSubmit();
            }
        });

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

    // Process book submission
    private void processBookSubmit() {
        clearErrorMessage();

        String bookTitle = bookTitleField.getText();
        String author = authorField.getText();
        String pubYear = pubYearField.getText();
        String status = statusComboBox.getValue();

        // Validation
        if (bookTitle.isEmpty()) {
            displayErrorMessage("Book Title cannot be empty");
            return;
        }

        if (author.isEmpty()) {
            displayErrorMessage("Author cannot be empty");
            return;
        }

        try {
            int year = Integer.parseInt(pubYear);
            if (year < 1800 || year > 2024) {
                displayErrorMessage("Publication Year must be between 1800 and 2024");
                return;
            }
        } catch (NumberFormatException e) {
            displayErrorMessage("Publication Year must be a valid number");
            return;
        }

        // Create properties object with book data
        Properties bookData = new Properties();
        bookData.setProperty("bookTitle", bookTitle);
        bookData.setProperty("author", author);
        bookData.setProperty("pubYear", pubYear);
        bookData.setProperty("status", status);

        // Send data to model
        myModel.stateChangeRequest("ProcessNewBook", bookData);
    }

    // Required by interface
    public void updateState(String key, Object value) {
        if (key.equals("BookUpdateStatusMessage")) {
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