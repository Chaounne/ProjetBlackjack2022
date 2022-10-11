package blackjack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Blackjack extends Application{

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			
			FXMLLoader loader = new FXMLLoader();
			
			loader.setLocation(Blackjack.class.getResource("Blackjack.fxml"));
			BorderPane root = loader.load();
			root.setStyle("-fx-background-image: url(\"blackjack/ressource/table.jpg\"); -fx-background-size: cover;"); //afaire
			BlackjackController ctrl = loader.getController();
			ctrl.setFenetrePrincipale(primaryStage);
			
			Scene scene = new Scene(root);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Ningen Blackjack");
			primaryStage.setScene(scene);
			scene.getStylesheets().add(getClass().getResource("view/blackjack.css").toExternalForm());
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
}


