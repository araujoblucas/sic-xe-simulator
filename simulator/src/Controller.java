import Registradores.Registrador;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class Controller {
    @FXML
    private TableView<Registrador> TabelaReg;

    public void initialize() {
        ObservableList<Registrador> registradores = FXCollections.observableArrayList();
        for (int i = 0; i < 8; i++) {
            registradores.add(new Registrador());
        }
        TabelaReg.setItems(registradores); // Configura os itens da tabela
    }
}
