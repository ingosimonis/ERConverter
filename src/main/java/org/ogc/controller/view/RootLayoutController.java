package org.ogc.controller.view;

import java.io.File;
import java.util.prefs.Preferences;

import org.ogc.controller.ERMain;
import org.ogc.er.ERConstants;
import org.ogc.spelling.InFileIdentifier;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class RootLayoutController {
	
	// reference to main application
	private ERMain mainApp;
	private Preferences prefs;
	
	// FXML links
	@FXML
	private MenuItem setIgnoreMenuItem;
	@FXML
	private MenuItem showIgnoreMenuItem;
	@FXML
	private MenuItem exitMenuItem;
			

	/**
	 * Contstructor
	 */
	public RootLayoutController() {
		prefs = Preferences.userNodeForPackage(this.getClass());
	}
		
	 /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
	@FXML
	private void initialize() {
		// todo
	}
	
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(ERMain mainApp) {
        this.mainApp = mainApp;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Called when the user clicks on SetIgnoreList
     */
    @FXML
    private void handleSetIgnoreMenuItem() {
    	mainApp.showIgnoreDialog();
    }
    
    /**
     * Called when the user clicks on showIgnoreList
     */
    @FXML
    private void handleShowIgnoreMenuItem() {
    	String ignore = prefs.get(ERConstants.IGNORE_PATH, null);
    	
    	InFileIdentifier ifi = new InFileIdentifier(new File(ignore), 0);

    	mainApp.showFileEditDialog(ifi);
    }
    
    
    
    /**
     * Called when the user clicks on exit
     */
    @FXML
    private void handleMenuExit() {
    	System.exit(0);
    }


}
