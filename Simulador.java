// Simulador.java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Simulador {

    public static void main(String[] args) {
        Conhecimentos conhecimentos = new Conhecimentos();
        SistemaFilas sistemaFilas = new SistemaFilas(conhecimentos);
        List<String> outputLines = new ArrayList<>();

        // Default to System.in if no file is provided, useful for testing.
        // For actual file input, use: new BufferedReader(new FileReader(args[0]));
        // For the example, let's assume we might paste input or use a file.
        // String inputFilePath = "entrada.txt"; // Or args[0] if passed as command line argument

        try (BufferedReader reader = new BufferedReader(args.length > 0 ? new FileReader(args[0]) : new InputStreamReader(System.in))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(":", 2);
                String comando = partes[0].trim();
                String argString = (partes.length > 1) ? partes[1].trim() : "";

                switch (comando) {
                    // Fase 1
                    case "grupo":
                        conhecimentos.processarComandoGrupo(argString);
                        break;
                    case "existe":
                        outputLines.add(conhecimentos.processarComandoExiste(argString));
                        break;
                    case "conhece":
                        outputLines.add(conhecimentos.processarComandoConhece(argString));
                        break;

                    // Fase 2
                    case "criaFila":
                        sistemaFilas.processarComandoCriaFila(argString);
                        break;
                    case "atendeFila":
                        sistemaFilas.processarComandoAtendeFila(argString);
                        break;
                    case "chegou":
                        sistemaFilas.processarComandoChegou(argString);
                        break;
                    case "desiste":
                        sistemaFilas.processarComandoDesiste(argString);
                        break;
                    case "imprime":
                        outputLines.addAll(sistemaFilas.imprimirFilas());
                        break;
                    default:
                        // System.err.println("Comando desconhecido: " + comando);
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler entrada: " + e.getMessage());
            return;
        }

        for (String outLinha : outputLines) {
            System.out.println(outLinha);
        }
    }
}