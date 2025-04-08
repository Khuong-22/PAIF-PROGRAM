package userinterface;

// system imports
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

/**
 * This class serves as a message display component for the user interface
 * It displays error messages and status information to the user
 */
//==============================================================
public class MessageView extends VBox
{
    // Private data members
    private Text messageText;

    // Class constructor
    //----------------------------------------------------------
    public MessageView(String initialMessage)
    {
        // Create the message display text object
        messageText = new Text(initialMessage);
        messageText.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        messageText.setWrappingWidth(400);
        messageText.setTextAlignment(TextAlignment.CENTER);

        // Add it to this view
        getChildren().add(messageText);
    }

    /**
     * Display an error message (shown in red)
     */
    //----------------------------------------------------------
    public void displayErrorMessage(String message)
    {
        messageText.setFill(Color.RED);
        messageText.setText(message);
    }

    /**
     * Display a normal message (shown in black)
     */
    //----------------------------------------------------------
    public void displayMessage(String message)
    {
        messageText.setFill(Color.BLACK);
        messageText.setText(message);
    }

    /**
     * Clear the error message
     */
    //----------------------------------------------------------
    public void clearErrorMessage()
    {
        messageText.setText("");
    }
}