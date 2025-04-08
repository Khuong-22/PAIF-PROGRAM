package userinterface;

// system imports
import javafx.stage.Stage;

/**
 * Class that provides a singleton container for the main stage (window)
 */
//==============================================================
public class MainStageContainer
{
    // Private data members
    private static Stage myInstance = null;

    /**
     * Set the stage reference for this application - used at launch
     */
    //----------------------------------------------------------
    public static void setStage(Stage st, String title)
    {
        myInstance = st;
        myInstance.setTitle(title);
        myInstance.setResizable(true);
    }

    /**
     * Get a reference to the singleton stage
     */
    //----------------------------------------------------------
    public static Stage getInstance()
    {
        return myInstance;
    }
}