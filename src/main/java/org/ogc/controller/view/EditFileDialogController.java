package org.ogc.controller.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.ogc.spelling.InFileIdentifier;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class EditFileDialogController {

	// reference to main application
	private InFileIdentifier ifi;
	
	private Stage dialogStage;
	
	// FXML controls
	@FXML
	private TextArea textArea;
	@FXML
	private Button saveButton;
	@FXML
	private Button cancelButton;
	
	public EditFileDialogController() {
		// TODO Auto-generated constructor stub
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
     * sets the text to be edited in this dialog
     * @param textArea
     */
    public void setText(InFileIdentifier ifi) {
    	this.ifi = ifi;
    	StringBuffer buffer;
    	String text = "";
		try {
			File f = ifi.getFile();
			BufferedReader reader = new BufferedReader(new FileReader(f));
			buffer = new StringBuffer();
			String line;
			while((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append(" \n");
			}
			text = buffer.toString();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	// if line number equals 0, open full file
    	if(ifi.getLine() == 0) {
    		textArea.setText(text);
    	} else { // otherwise jump to misspelled word ToDo: jump to actual occurrence of misspelled word!
    		textArea.setText(text);
    		int s = text.indexOf(ifi.getSelection());
    		
    		textArea.selectRange(s, s + ifi.getSelection().length());
    	}	
    	
    }
	
	 /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
	@FXML
	private void initialize() {
		textArea.setWrapText(true);
	}
	
	 /**
     * Called when the user clicks on saveButton
     */
    @FXML
    private void handleSaveButton() {
    	try {
			FileWriter fw = new FileWriter(ifi.getFile());
			fw.write(textArea.getText());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	 dialogStage.close();
    }
    
    /**
     * Called when the user clicks on cancelButton
     */
    @FXML
    private void handleCancelButton() {
    	 dialogStage.close();
    }

}
