package Montador;

import java.io.*;
import java.util.*;

public class Assembler {

    public static void main(String[] args) {
        // Valores padrão para compatibilidade com execução direta
        String arquivoEntrada = "codigoFonte.asm";
        String arquivoSaida = "object_code.txt";

        Assembler montador = new Assembler();
        montador.montar(arquivoEntrada, arquivoSaida);
    }

    public void montar(String arquivoEntrada, String arquivoSaida) {
        try {
            // Carregar as instruções do arquivo
            Map<String, Instruction> instructionSet = loadInstructionsFromFile("instructions.txt");

            // Ler o arquivo de entrada
            ArrayList<Lines> input = readInputFile(arquivoEntrada, instructionSet);

            // Criar tabela de símbolos
            Map<String, Integer> symbolTable = makeSymbolTable(input, instructionSet);

            // Segunda passagem
            secondPass(new File(arquivoEntrada), instructionSet, symbolTable);

            // Gerar código objeto
            String intermediateFile = "pass2_intermediate_file.txt";
            generateObjectCode(intermediateFile, arquivoSaida);

            System.out.println("Montagem concluída. Código objeto salvo em: " + arquivoSaida);

        } catch (FileNotFoundException e) {
            System.out.println("Erro: Arquivo não encontrado - " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro de I/O: " + e.getMessage());
        }
    }

    private static Map<String, Integer> makeSymbolTable(ArrayList<Lines> input, Map<String, Instruction> instructionSet) throws FileNotFoundException {
        Map<String, Integer> symbolTable = new HashMap<>();
        int position = 0;

        for (Lines line : input) {
            String label = line.label();
            if (!label.equals("     ")) {
                if (symbolTable.containsKey(label)) {
                    System.out.println("Erro: Rótulo duplicado - " + label);
                } else {
                    symbolTable.put(label, position);
                }
            }
            Instruction instruction = instructionSet.get(line.mnemonic());
            if (instruction != null) {
                position += instruction.getFormat();
            }
        }

        writeSymbolTable(symbolTable);
        return symbolTable;
    }

    private static void secondPass(File file, Map<String, Instruction> instructionSet, Map<String, Integer> symbolTable) throws IOException {
        try (Scanner scanner = new Scanner(file);
             BufferedWriter writer = new BufferedWriter(new FileWriter("pass2_intermediate_file.txt"))) {
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                String mnemonic = (parts.length >= 2) ? parts[1] : parts[0];

                Instruction instruction = instructionSet.get(mnemonic);
                if (instruction != null) {
                    writer.write(line + " - Instrução válida\n");
                } else {
                    writer.write(line + " - Erro: Instrução desconhecida\n");
                }
                writer.newLine();
            }
        }
    }

    private static ArrayList<Lines> readInputFile(String arquivoEntrada, Map<String, Instruction> instructionSet) throws FileNotFoundException {
        File file = new File(arquivoEntrada);
        ArrayList<Lines> lines = new ArrayList<>();
        int position = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                String label = (parts.length == 3) ? parts[0] : "     ";
                String mnemonic = (parts.length == 3) ? parts[1] : parts[0];
                String value = (parts.length == 3) ? parts[2] : (parts.length == 2 ? parts[1] : "");

                Instruction instruction = instructionSet.get(mnemonic);
                if (instruction != null) {
                    position += instruction.getFormat();
                    lines.add(new Lines(position, label, mnemonic, value));
                }
            }
        }
        return lines;
    }

    private static Map<String, Instruction> loadInstructionsFromFile(String fileName) throws FileNotFoundException {
        Map<String, Instruction> instructionSet = new HashMap<>();
        File file = new File(fileName);
    
        System.out.println("Tentando carregar: " + file.getAbsolutePath());  // Verifica caminho do arquivo
    
        if (!file.exists()) {
            System.out.println("Erro: Arquivo não encontrado - " + file.getAbsolutePath());
            return instructionSet;
        }
    
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
    
                String[] parts = line.split("\\s+");
                if (parts.length == 3) {
                    instructionSet.put(parts[0], new Instruction(Integer.parseInt(parts[1]), parts[2]));
                }
            }
        }
    
        System.out.println("Instruções carregadas: " + instructionSet.size()); // Mostra quantas instruções foram lidas
        return instructionSet;
    }

    public static void generateObjectCode(String intermediateFile, String outputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(intermediateFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Código objeto gerado com sucesso.");

        } catch (IOException e) {
            System.err.println("Erro ao gerar o código objeto: " + e.getMessage());
        }
    }

    private static void writeSymbolTable(Map<String, Integer> symbolTable) {
        try (FileWriter writer = new FileWriter("pass1_symbol_table.txt")) {
            for (Map.Entry<String, Integer> entry : symbolTable.entrySet()) {
                writer.write(String.format("%-10s %04X\n", entry.getKey(), entry.getValue()));
            }
        } catch (IOException e) {
            System.out.println("Erro ao escrever a tabela de símbolos: " + e.getMessage());
        }
    }

    static class Instruction {
        private int format;
        private String opcode;

        public Instruction(int format, String opcode) {
            this.format = format;
            this.opcode = opcode;
        }

        public int getFormat() {
            return format;
        }

        public String getOpcode() {
            return opcode;
        }
    }

    static class Lines {
        private int address;
        private String label;
        private String mnemonic;
        private String value;

        public Lines(int address, String label, String mnemonic, String value) {
            this.address = address;
            this.label = label;
            this.mnemonic = mnemonic;
            this.value = value;
        }

        public String address() {
            return String.format("%04X", address).toUpperCase();
        }

        public String label() {
            return label;
        }

        public String mnemonic() {
            return mnemonic;
        }

        public String value() {
            return value;
        }
    }

    public static Map<String, Integer> readSymbolTable(String fileName) {
        Map<String, Integer> symbolTable = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 2) {
                    symbolTable.put(parts[0], Integer.parseInt(parts[1], 16));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler a tabela de símbolos: " + e.getMessage());
        }
        return symbolTable;
    }
}
