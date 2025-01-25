package Assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Assembler {

    public static void main(String[] args) {
        try {
            // Chama a função para carregar as instruções do arquivo
            Map<String, Instruction> instructionSet = loadInstructionsFromFile("instructions.txt");

            ArrayList<Lines> input = readInputFile(instructionSet);

            writeIntermediateFile(input);

            makeSymbolTable(input, instructionSet);



            // Exemplo de como acessar uma instrução específica
            Instruction addInstruction = instructionSet.get("ADD");
            if (addInstruction != null) {
                System.out.println("ADD - Formato: " + addInstruction.getFormat() + ", Opcode: " + addInstruction.getOpcode());
            }

        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado: " + e.getMessage());
        }
    }

    private static void makeSymbolTable(ArrayList<Lines> input, Map<String, Instruction> instructionSet) throws FileNotFoundException {
        File file = new File("codigoFonte.asm");
        ArrayList<Lines> lines = new ArrayList<>();
        int position = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Ignorar linhas vazias ou comentários
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\s+");

                // Processar diretiva START
                if (line.contains("START")) {
                    String valueStart = parts[parts.length == 3 ? 2 : 1];
                    position = Integer.parseInt(valueStart, 16); // Define o endereço inicial

                    if(parts.length == 3) {

                    }

                    continue;
                }

                // Processar diretiva END
                if (parts[0].equals("END")) {
                    continue;
                }

                // Identificar partes da linha
                String label = (parts.length == 3) ? parts[0] : "     ";
                String mnemonic = (parts.length == 3) ? parts[1] : parts[0];
                String value = (parts.length == 3) ? parts[2] : (parts.length == 2 ? parts[1] : "");

                // Processar diretivas específicas
                if (mnemonic.equalsIgnoreCase("WORD")) {
                    position += 3;
                    continue;
                }

                if (mnemonic.equalsIgnoreCase("RESW")) {
                    position += 3 * Integer.parseInt(value);
                    continue;
                }

                // Processar instruções padrão
                Instruction instruction = instructionSet.get(mnemonic);
                if (instruction != null) {
                    position += instruction.getFormat();
                } else {
                    // Registrar erro para mnemônicos desconhecidos
                    System.out.println("Erro: Instrução desconhecida ou mal formatada - " + mnemonic);
                }
            }
        }
    }

    private static ArrayList<Lines> readInputFile(Map<String, Instruction> instructionSet) throws FileNotFoundException {
        File file = new File("codigoFonte.asm");
        ArrayList<Lines> lines = new ArrayList<>();
        int position = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Ignorar linhas vazias ou comentários
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\s+");

                // Processar diretiva START
                if (line.contains("START")) {
                    String labelStart = parts.length == 3 ? parts[0] : "     ";
                    String valueStart = parts[parts.length == 3 ? 2 : 1];

                    System.out.println("Label: " + labelStart + " Value: " + valueStart);
                    position = Integer.parseInt(valueStart, 16); // Define o endereço inicial
                    lines.add(new Lines(position, labelStart, "START", valueStart));
                    continue;
                }

                // Processar diretiva END
                if (parts[0].equals("END")) {
                    lines.add(new Lines(position, "", "END", parts.length > 1 ? parts[1] : ""));
                    continue;
                }

                // Identificar partes da linha
                String label = (parts.length == 3) ? parts[0] : "     ";
                String mnemonic = (parts.length == 3) ? parts[1] : parts[0];
                String value = (parts.length == 3) ? parts[2] : (parts.length == 2 ? parts[1] : "");

                // Processar diretivas específicas
                if (mnemonic.equalsIgnoreCase("WORD")) {
                    position += 3;
                    lines.add(new Lines(position, label, "WORD", value));
                    continue;
                }

                if (mnemonic.equalsIgnoreCase("RESW")) {
                    position += 3 * Integer.parseInt(value);
                    lines.add(new Lines(position, label, "RESW", value));
                    continue;
                }

                // Processar instruções padrão
                Instruction instruction = instructionSet.get(mnemonic);
                if (instruction != null) {
                    position += instruction.getFormat();
                    lines.add(new Lines(position, label, mnemonic, value));
                } else {
                    // Registrar erro para mnemônicos desconhecidos
                    System.out.println("Erro: Instrução desconhecida ou mal formatada - " + mnemonic);
                }
            }
        }

        return lines;
    }




    // Função para carregar as instruções do arquivo e preencher o HashMap
    private static Map<String, Instruction> loadInstructionsFromFile(String fileName) throws FileNotFoundException {
        Map<String, Instruction> instructionSet = new HashMap<>();

        // Abrindo o arquivo
        File file = new File(fileName);
        try (Scanner scanner = new Scanner(file)) {
            // Lendo cada linha do arquivo
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Ignorar linhas vazias ou comentários (caso haja)
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Dividir a linha por espaços ou tabulações
                String[] parts = line.split("\\s+");

                if (parts.length == 3) {
                    String instructionName = parts[0];
                    int format = Integer.parseInt(parts[1]);
                    String opcode = parts[2];

                    // Adicionar ao HashMap
                    instructionSet.put(instructionName, new Instruction(format, opcode));
                }
            }
        }

        return instructionSet;
    }

    public static void writeIntermediateFile(ArrayList<Lines> linesList) {
        try (FileWriter writer = new FileWriter("pass1_intermediate_file.txt")) {
            // Percorre cada linha no ArrayList
            for (Lines line : linesList) {
                // Escreve cada campo de cada objeto Lines separando com um espaço
                writer.write(line.address() + " " + line.label() + " " + line.mnemonic() + " " + line.value() + "\n");
            }
            System.out.println("Arquivo 'intermediate_file.txt' foi criado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao escrever o arquivo: " + e.getMessage());
        }
    }

    public static void writeSymbolTable(int address, String label, String value) {
        try (FileWriter writer = new FileWriter("pass1_symbol_table.txt")) {

            writer.write(String.format("%04X", address).toUpperCase() + " " + label + " " + value + "\n");

            System.out.println("Arquivo 'intermediate_file.txt' foi criado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao escrever o arquivo: " + e.getMessage());
        }
    }

    // Classe para armazenar o formato e opcode de cada instrução
    static class Instruction {
        private int format;   // Formato da instrução (1, 2, 3, 4)
        private String opcode; // Código de operação (opcode)

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

        public Lines(
                 int address,
                 String label,
                 String mnemonic,
                 String value
        ) {
            this.address = address;
            this.label = label;
            this.mnemonic = mnemonic;
            this.value = value;
        }

        public String address() {
            return String.format("%04X", address).toUpperCase();

//            String stringAddress = Integer.toHexString(address);
//            int length = stringAddress.length();
//            System.out.println("Address " + address + "Address lenght" + length);
//            if (length == 4) {
//                return stringAddress;
//            }
//
//            int expectedLength = 4;
//            if (expectedLength - length > 0) {
//                while(expectedLength != length) {
//                    stringAddress = "0" + stringAddress;
//                    System.out.println(stringAddress);
//                    length++;
//                }
//            }
//            return stringAddress;

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
}
