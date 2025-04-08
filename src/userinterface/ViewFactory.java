package userinterface;

import impresario.IModel;

public class ViewFactory {
    public static View createView(String viewName, IModel model) {
        if (viewName.equals("LibrarianView")) {
            return new LibrarianView(model);
        }
        else if (viewName.equals("BookView")) {
            return new BookView(model);
        }
        else if (viewName.equals("InsertBookView")) {
            return new InsertBookView(model);
        }
        else if (viewName.equals("PatronView")) {
            return new PatronView(model);
        }
        else if (viewName.equals("InsertPatronView")) {
            return new InsertPatronView(model);
        }
        else if (viewName.equals("SearchBooksView")) {
            return new SearchBooksView(model);
        }
        else if (viewName.equals("SearchPatronsView")) {
            return new SearchPatronsView(model);
        }
        else {
            return null;
        }
    }
}