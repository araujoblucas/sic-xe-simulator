package Montador;

import java.io.*;
import java.util.*;

public class Assembler {

    public static void main(String[] args) {
        try {
            // Chama a função para carregar as instruções do arquivo
            Map<String, Instruction> instructionSet = loadInstructionsFromFile("instructions.txt");

            ArrayList<Lines> input = readInputFile(instructionSet);

            writeIntermediateFile(input);

            makeSymbolTable(input, instructionSet);

            Map<String, Integer> symbolTable = readSymbolTable("pass1_symbol_table.txt");

            File sourceFile = new File("codigoFonte.asm");

            secondPass(sourceFile, instructionSet, symbolTable);

            String intermediateFile = "pass2_intermediate_file.txt";
            String outputFile = "object_code.txt";
            generateObjectCode(intermediateFile, outputFile);

            // Exemplo de como acessar uma instrução específica
            Instruction addInstruction = instructionSet.get("ADD");
            if (addInstruction != null) {
                System.out.println("ADD - Formato: " + addInstruction.getFormat() + ", Opcode: " + addInstruction.getOpcode());
            }

        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        Map<String, Integer> symbolTable = new HashMap<>();

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

                    // Se houver um rótulo, adicioná-lo à tabela de símbolos
                    if (!labelStart.isEmpty() && !labelStart.equals("     ")) {
                        if (symbolTable.containsKey(labelStart)) {
                            System.out.println("Erro: Rótulo duplicado - " + labelStart);
                        } else {
                            symbolTable.put(labelStart, position);
                        }
                    }

                    continue;
                }

                // Processar diretiva END
                if (parts[0].equals("END")) {
                    lines.add(new Lines(position, "", "END", parts.length > 1 ? parts[1] : ""));
                    continue;
                }

                String label = (parts.length == 3) ? parts[0] : "     ";
                String mnemonic = (parts.length == 3) ? parts[1] : parts[0];
                String value = (parts.length == 3) ? parts[2] : (parts.length == 2 ? parts[1] : "");

                System.out.println(line);
                if (line.contains("RSUB")) {
                    System.out.println("CAIUAQUI");
                    if (parts.length == 2) {
                        label = parts[0];
                        mnemonic = parts[1];
                        value = "     ";

                        if (!label.isEmpty() && !label.equals("     ")) {
                            if (symbolTable.containsKey(label)) {
                                System.out.println("Erro: Rótulo duplicado - " + label);
                            } else {
                                symbolTable.put(label, position);
                            }
                        }

                        System.out.println("Position" + position + " Label: " + label + " Mnemonic: " + mnemonic + " Value: " + value);
                        lines.add(new Lines(position, label, mnemonic, value));

                        continue;

                    } else {
                        label = "     ";
                        mnemonic = parts[0];
                        value = "     ";
                    }

                    Instruction instruction = instructionSet.get(mnemonic);
                    if (instruction != null) {
                        position += instruction.getFormat();
                        lines.add(new Lines(position, label, mnemonic, value));
                    } else {
                        System.out.println("Erro: Instrução desconhecida ou mal formatada - " + mnemonic);
                    }

                    continue;

                }

                // Processar diretivas específicas
                if (mnemonic.equalsIgnoreCase("WORD")) {
                    position += 3;
                    lines.add(new Lines(position, label, "WORD", value));

                    // Se houver um rótulo, adicioná-lo à tabela de símbolos
                    if (!label.isEmpty() && !label.equals("     ")) {
                        if (symbolTable.containsKey(label)) {
                            System.out.println("Erro: Rótulo duplicado - " + label);
                        } else {
                            symbolTable.put(label, position);
                        }
                    }
                    continue;
                }

                if (mnemonic.equalsIgnoreCase("RESW")) {
                    position += 3 * Integer.parseInt(value);
                    lines.add(new Lines(position, label, "RESW", value));

                    // Se houver um rótulo, adicioná-lo à tabela de símbolos
                    if (!label.isEmpty() && !label.equals("     ")) {
                        if (symbolTable.containsKey(label)) {
                            System.out.println("Erro: Rótulo duplicado - " + label);
                        } else {
                            symbolTable.put(label, position);
                        }
                    }

                    continue;
                }


                // Processar instruções padrão
                Instruction instruction = instructionSet.get(mnemonic);
                if (instruction != null) {
                    position += instruction.getFormat();
                    lines.add(new Lines(position, label, mnemonic, value));
                } else {
                    System.out.println("Erro: Instrução desconhecida ou mal formatada - " + mnemonic);
                }

                // Se houver um rótulo, adicioná-lo à tabela de símbolos
                System.out.println("Parte:" + parts[0] + " Label:" + label);
                if (!label.isEmpty() && !label.equals("     ")) {
                    if (symbolTable.containsKey(label)) {
                        System.out.println("Erro: Rótulo duplicado - " + label);
                    } else {
                        symbolTable.put(label, position);
                    }
                }

            }

            writeSymbolTable(symbolTable);
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

    private static void writeSymbolTable(Map<String, Integer> symbolTable) {
        try (FileWriter writer = new FileWriter("pass1_symbol_table.txt")) {
            for (Map.Entry<String, Integer> entry : symbolTable.entrySet()) {
                writer.write(String.format("%-10s %04X\n", entry.getKey(), entry.getValue()));
            }
            System.out.println("Arquivo 'pass1_symbol_table.txt' foi criado com sucesso.");
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

    public static void secondPass(File file, Map<String, Instruction> instructionSet, Map<String, Integer> symbolTable) throws IOException {
        int position = 0;
        List<String> intermediateTable = new ArrayList<>();

        try (Scanner scanner = new Scanner(file);
             BufferedWriter writer = new BufferedWriter(new FileWriter("pass2_intermediate_file.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\s+");

                String label = "";
                String mnemonic = "";
                String value = "";

                if (line.contains("START")) {
                    String valueStart = parts[parts.length == 3 ? 2 : 1];
                    position = Integer.parseInt(valueStart, 16);
                    value = parts[parts.length == 3 ? 2 : 1];
                    mnemonic = (parts.length == 3) ? parts[1] : parts[0];
                    value = (parts.length == 3) ? parts[2] : (parts.length == 2 ? parts[1] : "");

                    String outputLine = String.format("%04X %s %s %s %s", position, label, mnemonic, value, valueStart);
                    intermediateTable.add(outputLine);

                    continue;
                }
                if (parts[0].equals("END")) {
                    continue;
                }



                if (line.contains("RSUB")) {
                    label = (parts.length == 2) ? parts[0] : "";
                    mnemonic = (parts.length == 2) ? parts[1] : parts[0];
                    value = "     ";
                } else {
                    label = (parts.length == 3) ? parts[0] : "";
                    mnemonic = (parts.length == 3) ? parts[1] : parts[0];
                    value = (parts.length == 3) ? parts[2] : (parts.length == 2 ? parts[1] : "");
                }

                Instruction instruction = instructionSet.get(mnemonic);
                if (instruction != null) {
                    int format = instruction.getFormat();
                    String opcode = instruction.getOpcode();
                    int operandAddress = 0;
                    if (!value.isEmpty() && symbolTable.containsKey(value)) {
                        operandAddress = symbolTable.get(value);
                    }
                    String objectCode = opcode + String.format("%04X", operandAddress);
                    String outputLine = String.format("%04X %s %s %s %s", position, label, mnemonic, value, objectCode);
                    intermediateTable.add(outputLine);
                    position += format;
                } else {
                    if (mnemonic.equalsIgnoreCase("WORD")) {
                        int wordValue = Integer.parseInt(value);
                        String objectCode = String.format("%06X", wordValue);
                        String outputLine = String.format("%04X %s %s %s %s", position, label, mnemonic, value, objectCode);
                        intermediateTable.add(outputLine);
                        position += 3;
                    } else if (mnemonic.equalsIgnoreCase("RESW")) {
                        position += 3 * Integer.parseInt(value);
                    } else {
                        intermediateTable.add("Erro: Instrução desconhecida - " + mnemonic);
                    }
                }
            }

            for (String line : intermediateTable) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static Map<String, Integer> readSymbolTable(String fileName) {
        Map<String, Integer> symbolTable = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Divide a linha em partes usando espaços como delimitadores
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 2) {
                    String symbol = parts[0];
                    // Converte o valor hexadecimal para um inteiro
                    int address = Integer.parseInt(parts[1], 16);
                    symbolTable.put(symbol, address);
                } else {
                    System.out.println("Linha mal formatada: " + line);
                }
            }
            System.out.println("Arquivo '" + fileName + "' foi lido com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        return symbolTable;
    }

    public static void generateObjectCode(String intermediateFile, String outputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(intermediateFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String programName = "";
            int startAddress = 0;
            int lastAddress = 0;
            List<String> textRecords = new ArrayList<>();
            StringBuilder currentRecord = new StringBuilder();
            int currentRecordStart = -1;
            int currentRecordLength = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                // Divide a linha em partes, considerando espaços e tabulações
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 4) continue;

                int address = Integer.parseInt(parts[0], 16);
                String label = parts[1];
                String mnemonic = parts[2];
                String objectCode = parts[3];

                System.out.println("Linha processada: " + line);
                System.out.println("Current Record: " + currentRecord.toString());
                System.out.println("Current Record Start: " + Integer.toHexString(currentRecordStart));
                System.out.println("Current Record Length: " + currentRecordLength);
                System.out.println("Text Records: " + textRecords.toString());

                if (mnemonic.equals("START")) {
                    programName = label;
                    startAddress = address;
                    lastAddress = startAddress;
                } else if (mnemonic.equals("END")) {
                    // Finaliza o último registro de texto, se houver
                    if (currentRecordLength > 0) {
                        textRecords.add(String.format("T^%06X^%02X^%s", currentRecordStart, currentRecordLength, currentRecord.toString()));
                    }
                    break;
                } else {
                    if (currentRecordStart == -1) {
                        currentRecordStart = address;
                    }

                    int objectCodeLength = objectCode.length() / 2; // Cada par de caracteres hexadecimais representa 1 byte

                    // Verifica se adicionar este código objeto excederia 30 bytes
                    if (currentRecordLength + objectCodeLength >= 30) {
                        // Finaliza o registro de texto atual
                        textRecords.add(String.format("T^%06X^%02X^%s", currentRecordStart, currentRecordLength, currentRecord.toString()));
                        // Inicia um novo registro de texto
                        currentRecord = new StringBuilder();
                        currentRecordStart = address;
                        currentRecordLength = 0;
                    }

                    // Adiciona o código objeto ao registro atual, se existir
                    if (!objectCode.equals("null")) {
                        currentRecord.append(objectCode).append("^");
                        currentRecordLength += objectCodeLength;
                    }

                    // Atualiza lastAddress considerando o tamanho do código objeto
                    lastAddress = address + objectCodeLength;
                }
            }

            // Escreve o registro de cabeçalho
            int programLength = lastAddress - startAddress;
            writer.write(String.format("H^%-6s^%06X^%06X\n", programName, startAddress, programLength));

            // Escreve os registros de texto
            for (String record : textRecords) {
                // Remove o '^' final antes de escrever o registro
                writer.write(record.substring(0, record.length() - 1) + "\n");
            }

            // Escreve o registro de fim
            writer.write(String.format("E^%06X\n", startAddress));

            System.out.println("Código objeto gerado com sucesso no arquivo: " + outputFile);

        } catch (IOException e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

}
