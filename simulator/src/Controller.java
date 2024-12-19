import Registradores.Registradores;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class Controller {
    @FXML
    private TableView<Registradores> TabelaReg;

    public void initialize() {
        ObservableList<Registradores> registradores = FXCollections.observableArrayList();
        
        TabelaReg.setItems(registradores); // Configura os itens da tabela
    }
}