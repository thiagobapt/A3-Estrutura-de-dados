// Conhecimentos.java
import java.util.*;
import java.util.stream.Collectors;

public class Conhecimentos {
    // Maps a person's name to the set representing their group.
    // All members of the same group will share the same Set object.
    private final Map<String, Set<String>> pessoaParaGrupo;
    // Keeps track of all people assigned to any group, to enforce uniqueness.
    private final Set<String> pessoasEmGrupos;

    public Conhecimentos() {
        this.pessoaParaGrupo = new HashMap<>();
        this.pessoasEmGrupos = new HashSet<>();
    }

    /**
     * Creates a group of people who know each other.
     * If any person is already in another group, the command is ignored for those,
     * and only new, unassigned people form the group.
     * If all people provided are already in groups, or fewer than 2 new people, no new group is effectively formed.
     */
    public void criarGrupo(List<String> nomes) {
        List<String> pessoasParaNovoGrupo = new ArrayList<>();
        for (String nome : nomes) {
            if (!this.pessoasEmGrupos.contains(nome)) {
                pessoasParaNovoGrupo.add(nome);
            }
        }

        // A group needs at least two people, or one if it's the only way to register them.
        // Based on "grupo: nome [nome1, nome2, ...]", it implies a group is formed.
        // If the problem implies strictness (all must be new), then check first.
        // Current interpretation: only new people form the group.
        if (pessoasParaNovoGrupo.size() > 0) { // Allow single-person "groups" if they are mentioned in a group command
            Set<String> novoGrupo = new HashSet<>(pessoasParaNovoGrupo);
            for (String nome : pessoasParaNovoGrupo) {
                this.pessoaParaGrupo.put(nome, novoGrupo);
                this.pessoasEmGrupos.add(nome);
            }
        }
    }

    /**
     * Checks if a person exists in any group.
     * @return True if the person exists, false otherwise.
     */
    public boolean existePessoa(String nome) {
        return this.pessoasEmGrupos.contains(nome);
    }

    /**
     * Checks if two people know each other (are in the same group).
     * @return True if they are in the same group, false otherwise or if one/both don't exist.
     */
    public boolean seConhecem(String nome1, String nome2) {
        if (!existePessoa(nome1) || !existePessoa(nome2)) {
            return false;
        }
        // They know each other if they point to the same group Set object
        return this.pessoaParaGrupo.get(nome1) == this.pessoaParaGrupo.get(nome2);
    }

    // Methods to process commands and generate output strings for Phase 1

    public void processarComandoGrupo(String argString) {
        List<String> nomes = Arrays.stream(argString.split("\\s+"))
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
        String[] nomesArr = argString.split("\\s+");
        if (nomesArr.length == 2) {
            String nome1 = nomesArr[0].trim();
            String nome2 = nomesArr[1].trim();
            if (seConhecem(nome1, nome2)) {
                return "[" + nome1 + "] conhece [" + nome2 + "]";
            } else {
                return "[" + nome1 + "] NÃO conhece [" + nome2 + "]";
            }
        }
        return "Comando conhece mal formatado: " + argString; // Should not happen with valid input
    }
}