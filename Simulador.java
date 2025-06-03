import Parse.Parser;
import java.util.ArrayList;
import java.util.List;

public class Simulador {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Uso: java Simulador <caminho_para_arquivo_de_entrada>");
            return;
        }
        String filePath = args[0];

        Grupos grupos = new Grupos();
        SistemaFilas sistemaFilas = new SistemaFilas(grupos);
        List<String> outputLines = new ArrayList<>();
        
        Parser parser = new Parser(filePath);

        while (parser.hasNext()) {
            String linha = parser.nextLine();
            
            if (linha == null) { 
                continue;
            }

            linha = linha.trim();
            
            if (linha.isEmpty()) {
                continue;
            }

            String[] partes = linha.split(":", 2);
            String comando = partes[0].trim();
            String argString = (partes.length > 1) ? partes[1].trim() : "";

            String comandoOutput = null;

            switch (comando) {
                // Fase 1
                case "grupo":
                    grupos.processarComandoGrupo(argString);
                    break;
                case "existe":
                    comandoOutput = grupos.processarComandoExiste(argString);
                    break;
                case "conhece":
                    comandoOutput = grupos.processarComandoConhece(argString);
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
                    outputLines.addAll(sistemaFilas.processarComandoImprime());
                    break;
                default:
                    break;
            }

            if (comandoOutput != null) {
                outputLines.add(comandoOutput);
            }
        }

        parser.close();

        for (String outLinha : outputLines) {
            System.out.println(outLinha);
        }
    }
}