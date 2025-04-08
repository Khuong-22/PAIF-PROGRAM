package userinterface;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Vector;

import impresario.IModel;
import model.Patron;
import model.PatronCollection;

public class SearchPatronsView extends View {
    // GUI components
    private ComboBox<String> searchTypeComboBox;
    private TextField searchValueField;
    private Button searchButton;
    private Button cancelButton;
    private TableView<PatronTableModel> tableOfPatrons;

    // For showing error message
    private MessageView statusLog;

    // Constructor
    public SearchPatronsView(IModel model) {
        super(model, "SearchPatronsView");

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

        Text titleText = new Text("Search Patrons");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setWrappingWidth(300);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.BLACK);
        container.getChildren().add(titleText);

        return container;
    }

    // Create the main form content - renamed to avoid overriding parent method
    private VBox createFormContents() {
        VBox vbox = new VBox(10);

        // Search criteria
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        // Search type selection
        Label searchTypeLabel = new Label("Search by:");
        grid.add(searchTypeLabel, 0, 0);

        searchTypeComboBox = new ComboBox<>();
        searchTypeComboBox.getItems().addAll("Date of Birth", "Zip Code");
        searchTypeComboBox.setValue("Date of Birth");
        grid.add(searchTypeComboBox, 1, 0);

        // Search value
        Label searchValueLabel = new Label("Search value:");
        grid.add(searchValueLabel, 0, 1);

        searchValueField = new TextField();
        searchValueField.setMinWidth(200);
        grid.add(searchValueField, 1, 1);

        // Search button
        HBox searchButtonBox = new HBox(10);
        searchButtonBox.setAlignment(Pos.CENTER);

        searchButton = new Button("Search");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processSearch();
            }
        });

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                clearErrorMessage();
                myModel.stateChangeRequest("CancelAction", null);
            }
        });

        searchButtonBox.getChildren().add(searchButton);
        searchButtonBox.getChildren().add(cancelButton);

        // Create table for results
        tableOfPatrons = new TableView<>();
        tableOfPatrons.setEditable(false);

        TableColumn<PatronTableModel, String> patronIdColumn = new TableColumn<>("Patron ID");
        patronIdColumn.setMinWidth(70);
        patronIdColumn.setCellValueFactory(new PropertyValueFactory<>("patronId"));

        TableColumn<PatronTableModel, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(150);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<PatronTableModel, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setMinWidth(150);
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<PatronTableModel, String> cityColumn = new TableColumn<>("City");
        cityColumn.setMinWidth(100);
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<PatronTableModel, String> stateColumn = new TableColumn<>("State");
        stateColumn.setMinWidth(50);
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("stateCode"));

        TableColumn<PatronTableModel, String> zipColumn = new TableColumn<>("Zip");
        zipColumn.setMinWidth(50);
        zipColumn.setCellValueFactory(new PropertyValueFactory<>("zip"));

        TableColumn<PatronTableModel, String> dobColumn = new TableColumn<>("DOB");
        dobColumn.setMinWidth(80);
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        tableOfPatrons.getColumns().addAll(patronIdColumn, nameColumn, addressColumn,
                cityColumn, stateColumn, zipColumn, dobColumn);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(400, 200);
        scrollPane.setContent(tableOfPatrons);

        vbox.getChildren().add(grid);
        vbox.getChildren().add(searchButtonBox);
        vbox.getChildren().add(scrollPane);

        return vbox;
    }

    // Create the status log field
    private MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    // Process search request
    private void processSearch() {
        clearErrorMessage();

        String searchType = searchTypeComboBox.getValue();
        String searchValue = searchValueField.getText().trim();

        if (searchValue.length() == 0) {
            displayErrorMessage("Please enter a search value");
            return;
        }

        PatronCollection patronCollection = new PatronCollection();

        try {
            if (searchType.equals("Date of Birth")) {
                patronCollection.findPatronsYoungerThan(searchValue);
            } else if (searchType.equals("Zip Code")) {
                patronCollection.findPatronsAtZipCode(searchValue);
            }

            displayPatrons(patronCollection.getPatrons());

        } catch (Exception e) {
            displayErrorMessage("Error performing search: " + e.getMessage());
        }
    }

    // Display patrons in table
    private void displayPatrons(Vector<Patron> patrons) {
        ObservableList<PatronTableModel> tableData = FXCollections.observableArrayList();

        if (patrons.size() == 0) {
            displayMessage("No patrons found matching your criteria");
            return;
        }

        try {
            for (Patron patron : patrons) {
                Vector<String> patronData = patron.getEntryListView();
                PatronTableModel nextTableRowData = new PatronTableModel(patronData);
                tableData.add(nextTableRowData);
            }

            tableOfPatrons.setItems(tableData);
            displayMessage(patrons.size() + " patron(s) found");

        } catch (Exception e) {
            displayErrorMessage("Error displaying patrons: " + e.getMessage());
        }
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

    // Inner class for table model
    public class PatronTableModel {
        private final SimpleStringProperty patronId;
        private final SimpleStringProperty name;
        private final SimpleStringProperty address;
        private final SimpleStringProperty city;
        private final SimpleStringProperty stateCode;
        private final SimpleStringProperty zip;
        private final SimpleStringProperty email;
        private final SimpleStringProperty dateOfBirth;
        private final SimpleStringProperty status;

        public PatronTableModel(Vector<String> patronData) {
            this.patronId = new SimpleStringProperty(patronData.elementAt(0));
            this.name = new SimpleStringProperty(patronData.elementAt(1));
            this.address = new SimpleStringProperty(patronData.elementAt(2));
            this.city = new SimpleStringProperty(patronData.elementAt(3));
            this.stateCode = new SimpleStringProperty(patronData.elementAt(4));
            this.zip = new SimpleStringProperty(patronData.elementAt(5));
            this.email = new SimpleStringProperty(patronData.elementAt(6));
            this.dateOfBirth = new SimpleStringProperty(patronData.elementAt(7));
            this.status = new SimpleStringProperty(patronData.elementAt(8));
        }

        public String getPatronId() { return patronId.get(); }
        public String getName() { return name.get(); }
        public String getAddress() { return address.get(); }
        public String getCity() { return city.get(); }
        public String getStateCode() { return stateCode.get(); }
        public String getZip() { return zip.get(); }
        public String getEmail() { return email.get(); }
        public String getDateOfBirth() { return dateOfBirth.get(); }
        public String getStatus() { return status.get(); }
    }
}