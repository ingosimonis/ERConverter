package org.ogc.controller.view;

import java.io.File;
import java.util.prefs.Preferences;

import org.ogc.controller.ERMain;
import org.ogc.er.ERConstants;
import org.ogc.spelling.InFileIdentifier;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class IgnoreDialogController {
	
	private ERMain mainApp;
	private Stage dialogStage;
	Preferences prefs;
	
	@FXML
	private Button saveButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Button showButton;
	@FXML
	private Button ignoreButton;
	@FXML
	private Button magickButton;
	@FXML
	private Button ymlButton;
	@FXML
	private Button fontsButton;
	@FXML
	private TextField ignoreTextField;
	@FXML
	private TextField magickTextField;
	@FXML
	private TextField ymlTextField;
	@FXML
	private TextField fontsTextField;

	public IgnoreDialogController() {
		this.prefs = Preferences.userNodeForPackage(ERViewController.class);
	}
	
	/**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(ERMain mainApp) {
        this.mainApp = mainApp;
    }
    
	 /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
	@FXML
	private void initialize() {
		this.ignoreTextField.setText(prefs.get(ERConstants.IGNORE_PATH, ERConstants.NO_PATH_NO_DEFAULTS));
		this.magickTextField.setText(prefs.get(ERConstants.IMAGE_MAGICK, ERConstants.NO_PATH_NO_DEFAULTS));
		this.ymlTextField.setText(prefs.get(ERConstants.THEME_FILE, ERConstants.NO_PATH_USE_DEFAULTS));
		this.fontsTextField.setText(prefs.get(ERConstants.FONTS_DIR, ERConstants.NO_PATH_USE_DEFAULTS));
	}
	
	
	/**
     * Called when the user clicks on saveButton
     */
    @FXML
    private void handleSaveButton() {
    	// check if values have been set. Otherwise don't set values, as checks against null happens in 
    	// executeXYZ operations
    	if(!ignoreTextField.getText().equalsIgnoreCase(ERConstants.NO_PATH_NO_DEFAULTS)) {
    		prefs.put(ERConstants.IGNORE_PATH, ignoreTextField.getText());
    	}
    	
    	if(!magickTextField.getText().equalsIgnoreCase(ERConstants.NO_PATH_NO_DEFAULTS)) {
    		prefs.put(ERConstants.IMAGE_MAGICK, magickTextField.getText());
    	}
    	
    	if(!ymlTextField.getText().equalsIgnoreCase(ERConstants.NO_PATH_USE_DEFAULTS)) {
    		prefs.put(ERConstants.THEME_FILE, ymlTextField.getText());
    	}
    	
    	if(!fontsTextField.getText().equalsIgnoreCase(ERConstants.NO_PATH_USE_DEFAULTS)) {
    		prefs.put(ERConstants.FONTS_DIR, fontsTextField.getText());
    	}
    	
   	 	dialogStage.close();
    }
    
	/**
     * Called when the user clicks on saveButton
     */
    @FXML
    private void handleCancelButton() {
   	 	dialogStage.close();
    }
    
	/**
     * Called when the user clicks on saveButton
     */
    @FXML
    private void handleShowButton() {
    	String ignore = prefs.get(ERConstants.IGNORE_PATH, null);
    	InFileIdentifier ifi = new InFileIdentifier(new File(ignore), 0);
    	mainApp.showFileEditDialog(ifi);
    }
    
	/**
     * Called when the user clicks on ignoreButton
     */
    @FXML
    private void handleIgnoreButton() {
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Ignore List Chooser");
    	chooser.setSelectedExtensionFilter(new ExtensionFilter("txt files (*.txt)", "*.txt"));
    	File file = chooser.showOpenDialog(null);
    	ignoreTextField.setText(file.getAbsolutePath());
    	prefs.put(ERConstants.IGNORE_PATH, file.getAbsolutePath());
    }
    
    
	/**
     * Called when the user clicks on magickButton
     */
    @FXML
    private void handleMagickButton() {
    	DirectoryChooser chooser = new DirectoryChooser();
    	chooser.setTitle("ImageMagick Locator");
    	File file = chooser.showDialog(null);
    	magickTextField.setText(file.getAbsolutePath());
    	prefs.put(ERConstants.IMAGE_MAGICK, file.getAbsolutePath());
    }
    
    /**
     * Called when the user clicks on ignoreButton
     */
    @FXML
    private void handleYMLButton() {
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Theme File Chooser");
    	chooser.setSelectedExtensionFilter(new ExtensionFilter("yml files (*.yml)", "*.yml"));
    	File file = chooser.showOpenDialog(null);
    	ymlTextField.setText(file.getAbsolutePath());
    	prefs.put(ERConstants.THEME_FILE, file.getAbsolutePath());
    }
    
	/**
     * Called when the user clicks on magickButton
     */
    @FXML
    private void handleFontsButton() {
    	DirectoryChooser chooser = new DirectoryChooser();
    	chooser.setTitle("Fonts for PDF Conversion Locator");
    	File file = chooser.showDialog(null);
    	fontsTextField.setText(file.getAbsolutePath());
    	prefs.put(ERConstants.FONTS_DIR, file.getAbsolutePath());
    }

}
