import java.util.*;
import java.util.stream.Collectors;

public class SistemaFilas {
    private final Map<String, LinkedList<String>> guiches;
    private final Grupos conhecimentosService;

    public SistemaFilas(Grupos conhecimentosService) {
        this.guiches = new TreeMap<>(); 
        this.conhecimentosService = conhecimentosService;
    }

    private List<String> parseNomesMulti(String argString) {
        return Arrays.stream(argString.split("\\s+"))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .collect(Collectors.toList());
    }

    public void processarComandoCriaFila(String argString) {
        List<String> ids = parseNomesMulti(argString);
        for (String id : ids) {
            this.guiches.putIfAbsent(id, new LinkedList<>());
        }
    }

    public void processarComandoAtendeFila(String argString) {
        List<String> ids = parseNomesMulti(argString);
        for (String id : ids) {
            LinkedList<String> fila = this.guiches.get(id);
            if (fila != null && !fila.isEmpty()) {
                fila.removeFirst();
            }
        }
    }
    
    private void adicionarPessoaAFila(String nomePessoa) {
        if (this.guiches.isEmpty()) {
            return; 
        }

        String melhorGuicheParaConhecido = null;
        int melhorPosicaoParaConhecido = -1; 
        int melhorProfundidadeParaConhecido = Integer.MAX_VALUE;


        // Passo 1: checar por conhecidos para achar o melhor lugar.
        // Iterar os guiches usando keySet() garante uma ordenação para os IDs dos guiches,
        // garantindo uma forma determinística de selecionar uma fila caso 2 filas tenham o mesmo melhor lugar.

        for (String guicheIdAtual : this.guiches.keySet()) {
            LinkedList<String> filaAtual = this.guiches.get(guicheIdAtual);
            int indiceUltimoConhecidoNestaFila = -1;

            for (int i = 0; i < filaAtual.size(); i++) {
                if (conhecimentosService.seConhecem(nomePessoa, filaAtual.get(i))) {
                    indiceUltimoConhecidoNestaFila = i;
                }
            }

            if (indiceUltimoConhecidoNestaFila != -1) {
                int profundidadeAtual = indiceUltimoConhecidoNestaFila + 1;
                if (profundidadeAtual < melhorProfundidadeParaConhecido) {
                    melhorProfundidadeParaConhecido = profundidadeAtual;
                    melhorPosicaoParaConhecido = profundidadeAtual;
                    melhorGuicheParaConhecido = guicheIdAtual;
                }
            }
        }

        if (melhorGuicheParaConhecido != null) {
            this.guiches.get(melhorGuicheParaConhecido).add(melhorPosicaoParaConhecido, nomePessoa);
        } else {
            String guicheMaisCurto = null;
            int tamanhoMinimo = Integer.MAX_VALUE;

            for (String guicheId : this.guiches.keySet()) {
                LinkedList<String> filaAtual = this.guiches.get(guicheId);
                if (filaAtual.size() < tamanhoMinimo) {
                    tamanhoMinimo = filaAtual.size();
                    guicheMaisCurto = guicheId;
                }
            }
            
            if (guicheMaisCurto != null) {
                this.guiches.get(guicheMaisCurto).addLast(nomePessoa);
            }

        }
    }

    public void processarComandoChegou(String argString) {
        List<String> nomes = parseNomesMulti(argString);
        for (String nome : nomes) {
            adicionarPessoaAFila(nome);
        }
    }

    public void processarComandoDesiste(String argString) {
        List<String> nomes = parseNomesMulti(argString);
        for (String nomePessoa : nomes) {
            for (LinkedList<String> fila : this.guiches.values()) {
                fila.remove(nomePessoa); 
            }
        }
    }

    public List<String> processarComandoImprime() {
        List<String> saidaImpressao = new ArrayList<>();
        for (Map.Entry<String, LinkedList<String>> entry : this.guiches.entrySet()) {
            String nomesNaFila = String.join(" ", entry.getValue());
            saidaImpressao.add("#" + entry.getKey() + " [ " + nomesNaFila + " ]");
        }
        return saidaImpressao;
    }
}