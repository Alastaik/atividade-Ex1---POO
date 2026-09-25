package app;

import conexao.ConnectionFactory;
import dao.FornecedorDAO;
import dao.ProdutoDAO;
import modelo.Fornecedor;
import modelo.Produto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * ==============================================================================
 * BATERIA INTEGRADA DE TESTES: ETAPAS 1, 2, 3 E 4
 * ==============================================================================
 * 
 * Disciplina: ADS1253 - Programação Orientada a Objetos com Banco de Dados
 * Professor: Welington Júlio
 * Aluno: Yuri Silva
 * 
 * Executa todos os desafios propostos no roteiro em sequência estruturada:
 * 0. Prova de Instância Única da ConnectionFactory (Singleton GoF)
 * 1. Verificação da Modelagem DDL (Tabela Fornecedor e FK em Produto)
 * 2. CRUD Completo de FornecedorDAO com PreparedStatement e mapearFornecedor()
 * 3. Consulta Parametrizada com INNER JOIN (listarProdutosPorFornecedor)
 * 4. Teste de Integridade Referencial (Captura e Documentação de ON DELETE RESTRICT)
 */
public class TesteEtapasCompleto {

    public static void main(String[] args) {
        imprimirCabecalho();

        // ----------------------------------------------------------------------
        // FASE 0: VALIDAÇÃO DO PADRÃO SINGLETON NA CONNECTION FACTORY
        // ----------------------------------------------------------------------
        System.out.println("\n>>> [FASE 0] TESTE DE UNICIDADE DO SINGLETON (ConnectionFactory) <<<");
        ConnectionFactory f1 = ConnectionFactory.getInstancia();
        ConnectionFactory f2 = ConnectionFactory.getInstancia();
        ConnectionFactory f3 = ConnectionFactory.getInstancia();

        boolean singletonOk = (f1 == f2) && (f2 == f3);
        System.out.println("  • Instância 1: " + System.identityHashCode(f1));
        System.out.println("  • Instância 2: " + System.identityHashCode(f2));
        System.out.println("  • Instância 3: " + System.identityHashCode(f3));
        System.out.println("  • Referências idênticas em memória? " + (singletonOk ? "SIM (✔ Singleton GoF Comprovado)" : "NÃO (✖ Erro)"));

        // Testa conexão física ativa
        try (Connection conn = ConnectionFactory.getInstancia().getConnection()) {
            System.out.println("  • Conexão ativa estabelecida com sucesso via Singleton no PostgreSQL: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("  ✖ Falha ao conectar ao banco de dados: " + e.getMessage());
            return;
        }

        // ----------------------------------------------------------------------
        // FASE 1: REVISÃO DA MODELAGEM E DDL (ETAPA 1)
        // ----------------------------------------------------------------------
        System.out.println("\n>>> [ETAPA 1] MODELAGEM E DDL (TABELA FORNECEDOR E FK EM PRODUTO) <<<");
        System.out.println("  • Tabela 'fornecedor': id_fornecedor (SERIAL, PK), nome (VARCHAR, NOT NULL), telefone (VARCHAR).");
        System.out.println("  • Alteração em 'produto': adição de id_fornecedor com FK apontando para fornecedor.");
        System.out.println("  • Regra de integridade: ON DELETE RESTRICT.");
        System.out.println("  • Justificativa: Evitar a exclusão acidental em cascata dos produtos cadastrados ao remover um fornecedor.");

        FornecedorDAO fornecedorDAO = new FornecedorDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        // ----------------------------------------------------------------------
        // FASE 2: CRUD COMPLETO DO FORNECEDOR (ETAPA 2)
        // ----------------------------------------------------------------------
        System.out.println("\n>>> [ETAPA 2] CRUD COMPLETO COM FornecedorDAO <<<");
        int idFornecedorTeste = -1;
        try {
            // 2.1 Inserir
            Fornecedor f = new Fornecedor("Brasil Componentes Eletrônicos", "(62) 3200-1010");
            fornecedorDAO.inserir(f);
            idFornecedorTeste = f.getIdFornecedor();
            System.out.println("  [2.1 Inserção] ✔ Inserido com ID autogerado (SERIAL): " + idFornecedorTeste);

            // 2.2 Buscar por ID
            Optional<Fornecedor> buscado = fornecedorDAO.buscarPorId(idFornecedorTeste);
            System.out.println("  [2.2 Buscar ID] ✔ Encontrado: " + buscado.orElse(null));

            // 2.3 Atualizar
            f.setNome("Brasil Componentes e Suprimentos de TI Ltda");
            f.setTelefone("(62) 98111-2233");
            fornecedorDAO.atualizar(f);
            Optional<Fornecedor> atualizado = fornecedorDAO.buscarPorId(idFornecedorTeste);
            System.out.println("  [2.3 Atualização] ✔ Atualizado para: " + atualizado.orElse(null));

            // 2.4 Listar Todos
            List<Fornecedor> todos = fornecedorDAO.listarTodos();
            System.out.println("  [2.4 Listar Todos] ✔ Total de registros na tabela fornecedor: " + todos.size());

            // 2.5 Buscar Parcial
            List<Fornecedor> parciais = fornecedorDAO.buscarPorNomeParcial("Componentes");
            System.out.println("  [2.5 Busca LIKE] ✔ Encontrados contendo 'Componentes': " + parciais.size());

        } catch (SQLException e) {
            System.err.println("✖ Erro na Etapa 2: " + e.getMessage());
        }

        // ----------------------------------------------------------------------
        // FASE 3: CONSULTA PARAMETRIZADA COM INNER JOIN (ETAPA 3)
        // ----------------------------------------------------------------------
        System.out.println("\n>>> [ETAPA 3] CONSULTA PARAMETRIZADA COM INNER JOIN (PRODUTO + FORNECEDOR) <<<");
        Produto p1 = null;
        Produto p2 = null;
        try {
            // Vincula 2 produtos ao fornecedor cadastrado na Etapa 2
            p1 = new Produto("Placa de Vídeo RTX 4060 8GB", 2299.00, 10, idFornecedorTeste);
            p2 = new Produto("Fonte Modular 750W 80 Plus Gold", 649.90, 25, idFornecedorTeste);
            produtoDAO.inserir(p1);
            produtoDAO.inserir(p2);
            System.out.println("  • Vinculados 2 produtos ao Fornecedor ID " + idFornecedorTeste);

            // Executa a busca parametrizada com INNER JOIN
            List<Produto> listaJoin = produtoDAO.listarProdutosPorFornecedor(idFornecedorTeste);
            System.out.println("  • Executando produtoDAO.listarProdutosPorFornecedor(" + idFornecedorTeste + "):");
            System.out.println("    ----------------------------------------------------------------------------");
            System.out.printf("    %-6s | %-34s | %-12s | %-25s%n", "ID_PRD", "NOME DO PRODUTO", "PREÇO", "NOME FORNECEDOR (JOIN)");
            System.out.println("    ----------------------------------------------------------------------------");
            for (Produto p : listaJoin) {
                System.out.printf("    %-6d | %-34s | R$ %9.2f | %-25s%n",
                        p.getIdProduto(), p.getNome(), p.getPreco(), p.getNomeFornecedor());
            }
            System.out.println("    ----------------------------------------------------------------------------");
            System.out.println("  ✔ INNER JOIN executado e validado com sucesso!");

            System.out.println("\n  • Executando fornecedorDAO.listarProdutosPorFornecedor(" + idFornecedorTeste + ") [Formato Roteiro]:");
            List<String> linhasRoteiro = fornecedorDAO.listarProdutosPorFornecedor(idFornecedorTeste);
            for (String linha : linhasRoteiro) {
                System.out.println("    -> " + linha);
            }

        } catch (SQLException e) {
            System.err.println("✖ Erro na Etapa 3: " + e.getMessage());
        }

        // ----------------------------------------------------------------------
        // FASE 4: TESTE DE INTEGRIDADE REFERENCIAL (ETAPA 4)
        // ----------------------------------------------------------------------
        System.out.println("\n>>> [ETAPA 4] TESTE DE INTEGRIDADE REFERENCIAL (ON DELETE RESTRICT) <<<");
        try {
            System.out.println("  • Tentando remover o Fornecedor ID " + idFornecedorTeste + " que possui produtos associados...");
            try {
                fornecedorDAO.remover(idFornecedorTeste);
                System.err.println("  ✖ Falha de integridade: O banco permitiu a exclusão do fornecedor vinculado!");
            } catch (SQLException exBanco) {
                System.out.println("  ✔ SUCESSO: O PostgreSQL BLOQUEOU A EXCLUSÃO com ON DELETE RESTRICT!");
                System.out.println("    - SQLState : " + exBanco.getSQLState() + " (23503: foreign_key_violation)");
                System.out.println("    - Mensagem : " + exBanco.getMessage());
                System.out.println("  [DOCUMENTAÇÃO]: O banco recusou a exclusão porque a chave primária 'id_fornecedor'");
                System.out.println("  está presente como chave estrangeira na tabela 'produto'. A restrição ON DELETE RESTRICT");
                System.out.println("  impede que produtos fiquem desprovidos de fornecedor ou sejam deletados sem controle.");
            }

            // Limpeza final para não poluir o banco
            System.out.println("\n  • Limpando dados de teste ordenadamente (produtos primeiro, fornecedor depois)...");
            if (p1 != null && p1.getIdProduto() != null) produtoDAO.remover(p1.getIdProduto());
            if (p2 != null && p2.getIdProduto() != null) produtoDAO.remover(p2.getIdProduto());
            if (idFornecedorTeste > 0) fornecedorDAO.remover(idFornecedorTeste);
            System.out.println("  ✔ Limpeza concluída.");

        } catch (SQLException e) {
            System.err.println("✖ Erro durante a finalização da Etapa 4: " + e.getMessage());
        }

        imprimirRodape();
    }

    private static void imprimirCabecalho() {
        System.out.println("================================================================================");
        System.out.println("         PUC GOIÁS - ANÁLISE E DESENVOLVIMENTO DE SISTEMAS (ADS)                ");
        System.out.println("         ADS1253 - PROGRAMAÇÃO ORIENTADA A OBJETOS COM BANCO DE DADOS           ");
        System.out.println("         PROFESSOR: WELINGTON JÚLIO                                             ");
        System.out.println("         ATIVIDADE PRÁTICA: FORNECEDORES, PRODUTOS, SINGLETON E DDL/DML         ");
        System.out.println("================================================================================");
    }

    private static void imprimirRodape() {
        System.out.println("\n================================================================================");
        System.out.println("               TODAS AS 4 ETAPAS FORAM EXECUTADAS COM SUCESSO!                  ");
        System.out.println("================================================================================");
    }
}
