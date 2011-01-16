/*
 * FalconMainApp.java
 */

package Falcon;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class FalconMainApp extends SingleFrameApplication {

    private static boolean devMode = false;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        //FalconMainView pv = new FalconMainView(this);
        FalconMainView.initialize(this);
        FalconMainView pv = FalconMainView.getInstance();
        //pv.getFrame().setTitle("Falcon"); //gets overwritten by the heavy-weight browser container
        //TODO: override the embedded browser title from forwarding to the top-level frame title
        if(devMode) pv.useDevMode();
        show(pv);
        pv.postInit();
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of FalconMainApp
     */
    public static FalconMainApp getApplication() {
        return Application.getInstance(FalconMainApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        for(String s:args){
            if(s.equals("--dev")){
                devMode = true;
            }
        }
        launch(FalconMainApp.class, args);
    }
}
