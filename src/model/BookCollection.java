package model;

import java.util.Properties;
import java.util.Vector;
import exception.InvalidPrimaryKeyException;
import database.*;
import impresario.IView;

public class BookCollection extends EntityBase implements IView {
    private static final String myTableName = "Book";
    private Vector<Book> bookList;

    // Constructor - creates an empty collection
    public BookCollection() {
        super(myTableName);
        bookList = new Vector<Book>();
    }

    // Find books published before the specified year
    public void findBooksOlderThanDate(String year) {
        String query = "SELECT * FROM " + myTableName +
                " WHERE (pubYear <= '" + year + "')";
        populateBookList(query);
    }

    // Find books published after the specified year
    public void findBooksNewerThanDate(String year) {
        String query = "SELECT * FROM " + myTableName +
                " WHERE (pubYear > '" + year + "')";
        populateBookList(query);
    }

    // Find books with titles containing the search string
    public void findBooksWithTitleLike(String title) {
        String query = "SELECT * FROM " + myTableName +
                " WHERE (bookTitle LIKE '%" + title + "%')";
        populateBookList(query);
    }

    // Find books with authors containing the search string
    public void findBooksWithAuthorLike(String author) {
        String query = "SELECT * FROM " + myTableName +
                " WHERE (author LIKE '%" + author + "%')";
        populateBookList(query);
    }

    // Helper method to populate the bookList from a query
    private void populateBookList(String query) {
        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        bookList = new Vector<Book>();

        if (allDataRetrieved != null) {
            for (int cnt = 0; cnt < allDataRetrieved.size(); cnt++) {
                Properties nextBookData = allDataRetrieved.elementAt(cnt);
                Book book = new Book(nextBookData);

                if (book != null) {
                    bookList.add(book);
                }
            }
        }
    }

    // Required by IView interface
    public Object getState(String key) {
        if (key.equals("Books"))
            return bookList;
        else
        if (key.equals("BookList"))
            return this;
        return null;
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    // Method to get direct access to bookList
    public Vector<Book> getBooks() {
        return bookList;
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}