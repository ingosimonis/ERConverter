package org.ogc.controller.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import org.im4java.process.ProcessStarter;
import org.ogc.controller.ERMain;
import org.ogc.er.ERConstants;
import org.ogc.er.EngineeringReportFolder;
import org.ogc.er.Report;
import org.ogc.spelling.InFileIdentifier;
import org.ogc.spelling.SpellChecker;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class ERViewController {
	
	// reference to main application
	private ERMain mainApp;
	private Preferences prefs;
	
	// FXML controls
	@FXML
	private TextField erField;
	@FXML
	private TextField compiledField;
	@FXML
	private TextArea resultArea;
	@FXML
	private Button spellCheckButton;
	@FXML
	private Button rulesCheckButton;
	@FXML
	private Button pngCheckButton;
	@FXML
	private Button pngCorrectButton;
	@FXML
	private Button toHTMLButton;
	@FXML
	private Button toPDFButton;
	@FXML
	private Button erDirButton;
	@FXML
	private Button erCompiledButton;
	@FXML
	private Button openButton;
	@FXML
	private Button addIgnoreButton;
	
    /////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 */
	public ERViewController() {
		this.mainApp = null;
		this.prefs = Preferences.userNodeForPackage(this.getClass());
	}
	
    /////////////////////////////////////////////////////////////////////////////////////////////////////
	public String getIgnoreListLocation(){
		return prefs.get(ERConstants.IGNORE_PATH, "default");
	}


    /////////////////////////////////////////////////////////////////////////////////////////////////////

	 /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
	@FXML
	private void initialize() {
		erField.setText(prefs.get(ERConstants.ER_PATH, ""));
		compiledField.setText(prefs.get(ERConstants.COMPILED_PATH, ""));
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
     * Called when the user clicks on erDirButton
     */
    @FXML
    private void handleErDirButton() { 
    	DirectoryChooser dc = new DirectoryChooser();
    	dc.setTitle("Root Directory Selection");
    	String iniDir = prefs.get(ERConstants.ER_PATH, null);
    	
    	// check if preferences contain information about initial directory
    	if(iniDir == null) {
    		dc.setInitialDirectory(new File(System.getProperty("user.home")));
    	} else {
    		// check if iniDir exists
    		File f = new File(iniDir);
    		if(f.exists()) {
    			dc.setInitialDirectory(new File(iniDir));
    		} else {
    			dc.setInitialDirectory(new File(System.getProperty("user.home")));
    		}
    	}	

    	// set file to textField and add to preferences
    	File selectedDir = dc.showDialog(null);
    	if(selectedDir == null || !selectedDir.exists()) {
    		erField.setText(prefs.get(ERConstants.ER_PATH, ""));
    	} else {
    		erField.setText(selectedDir.getAbsolutePath());
    	}
    
    	prefs.put(ERConstants.ER_PATH, erField.getText());
    }
    
    /**
     * Called when the user clicks on compiledDirButton
     */
    @FXML
    private void handleErCompiledButton() {
    	DirectoryChooser dc = new DirectoryChooser();
    	dc.setTitle("Output Directory Selection");
    	String iniDir = prefs.get(ERConstants.COMPILED_PATH, null);
    	
    	// check if preferences contain information about initial directory
    	if(iniDir == null) {
    		dc.setInitialDirectory(new File(System.getProperty("user.home")));
    	} else {
    		// check if iniDir exists
    		File f = new File(iniDir);
    		if(f.exists()) {
    			dc.setInitialDirectory(new File(iniDir));
    		} else {
    			dc.setInitialDirectory(new File(System.getProperty("user.home")));
    		}
    	}	

    	// set file to textField and add to preferences
    	File selectedDir = dc.showDialog(null);
    	if(selectedDir == null || !selectedDir.exists()) {
    		compiledField.setText(prefs.get(ERConstants.COMPILED_PATH, ""));
    	} else {
    		compiledField.setText(selectedDir.getAbsolutePath());
    	}
    
    	prefs.put(ERConstants.COMPILED_PATH, compiledField.getText());
    }
    
    /**
     * Called when the user clicks on spellCheckButton
     */
    @FXML
    private void handleSpellCheckButton() {
    	String dir = erField.getText();
    	// check if dir was provided
    	if(dir.isEmpty()) {
    		DirectoryChooser dc = new DirectoryChooser();
        	dc.setTitle("Root Directory Selection");
        	File selectedDir = dc.showDialog(null);
        	erField.setText(selectedDir.getAbsolutePath());
        	dir = erField.getText();
    	}
    	// get location of ignore list or run default
        String s = prefs.get(ERConstants.IGNORE_PATH, null);
        if(s == null) {
        	Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Information");
    		alert.setHeaderText("Spell checker runs with default ignore list!");
    		alert.setContentText("You have not specified an ignore list. "
    				+ "This is fine, but you cannot alter that list. You "
    				+ "can define your own ignore list under File > SetIgnoreList.");

    		alert.showAndWait();
    		s = "ignore.txt";
        } 
        File ignore = new File(s);
        try {
			SpellChecker sp = new SpellChecker(ignore);
			resultArea.setText(sp.getSpellingReport(new File(dir)).toString());
		} catch (StringIndexOutOfBoundsException e) {
			resultArea.setText("The ignore file could not be read. Please provide a "
					+ "single word per line.");
		} catch (Exception e) {
			resultArea.setText(e.getMessage());
		}
    	
    }
    
    /**
     * Called when the user clicks on RulesCheckButton
     */
    @FXML
    private void handleRulesCheckButton() {
    	
    	// todo
    	
    }
    
    /**
     * Called when the user clicks on addIgnoreButton
     */
    @FXML
    private void handleAddIgnoreButton() {
    	
    	// only full words are added
    	if(resultArea.getSelectedText().equalsIgnoreCase("")) {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Information");
    		alert.setHeaderText("Selected text cannot be added!");
    		alert.setContentText("Please select a full word to be added to the ignore list. "
    				+ "You can review the ignore list any time at menu File > Show Ignore List.");

    		alert.showAndWait();
    	}
    	
    	String ignorePath = prefs.get(ERConstants.IGNORE_PATH, null);
    	// check if ignoreFile path is provided
    	if(ignorePath == null) {
    		mainApp.showIgnoreDialog();
    	}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ignorePath), true));
			writer.write(resultArea.getSelectedText());
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Called when the user clicks on pngCheck
     */
    @FXML
    private void handlePNGCheckButton() {
       String folder = erField.getText();
       EngineeringReportFolder erf = new EngineeringReportFolder(new File(folder));
       List<String> png = erf.getInterlacedPNGs();
       Iterator<String> iter = png.iterator();
       StringBuffer buf = new StringBuffer();
       while(iter.hasNext()) {
    	   	buf.append(iter.next());
    	   	buf.append("\n");
       }
       // write results to resultArea
       String allPng = buf.toString();
       if(allPng.isEmpty()) {
    	   resultArea.setText("No interlaced png found!");
       } else {
           resultArea.setText(allPng);
       }
    }
    
    /**
     * Called when the user clicks on pngCorrect
     */
    @FXML
    private void handlePNGCorrectButton() {
    	
    	// first check if reference to ImageMagick is provided
    	String myPath = prefs.get(ERConstants.IMAGE_MAGICK, null);
    	if(myPath == null) {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Information");
    		alert.setHeaderText("ImageMagick not found!");
    		alert.setContentText("This program requires ImageMagick to be installed"
    				+ "on your system. Please download from here: http://imagemagick.org and provide "
    				+ "the path to ImageMagick in your preferences");

    		alert.showAndWait();
    		mainApp.showIgnoreDialog();
    	} else {
        	ProcessStarter.setGlobalSearchPath(myPath);
        	String folder = erField.getText();
        	EngineeringReportFolder erf = new EngineeringReportFolder(new File(folder));
           
        	Boolean b = erf.removePNGInterlace();
        	if(b) {
        		resultArea.setText("All interleaved PNG files overwritten!");
        	} else {
        		resultArea.setText("Error handling PNG files. No files overwritten!");
        	}
        	
    	}
    }
    
    /**
     * Called when the user clicks on toHTMLCorrect
     */
    @FXML
    private void handleToHTMLButton() {
    	
    	// get the folder to be converted
    	String erFolder = erField.getText();
    	
        EngineeringReportFolder erf = new EngineeringReportFolder(new File(erFolder));
        Report report;
		try {
			// convert
			report = erf.convertToHTML(new File(compiledField.getText()));
			// and provide results
			StringBuffer buffer = new StringBuffer();    
	        buffer.append(report.getTitle());
	        buffer.append("\n");
	        buffer.append(report.toString());
	        resultArea.setText(buffer.toString());
		} catch (Exception e) {
			resultArea.setText("Requested directory/file not found!");
		}
       
    }
    
    /**
     * Called when the user clicks on toHTMLCorrect
     */
    @FXML
    private void handleToPDFButton() {
    	  	
    	// get the folder to be converted
    	String erFolder = erField.getText();
    	
        EngineeringReportFolder erf = new EngineeringReportFolder(new File(erFolder));
        
        try {
			// convert
        	Report report = erf.convertToPDF(new File(compiledField.getText()));
			// and provide report
			StringBuffer buffer = new StringBuffer();
			buffer.append(report.getTitle());
			buffer.append("\n");
			buffer.append(report.toString());
			resultArea.setText(buffer.toString());
		} catch (Exception e) {
			resultArea.setText("Requested directory/file not found!");
		}
    }
        
    /**
     * Called when the user clicks on openCheck: Get selected text and open in preferred editor
     */
    @FXML
    private void handleOpenButton() {
    	   	  	
    	int caret = resultArea.getCaretPosition();	
    	String selection = resultArea.getSelectedText();
		InFileIdentifier ifi = searchFile(caret, resultArea.getText());
		
		if(ifi != null) {
			ifi.setSelection(selection);
			mainApp.showFileEditDialog(ifi);
		}
	
//    	
//    	
//    	File file = new File(compiledField.getText());
//    	
//    	Runtime run = Runtime.getRuntime();
//        String lcOSName = System.getProperty("os.name").toLowerCase();
//        boolean MAC_OS_X = lcOSName.startsWith("mac os x");
//        try {
//			if (MAC_OS_X) {
//			    run.exec("open " + file);
//			} else {
//			    //run.exec("cmd.exe /c start " + file); //win NT, win2000
//			    run.exec("rundll32 url.dll, FileProtocolHandler " + file.getAbsolutePath());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    	
    }

    /**
     * Helper method to find the file the error comes from together with the 
     * line number of the error. If the entire file should be loaded, lineNumber
     * equals 0.
     * @param caret
     * @param text
     * @return InFileIdentifier
     */
	private InFileIdentifier searchFile(int caret, String text) {
		// iterate over text and find last adoc before and after the cursor position
		int posABegin = 0;
		int posAEnd = 0;
		int posBBegin = 0;
		int posBEnd = 0;
		String file = "";
		int line = 0;
		String er = erField.getText();
		
		posABegin = text.lastIndexOf(er);
		posAEnd = text.lastIndexOf(".adoc"); 
		
		do {		
			try {
				// case 1: the user clicked on a file 
				if(caret < posAEnd && caret > posABegin) {
					file = text.substring(posABegin, posAEnd+5); 
					break; 
				} 
				
				// case 2: the user clicked one of the last error messages
				if(caret > posABegin) {
					file = text.substring(posABegin, posAEnd+5);
					line = identifyLine(caret, text);
					break;
				}
				
				// reduce text, as caret is before the given file line
				text = text.substring(0, posABegin);
				
				// get the previous file line
				posBBegin = text.lastIndexOf(er);
				posBEnd = text.lastIndexOf(".adoc");

				// case 3: the user clicked on a error line
				if(caret < posABegin && caret > posBEnd) {
					file = text.substring(posBBegin, posBEnd+5);
					line = identifyLine(caret, text);
					break;
				} 
				
				// iterate to next pair 
				posABegin = posBBegin;
				posAEnd = posBEnd;
			} catch (java.lang.StringIndexOutOfBoundsException e) { // case user clicked on empty area
				Alert alert = new Alert(AlertType.INFORMATION);
	    		alert.setTitle("Information");
	    		alert.setHeaderText("Selected text cannot be opened!");
	    		alert.setContentText("Please select a complete misspelled word to open the "
	    							+ "corresponding passage; or select a file line to "
	    							+ "open the entire file. Then click OPEN again.");
	
	    		alert.showAndWait();
	    		return null;
			}
			
		} while(true);
		return new InFileIdentifier(new File(file), line);
	}

	/**
	 * helper to identify the line of an error message
	 * @param caret
	 * @param text
	 * @return line number as int
	 */
	private int identifyLine(int caret, String text) {
		int bracket = text.lastIndexOf("[", caret);
		String lineNumber = text.substring(bracket-5, bracket-1);
		lineNumber = lineNumber.trim();
		
		return Integer.parseInt(lineNumber);
	}
	
}
