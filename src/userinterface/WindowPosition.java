package userinterface;

// system imports
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * Utility class to help position windows on the screen
 */
//==============================================================
public class WindowPosition
{
    /**
     * Center a window on the screen
     */
    //----------------------------------------------------------
    public static void placeCenter(Stage stage)
    {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    /**
     * Place a window at specified x, y coordinates
     */
    //----------------------------------------------------------
    public static void placeAt(Stage stage, double x, double y)
    {
        stage.setX(x);
        stage.setY(y);
    }

    /**
     * Place a window on the left side of the screen
     */
    //----------------------------------------------------------
    public static void placeLeft(Stage stage)
    {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX() + 20);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    /**
     * Place a window on the right side of the screen
     */
    //----------------------------------------------------------
    public static void placeRight(Stage stage)
    {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMaxX() - stage.getWidth() - 20);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }
}