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
import model.Book;
import model.BookCollection;

public class    SearchBooksView extends View {
    // GUI components
    private ComboBox<String> searchTypeComboBox;
    private TextField searchValueField;
    private Button searchButton;
    private Button cancelButton;
    private TableView<BookTableModel> tableOfBooks;

    // For showing error message
    private MessageView statusLog;

    // Constructor
    public SearchBooksView(IModel model) {
        super(model, "SearchBooksView");

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

        Text titleText = new Text("Search Books");
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
        searchTypeComboBox.getItems().addAll("Title", "Year");
        searchTypeComboBox.setValue("Title");
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
        tableOfBooks = new TableView<>();
        tableOfBooks.setEditable(false);

        TableColumn<BookTableModel, String> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setMinWidth(50);
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));

        TableColumn<BookTableModel, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setMinWidth(200);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));

        TableColumn<BookTableModel, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setMinWidth(150);
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<BookTableModel, String> pubYearColumn = new TableColumn<>("Year");
        pubYearColumn.setMinWidth(50);
        pubYearColumn.setCellValueFactory(new PropertyValueFactory<>("pubYear"));

        TableColumn<BookTableModel, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setMinWidth(70);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableOfBooks.getColumns().addAll(bookIdColumn, titleColumn, authorColumn, pubYearColumn, statusColumn);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(400, 200);
        scrollPane.setContent(tableOfBooks);

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

        BookCollection bookCollection = new BookCollection();

        try {
            if (searchType.equals("Title")) {
                bookCollection.findBooksWithTitleLike(searchValue);
            } else if (searchType.equals("Year")) {
                bookCollection.findBooksOlderThanDate(searchValue);
            }

            displayBooks(bookCollection.getBooks());

        } catch (Exception e) {
            displayErrorMessage("Error performing search: " + e.getMessage());
        }
    }

    // Display books in table
    private void displayBooks(Vector<Book> books) {
        ObservableList<BookTableModel> tableData = FXCollections.observableArrayList();

        if (books.size() == 0) {
            displayMessage("No books found matching your criteria");
            return;
        }

        try {
            for (Book book : books) {
                Vector<String> bookData = book.getEntryListView();
                BookTableModel nextTableRowData = new BookTableModel(bookData);
                tableData.add(nextTableRowData);
            }

            tableOfBooks.setItems(tableData);
            displayMessage(books.size() + " book(s) found");

        } catch (Exception e) {
            displayErrorMessage("Error displaying books: " + e.getMessage());
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
    public class BookTableModel {
        private final SimpleStringProperty bookId;
        private final SimpleStringProperty bookTitle;
        private final SimpleStringProperty author;
        private final SimpleStringProperty pubYear;
        private final SimpleStringProperty status;

        public BookTableModel(Vector<String> bookData) {
            this.bookId = new SimpleStringProperty(bookData.elementAt(0));
            this.bookTitle = new SimpleStringProperty(bookData.elementAt(1));
            this.author = new SimpleStringProperty(bookData.elementAt(2));
            this.pubYear = new SimpleStringProperty(bookData.elementAt(3));
            this.status = new SimpleStringProperty(bookData.elementAt(4));
        }

        public String getBookId() { return bookId.get(); }
        public String getBookTitle() { return bookTitle.get(); }
        public String getAuthor() { return author.get(); }
        public String getPubYear() { return pubYear.get(); }
        public String getStatus() { return status.get(); }
    }
}