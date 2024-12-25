import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import Memória.Memoria;
import Memória.Palavramem;
import Registradores.Registradores;
import Registradores.Registrador;

public class Controller {

    // Elementos da tabela de registradores
    @FXML
    private TableView<Registrador> TabelaReg;

    @FXML
    private TableColumn<Registrador, String> registerColumn;

    @FXML
    private TableColumn<Registrador, String> valueColumn;

    // Elementos da tabela de memória
    @FXML
    private TableView<Palavramem> TabelaMem;

    @FXML
    private TableColumn<Palavramem, String> memoryIndexColumn;

    @FXML
    private TableColumn<Palavramem, String> memoryValueColumn;

    // Dados das tabelas
    private ObservableList<Registrador> registradores = FXCollections.observableArrayList();
    private ObservableList<Palavramem> memoria = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar tabela de registradores
        registerColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(registradores.indexOf(data.getValue()) + 1))
        );
        valueColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getValueAsString())
        );
        TabelaReg.setItems(registradores);

        // Configurar tabela de memória
        memoryIndexColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(memoria.indexOf(data.getValue())))
        );
        memoryValueColumn.setCellValueFactory(data ->
                new SimpleStringProperty(formatMemoryValue(data.getValue()))
        );
        TabelaMem.setItems(memoria);
    }

    // Método para atualizar os registradores
    public void updateRegistradores(Registradores regs) {
        registradores.clear();
        Registrador[] regsArray = regs.getAllRegistradores();
        registradores.addAll(regsArray);
    }

    // Método para atualizar a memória
    public void updateMemoria(Memoria mem) {
        memoria.clear();
        memoria.addAll(mem.memoria);
    }

    // Formatar valores da memória para exibição
    private String formatMemoryValue(Palavramem palavra) {
        StringBuilder hexValue = new StringBuilder();
        for (byte b : palavra.getBytes()) {
            hexValue.append(String.format("%02X ", b));
        }
        return hexValue.toString().trim();
    }
}
