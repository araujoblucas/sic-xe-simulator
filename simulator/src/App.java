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

        registradores.registradores[0].setReg((byte) 0x01, (byte) 0x02, (byte) 0x03);
        memoria.memoria.get(0).setValor((byte) 0x01, (byte) 0x02, (byte) 0x03);
        memoria.memoria.get(1).setValor((byte) 0x0A, (byte) 0x0B, (byte) 0x0C);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("style.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setStage(primaryStage);
        controller.updateRegistradores(registradores);
        controller.updateMemoria(memoria);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Simulador SIC");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}