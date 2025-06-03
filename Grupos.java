import java.util.*;
import java.util.stream.Collectors;

public class Grupos {
    private final Map<String, Set<String>> pessoaParaGrupo;
    // pessoasEmGrupos stores all names that have been assigned to any group.
    private final Set<String> pessoasEmGrupos;

    public Grupos() {
        this.pessoaParaGrupo = new HashMap<>();
        this.pessoasEmGrupos = new HashSet<>();
    }

    public void criarGrupo(List<String> nomesInput) {

        List<String> pessoasQuePodemFormarEsteNovoGrupo = new ArrayList<>();
        for (String nome : nomesInput) {
            if (!this.pessoasEmGrupos.contains(nome)) {
                pessoasQuePodemFormarEsteNovoGrupo.add(nome);
            }
        }

        if (!pessoasQuePodemFormarEsteNovoGrupo.isEmpty()) {
            Set<String> novoGrupoCompartilhado = new HashSet<>(pessoasQuePodemFormarEsteNovoGrupo);
            for (String nome : pessoasQuePodemFormarEsteNovoGrupo) {
                this.pessoaParaGrupo.put(nome, novoGrupoCompartilhado);
                this.pessoasEmGrupos.add(nome);
            }
        }
    }

    public boolean existePessoa(String nome) {
        return this.pessoasEmGrupos.contains(nome);
    }

    public boolean seConhecem(String nome1, String nome2) {

        if (!this.pessoasEmGrupos.contains(nome1) || !this.pessoasEmGrupos.contains(nome2)) {
            return false;
        }

        return this.pessoaParaGrupo.get(nome1) == this.pessoaParaGrupo.get(nome2);
    }

    public void processarComandoGrupo(String argString) {
        List<String> nomes = Arrays.stream(argString.split("\\s+"))
                                   .map(String::trim)
                                   .filter(s -> !s.isEmpty())
                                   .collect(Collectors.toList());
        if (!nomes.isEmpty()) {
            criarGrupo(nomes);
        }
    }

    public String processarComandoExiste(String argString) {
        String nome = argString.trim();
        if (existePessoa(nome)) {
            return "[" + nome + "] existe!";
        } else {
            return "[" + nome + "] NÃO existe!";
        }
    }

    public String processarComandoConhece(String argString) {
        String[] nomesArr = argString.trim().split("\\s+");
        if (nomesArr.length == 2) {
            String nome1 = nomesArr[0];
            String nome2 = nomesArr[1];
            if (seConhecem(nome1, nome2)) {
                return "[" + nome1 + "] conhece [" + nome2 + "]";
            } else {
                return "[" + nome1 + "] NÃO conhece [" + nome2 + "]";
            }
        }
        
        return null; 
    }
}