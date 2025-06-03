// SistemaFilas.java
import java.util.*;
import java.util.stream.Collectors;

public class SistemaFilas {
    private final Map<String, LinkedList<String>> guiches; // Using TreeMap for sorted output of guiches
    private final Conhecimentos conhecimentosService;

    public SistemaFilas(Conhecimentos conhecimentosService) {
        this.guiches = new TreeMap<>(); // TreeMap sorts by key (guiche ID)
        this.conhecimentosService = conhecimentosService;
    }

    public void criarFilas(List<String> ids) {
        for (String id : ids) {
            this.guiches.putIfAbsent(id, new LinkedList<>());
        }
    }

    public void atenderFilas(List<String> ids) {
        for (String id : ids) {
            LinkedList<String> fila = this.guiches.get(id);
            if (fila != null && !fila.isEmpty()) {
                fila.removeFirst();
            }
        }
    }

    public void chegouPessoa(List<String> nomes) {
        for (String nomePessoa : nomes) {
            String melhorGuicheId = null;
            int melhorPosicaoNaLista = -1; // index for insertion
            int melhorProfundidade = Integer.MAX_VALUE; // how deep in queue (0 is front)

            boolean encontrouConhecido = false;

            // Try to find a known person
            for (Map.Entry<String, LinkedList<String>> entry : this.guiches.entrySet()) {
                String guicheIdAtual = entry.getKey();
                LinkedList<String> filaAtual = entry.getValue();
                int indiceUltimoConhecidoNestaFila = -1;

                for (int i = 0; i < filaAtual.size(); i++) {
                    String pessoaNaFila = filaAtual.get(i);
                    if (conhecimentosService.seConhecem(nomePessoa, pessoaNaFila)) {
                        indiceUltimoConhecidoNestaFila = i;
                        encontrouConhecido = true;
                    }
                }

                if (indiceUltimoConhecidoNestaFila != -1) { // Found acquaintance(s) in this queue
                    int profundidadeInsercao = indiceUltimoConhecidoNestaFila + 1;
                    if (profundidadeInsercao < melhorProfundidade) {
                        melhorProfundidade = profundidadeInsercao;
                        melhorGuicheId = guicheIdAtual;
                        melhorPosicaoNaLista = profundidadeInsercao;
                    }
                }
            }

            if (encontrouConhecido && melhorGuicheId != null) {
                this.guiches.get(melhorGuicheId).add(melhorPosicaoNaLista, nomePessoa);
            } else {
                // No known person found, add to the end of the shortest queue
                String guicheMaisCurto = null;
                int tamanhoMinimo = Integer.MAX_VALUE;

                if (this.guiches.isEmpty()) continue; // No queues to add to

                // Find shortest queue (if multiple, TreeMap order will make it deterministic)
                for (Map.Entry<String, LinkedList<String>> entry : this.guiches.entrySet()) {
                    if (entry.getValue().size() < tamanhoMinimo) {
                        tamanhoMinimo = entry.getValue().size();
                        guicheMaisCurto = entry.getKey();
                    }
                }
                if (guicheMaisCurto != null) {
                    this.guiches.get(guicheMaisCurto).addLast(nomePessoa);
                }
            }
        }
    }

    public void desistirPessoa(List<String> nomes) {
        for (String nomePessoa : nomes) {
            for (LinkedList<String> fila : this.guiches.values()) {
                fila.remove(nomePessoa); // remove first occurrence
            }
        }
    }

    public List<String> imprimirFilas() {
        List<String> impressoes = new ArrayList<>();
        for (Map.Entry<String, LinkedList<String>> entry : this.guiches.entrySet()) {
            String filaStr = entry.getValue().stream().collect(Collectors.joining(" "));
            impressoes.add("#" + entry.getKey() + " [ " + filaStr + " ]");
        }
        return impressoes;
    }

    // Methods to process commands

    public void processarComandoCriaFila(String argString) {
        List<String> ids = parseNomesMulti(argString);
        criarFilas(ids);
    }

    public void processarComandoAtendeFila(String argString) {
        List<String> ids = parseNomesMulti(argString);
        atenderFilas(ids);
    }

    public void processarComandoChegou(String argString) {
        List<String> nomes = parseNomesMulti(argString);
        chegouPessoa(nomes);
    }

    public void processarComandoDesiste(String argString) {
        List<String> nomes = parseNomesMulti(argString);
        desistirPessoa(nomes);
    }
    
    private List<String> parseNomesMulti(String argString) {
        return Arrays.stream(argString.split("\\s+"))
                     .filter(s -> !s.isEmpty())
                     .collect(Collectors.toList());
    }
}