
import Memória.Memoria;
import Registradores.Registradores;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private Memoria memoria;
    private Registradores registradores;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        memoria = new Memoria();
        registradores = new Registradores();
        registradores.getRegistradores(1).setReg((byte) 0x01, (byte) 0x02, (byte) 0x03);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("style.fxml"));
        Parent root = loader.load();

        // Obter o controlador e passar os dados
        Controller controller = loader.getController();
        controller.updateRegistradores(registradores);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Simulador SIC");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
