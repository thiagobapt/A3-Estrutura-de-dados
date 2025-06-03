# A3 Estrutura de Dados

| Integrante | RA |
| ------ | ------ |
| Vinícius Scarpa | 821218970 |
| Guilherme Peres | 822128233 |
| Pedro Henrique Souto Freitas | 822161391 |
| Thiago Baptista | 821223900 |
| Bruno | 1 |

Este projeto em Java simula dois cenários interconectados: um sistema de gerenciamento de grupos sociais onde pessoas se conhecem (Fase 1) e um sistema de gerenciamento de filas de atendimento com regras especiais baseadas nesses grupos (Fase 2). A implementação foca na eficiência e clareza, utilizando estruturas de dados apropriadas para cada funcionalidade.

---

## Estrutura Geral do Projeto

O projeto é modularizado para separar as responsabilidades, com as seguintes classes principais:

1.  **`Simulador.java`**: A classe principal que orquestra a simulação. Ela é responsável por:
    * Iniciar os serviços de gerenciamento de grupos (`Grupos`) e de filas (`SistemaFilas`).
    * Utilizar a classe `Parser` para ler o arquivo de entrada.
    * Interpretar cada comando lido e delegá-lo à classe apropriada (`Grupos` ou `SistemaFilas`).
    * Coletar e imprimir as saídas geradas pelos comandos.

---

## Fase 1: Pessoas Conhecem Pessoas (`Grupos.java`)

Esta classe gerencia os grupos de pessoas e as relações de conhecimento entre elas.

### Objetivo Principal

Armazenar grupos de pessoas que se conhecem e responder a consultas sobre a existência de pessoas e se duas pessoas se conhecem. A restrição fundamental é que "Uma pessoa não poderá estar em dois grupos diferentes."

### Estruturas de Dados Utilizadas e Justificativas

1.  `private final Map<String, Set<String>> pessoaParaGrupo;`
    * **O que armazena:** Associa o nome de uma pessoa (`String`) a um `Set<String>` que representa o grupo ao qual ela pertence. Todos os membros de um mesmo grupo compartilham a **mesma instância** do objeto `Set`.
    * **Justificativa:**
        * **Eficiência de Acesso:** `HashMap` oferece acesso em tempo médio $O(1)$ para obter o grupo de uma pessoa.
        * **Verificação de Conhecimento:** A partilha da mesma instância do `Set` por membros do mesmo grupo permite uma verificação `seConhecem(p1, p2)` extremamente eficiente: basta comparar as referências dos `Set`s ($`pessoaParaGrupo.get(p1) == pessoaParaGrupo.get(p2)`$). Isso é mais rápido do que comparar o conteúdo dos `Set`s.
        * **Conteúdo do Grupo:** O `Set<String>` interno armazena os nomes dos membros do grupo, garantindo unicidade de nomes dentro daquele grupo específico.

2.  `private final Set<String> pessoasEmGrupos;`
    * **O que armazena:** Um conjunto (`HashSet`) que contém os nomes de todas as pessoas que já foram atribuídas a algum grupo.
    * **Justificativa:**
        * **Restrição de Unicidade:** Permite verificar de forma eficiente (tempo médio $O(1)$) se uma pessoa já pertence a algum grupo. Isso é vital para implementar a regra "Uma pessoa não poderá estar em dois grupos diferentes" ao processar o comando `grupo:`.

### Lógica dos Comandos

* `grupo: nome [nome1, nome2, ...]`: Filtra a lista de nomes para incluir apenas pessoas que ainda não estão em `pessoasEmGrupos`. Um novo `Set<String>` é criado para essas pessoas. Cada nova pessoa é adicionada ao mapa `pessoaParaGrupo` (apontando para esta nova instância de `Set`) e também ao `Set pessoasEmGrupos`.
* `existe: nome`: Verifica se o `nome` está presente no `Set pessoasEmGrupos`.
* `conhece: nome1 nome2`: Primeiro, verifica se `nome1` e `nome2` existem (consultando `pessoasEmGrupos`). Se ambos existem, obtém os `Set`s de grupo associados a cada um a partir de `pessoaParaGrupo` e compara se as referências desses `Set`s são idênticas.

---

## Fase 2: Filas Brasileiras (`SistemaFilas.java`)

Esta classe simula o sistema de múltiplas filas de atendimento, aplicando as regras da "fila brasileira".

### Objetivo Principal

Gerenciar a chegada, atendimento e desistência de pessoas em diversas filas (guichês), respeitando a regra de que uma pessoa pode entrar no final da fila ou "furar fila" para ficar junto a um conhecido.

### Dependência

Uma instância de `Grupos` é injetada no construtor de `SistemaFilas` para permitir que a lógica de `chegou` consulte se duas pessoas se conhecem.

### Estruturas de Dados Utilizadas e Justificativas

1.  `private final Map<String, LinkedList<String>> guiches;`
    * **O que armazena:** Associa o identificador (ID) de um guichê (`String`) a uma `LinkedList<String>` que representa a fila de pessoas (seus nomes) naquele guichê.
    * **Justificativa:**
        * **`TreeMap`:** Escolhido para manter as chaves (IDs dos guichês) ordenadas lexicograficamente. Isso garante que, ao executar o comando `imprime:`, as filas sejam listadas sempre na mesma ordem, o que é importante para saídas consistentes e testáveis.
        * **`LinkedList<String>`:** Ideal para filas devido a:
            * **Eficiência nas Extremidades:** Adição (`addLast()`) e remoção (`removeFirst()`) eficientes no início e no fim da lista (chegada normal e atendimento).
            * **Inserção Indexada:** Suporta a inserção de elementos em posições arbitrárias (`add(index, element)`), crucial para a regra da "fila brasileira" (furar fila junto a um conhecido).
            * **Remoção por Valor:** Permite a remoção de um elemento específico pelo seu valor (`remove(Object o)`), útil para o comando `desiste:`.

### Lógica dos Comandos (método `adicionarPessoaAFila` para `chegou`)

O comando `chegou` é o mais complexo e sua lógica é encapsulada principalmente no método `adicionarPessoaAFila`, que é chamado sequencialmente para cada pessoa na lista do comando `chegou`.

1.  **Prioridade para Conhecidos:**
    * Para a pessoa que está chegando (`nomePessoa`), o sistema itera sobre todos os guichês (em ordem de ID, devido ao `TreeMap`).
    * Dentro de cada guichê, verifica todas as pessoas já na fila para encontrar conhecidos usando `gruposService.seConhecem()`.
    * Se conhecidos são encontrados, a posição de inserção é definida como *após o último conhecido encontrado naquela fila específica*. Isso determina a "profundidade" de inserção (o índice).
    * O sistema compara as profundidades de inserção em todas as filas onde conhecidos foram encontrados. `nomePessoa` será colocada na fila que oferecer a *menor profundidade* (o menor índice, mais próximo do início da fila).
    * **Desempate (Conhecidos):** Se múltiplas filas oferecerem a mesma melhor profundidade, a fila com o ID lexicograficamente menor é escolhida (natural devido à ordem de iteração do `TreeMap`).

2.  **Sem Conhecidos (Fallback):**
    * Se `nomePessoa` não encontrar nenhum conhecido em nenhuma fila:
    * Ela é adicionada ao final da fila que estiver *atualmente mais curta*.
    * **Desempate (Sem Conhecidos):** Se múltiplas filas tiverem o mesmo tamanho mínimo, a fila com o ID lexicograficamente menor é escolhida (novamente, um resultado natural da iteração ordenada sobre o `TreeMap`).

Os demais comandos são mais diretos:

* `criaFila`: Adiciona uma nova chave (ID do guichê) ao `TreeMap guiches` com uma `LinkedList` vazia como valor.
* `atendeFila`: Para cada ID de guichê especificado, remove o primeiro elemento (`removeFirst()`) da `LinkedList` correspondente, se a fila não estiver vazia.
* `desiste`: Itera por todas as filas e remove a primeira ocorrência do nome da pessoa especificada de cada `LinkedList` em que for encontrada.
* `imprime`: Itera sobre as entradas do `TreeMap guiches` (já ordenadas por ID) e formata a representação de cada fila para a saída.

---

## Conclusão

A escolha das estruturas de dados (`HashMap`, `HashSet`, `TreeMap`, `LinkedList`) foi guiada pela necessidade de **eficiência** nas operações mais frequentes de cada fase (busca de pessoas e grupos, manipulação de filas) e pela necessidade de manter uma **ordem específica** para a saída (no caso do `TreeMap` para os guichês). A lógica implementada busca seguir estritamente as regras e exemplos fornecidos no problema, processando comandos e chegadas de forma sequencial e determinística.

---
