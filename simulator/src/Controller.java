import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import Registradores.Registradores;
import Registradores.Registrador;

public class Controller {

    @FXML
    private TableView<Registrador> TabelaReg;

    @FXML
    private TableColumn<Registrador, String> registerColumn;

    @FXML
    private TableColumn<Registrador, String> valueColumn;

    private ObservableList<Registrador> registradores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Chatgptzei aqui revisar dps
        registerColumn.setCellValueFactory(new PropertyValueFactory<>("registerName"));
        valueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValor()));

        // Inicialize o TableView
        TabelaReg.setItems(registradores);
    }

    // Método para atualizar os dados dos registradores
    public void updateRegistradores(Registradores regs) {
        registradores.clear();
        Registrador[] regsArray = regs.getAllRegistradores();

        for (int i = 0; i < regsArray.length; i++) {
            registradores.add(regsArray[i]);
        }
    }
}
