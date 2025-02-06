import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import Memória.Memoria;
import Memória.Palavramem;
import Registradores.Registradores;
import Registradores.Registrador;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import Montador.Assembler;
import javafx.stage.Stage;

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
    private TextField arquivoInput;

    @FXML
    private Button carregarArquivoBtn;

    @FXML
    private Button montarBtn;

    @FXML
    private TextArea fonteTextArea;

    @FXML
    private TextArea saidaTextArea;

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
    

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    /////
    @FXML
    private void carregarArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos ASM", "*.asm"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            arquivoInput.setText(file.getAbsolutePath());
            lerArquivo(file);
        }
    }
    /////
    private void lerArquivo(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder conteudo = new StringBuilder();
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
            fonteTextArea.setText(conteudo.toString());  // Atualiza a fonteTextArea com o conteúdo
        } catch (IOException e) {
            fonteTextArea.setText("Erro ao ler o arquivo!");
        }
    }
    /////
    @FXML
    private void limparCampos() {
        arquivoInput.clear();
        fonteTextArea.clear();
        saidaTextArea.clear();
    }
    /////
    @FXML
    private void executarMontador() {
        String arquivoEntrada = arquivoInput.getText();
        String arquivoSaida = "object_code.txt";

        if (arquivoEntrada.isEmpty()) {
            saidaTextArea.setText("Erro: Nenhum arquivo .asm selecionado!");
            return;
        }

        try {
            // Criar uma instância do Assembler e chamar o método de montagem
            Assembler montador = new Assembler();
    
            // Criar um arquivo temporário chamado "codigoFonte.asm" para garantir compatibilidade
            File tempFile = new File("codigoFonte.asm");
            Files.copy(Paths.get(arquivoEntrada), tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    
            // Exibir o conteúdo do arquivo de entrada na fonteTextArea
            lerArquivo(tempFile);

            // Executar a montagem
            montador.main(new String[]{}); 

            // Ler o arquivo gerado pelo montador e exibir na interface
            File arquivoSaidaFile = new File(arquivoSaida);
            if (!arquivoSaidaFile.exists()) {
                saidaTextArea.setText("Erro: Arquivo de saída não encontrado!");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(arquivoSaidaFile))) {
                StringBuilder conteudo = new StringBuilder();
                String linha;
                while ((linha = br.readLine()) != null) {
                    conteudo.append(linha).append("\n");
                }
                saidaTextArea.setText(conteudo.toString());
            }

        } catch (Exception e) {
            saidaTextArea.setText("Erro ao executar o montador: " + e.getMessage());
        }
    }   
}
