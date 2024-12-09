import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application{
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carregar o arquivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("style.fxml"));

        // Configurar a cena e o palco
        Scene scene = new Scene(root);
        primaryStage.setTitle("Aplicação JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
