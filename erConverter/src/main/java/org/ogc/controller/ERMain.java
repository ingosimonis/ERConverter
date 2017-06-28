package org.ogc.controller;

import java.io.IOException;

import org.ogc.controller.view.ERViewController;
import org.ogc.controller.view.EditFileDialogController;
import org.ogc.controller.view.IgnoreDialogController;
import org.ogc.controller.view.RootLayoutController;
import org.ogc.spelling.InFileIdentifier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ERMain extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("ER Review Tool");
		
		initRootLayout();
		showEROverview();
	}
	
	public void showIgnoreDialog(){
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ERMain.class.getResource("view/IgnoreDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Preferenes");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Load the controller.
			IgnoreDialogController controller = loader.getController();
			controller.setMainApp(this);
			controller.setDialogStage(dialogStage);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Opens the FileEditDialog
	 * @param ifi
	 */
	public void showFileEditDialog(InFileIdentifier ifi) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ERMain.class.getResource("view/EditFileDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Correct Spelling");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Set the text
			EditFileDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setText(ifi);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	private void showEROverview() {
		// Load ER part from fxml file
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ERMain.class.getResource("view/ERView.fxml"));
			AnchorPane erView = (AnchorPane) loader.load();
			
			// set erView into center of root layout
			rootLayout.setCenter(erView);
			
			// give the controller access to the main app
			ERViewController controller = loader.getController();
			controller.setMainApp(this);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void initRootLayout() {
		try {
			// load root layout from fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ERMain.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			// show the scene 
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// give the controller access to the main app
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
