package userinterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import impresario.IModel;

public class LibrarianView extends View {

    // For showing error message
    private MessageView statusLog;

    // Constructor
    public LibrarianView(IModel model) {
        super(model, "LibrarianView");

        // Create a container for showing the contents
        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        // Create our GUI components, add them to this panel
        container.getChildren().add(createTitle("LIBRARY SYSTEM"));
        container.getChildren().add(createFormContents());

        // Error message area
        container.getChildren().add(createStatusLog());

        getChildren().add(container);
    }

    // Create the main form contents - note this is different from the parent method
    private VBox createFormContents() {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        // Insert Book Button
        // GUI components
        Button insertBookButton = new Button("INSERT NEW BOOK");
        insertBookButton.setMinWidth(200);
        insertBookButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processAction("InsertBook");
            }
        });

        // Insert Patron Button
        var insertPatronButton = new Button("INSERT NEW PATRON");
        insertPatronButton.setMinWidth(200);
        insertPatronButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processAction("InsertPatron");
            }
        });

        // Search Books Button
        var searchBooksButton = new Button("SEARCH BOOKS");
        searchBooksButton.setMinWidth(200);
        searchBooksButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processAction("SearchBooks");
            }
        });

        // Search Patrons Button
        var searchPatronsButton = new Button("SEARCH PATRONS");
        searchPatronsButton.setMinWidth(200);
        searchPatronsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processAction("SearchPatrons");
            }
        });

        // Done Button
        var doneButton = new Button("DONE");
        doneButton.setMinWidth(100);
        doneButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processAction("Done");
            }
        });

        // Add buttons to vbox
        vbox.getChildren().add(insertBookButton);
        vbox.getChildren().add(insertPatronButton);
        vbox.getChildren().add(searchBooksButton);
        vbox.getChildren().add(searchPatronsButton);
        vbox.getChildren().add(doneButton);

        return vbox;
    }

    // Create the status log field
    private MessageView createStatusLog() {
        statusLog = new MessageView("");
        return statusLog;
    }

    // Process button events
    private void processAction(String action) {
        clearErrorMessage();
        myModel.stateChangeRequest(action, null);
    }

    // Required by interface
    public void updateState(String key, Object value) {
        // Nothing to update in this view
    }

    // Display error message
    public void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    // Clear error message
    public void clearErrorMessage() {
        statusLog.clearErrorMessage();
    }
}