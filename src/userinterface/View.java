package userinterface;

// system imports
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;

// project imports
import impresario.IModel;
import impresario.IView;

/**
 * The abstract class for all Views
 */
//==============================================================
public abstract class View extends VBox implements IView
{
    // Class variables
    protected IModel myModel;
    protected String myPresenterId;

    // GUI components
    protected Scene myScene;

    // Class constructor
    //----------------------------------------------------------
    public View(IModel model, String presenterId)
    {
        // Set up associations
        myModel = model;
        myPresenterId = presenterId;

        // Subscribe this view to the model
        myModel.subscribe(myPresenterId, this);
    }

    /**
     * Update the visual appearance of this view using information provided by the model
     */
    //--------------------------------------------------------------------------
    public abstract void updateState(String key, Object value);

    /**
     * Create the status log visual appearance
     */
    //---------------------------------------------------------
    protected Node createTitle(String titleStr)
    {
        HBox container = new HBox();
        container.setAlignment(javafx.geometry.Pos.CENTER);

        Text titleText = new Text(titleStr);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setWrappingWidth(300);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.DARKGREEN);
        container.getChildren().add(titleText);

        return container;
    }

    /**
     * Create a centered grid for form content
     */
    //---------------------------------------------------------
    protected GridPane createFormContent()
    {
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(25, 25, 25, 25));

        return grid;
    }

    /**
     * Display an error message
     */
    //----------------------------------------------------------
    public void displayErrorMessage(String message)
    {
        // Default implementation - override in subclasses
        System.out.println("Error: " + message);
    }

    /**
     * Display a message
     */
    //----------------------------------------------------------
    public void displayMessage(String message)
    {
        // Default implementation - override in subclasses
        System.out.println("Message: " + message);
    }

    /**
     * Clear error message
     */
    //----------------------------------------------------------
    public void clearErrorMessage()
    {
        // Default implementation - override in subclasses
    }
}