package trying;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Trying extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../Vistas/UserVista.fxml"));
        stage.setTitle("PROYECTO PLAGIO - EDA");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
