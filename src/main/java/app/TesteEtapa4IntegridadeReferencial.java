package app;

import dao.FornecedorDAO;
import dao.ProdutoDAO;
import modelo.Fornecedor;
import modelo.Produto;

import java.sql.SQLException;

/**
 * ==============================================================================
 * ETAPA 4: TESTE DE INTEGRIDADE REFERENCIAL (FOREIGN KEY & ON DELETE RESTRICT)
 * ==============================================================================
 * 
 * OBJETIVO:
 * 1. Cadastrar um fornecedor e vincular pelo menos um produto a ele.
 * 2. Tentar remover o fornecedor diretamente via fornecedorDAO.remover().
 * 3. Capturar via bloco try/catch a SQLException disparada pelo PostgreSQL,
 *    comprovando que a constraint fk_produto_fornecedor (ON DELETE RESTRICT)
 *    bloqueia a exclusão para evitar produtos órfãos.
 * 4. Documentar detalhadamente o motivo da recusa pelo SGBD.
 * 
 * NOTA TÉCNICA E DOCUMENTAÇÃO DA RECUSA DO BANCO:
 * O banco de dados PostgreSQL recusou a operação de exclusão porque a chave primária
 * 'id_fornecedor' está sendo referenciada como chave estrangeira (FK) na tabela 'produto'.
 * Ao configurarmos 'ON DELETE RESTRICT' no DDL da Etapa 1, instruímos o motor relacional 
 * a impedir (abortar) qualquer DELETE na tabela pai caso existam registros dependentes 
 * na tabela filha.
 * 
 * Código do Erro no PostgreSQL:
 * - SQLState: 23503 (foreign_key_violation)
 * - Mensagem oficial: "update ou delete em tabela \"fornecedor\" viola restrição de chave 
 *   estrangeira \"fk_produto_fornecedor\" na tabela \"produto\""
 * 
 * Disciplina: ADS1253 - POO com Banco de Dados
 * Professor: Welington Júlio
 */
public class TesteEtapa4IntegridadeReferencial {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("        ETAPA 4: TESTE DE INTEGRIDADE REFERENCIAL (ON DELETE RESTRICT)          ");
        System.out.println("================================================================================");

        FornecedorDAO fornecedorDAO = new FornecedorDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        Fornecedor fornecedor = null;
        Produto produto = null;

        try {
            // Passo 1: Cadastrar um novo fornecedor
            System.out.println("\n[Passo 1] Cadastrando fornecedor para o teste de integridade...");
            fornecedor = new Fornecedor("Distribuidora Global de Periféricos", "(11) 2345-6789");
            fornecedorDAO.inserir(fornecedor);
            System.out.println("✔ Fornecedor cadastrado com sucesso! ID: " + fornecedor.getIdFornecedor());

            // Passo 2: Cadastrar um produto vinculado a esse fornecedor
            System.out.println("\n[Passo 2] Cadastrando produto vinculado ao Fornecedor ID " + fornecedor.getIdFornecedor() + "...");
            produto = new Produto();
            produto.setNome("Headset Gamer Surround 7.1");
            produto.setPreco(349.90);
            produto.setEstoque(30);
            produto.setIdFornecedor(fornecedor.getIdFornecedor()); // Vínculo explícito de FK
            produtoDAO.inserir(produto);
            System.out.println("✔ Produto cadastrado com sucesso! ID: " + produto.getIdProduto() + " | Fornecedor FK: " + produto.getIdFornecedor());

            // Passo 3: Tentativa de exclusão do fornecedor (deve falhar devido ao ON DELETE RESTRICT)
            System.out.println("\n[Passo 3] Tentando remover o fornecedor (ID: " + fornecedor.getIdFornecedor() + ") com produto ainda vinculado...");
            try {
                fornecedorDAO.remover(fornecedor.getIdFornecedor());
                // Caso passe sem exceção, a integridade não funcionou como esperado
                System.err.println("✖ ERRO CRÍTICO: O banco permitiu a exclusão! A constraint RESTRICT falhou.");
            } catch (SQLException e) {
                // Captura do erro real disparado pelo PostgreSQL
                System.out.println("\n✔ SUCESSO NO TESTE: O PostgreSQL bloqueou a exclusão conforme esperado!");
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("DETALHES DO ERRO CAPTURADO DO POSTGRESQL:");
                System.out.println("  • SQLState (Código do Erro) : " + e.getSQLState() + " (23503 = foreign_key_violation)");
                System.out.println("  • Mensagem do Banco         : " + e.getMessage());
                System.out.println("--------------------------------------------------------------------------------");

                /*
                 * DOCUMENTAÇÃO DO RESULTADO:
                 * Por que o banco recusou a operação?
                 * A operação foi abortada pelo PostgreSQL devido à restrição de chave estrangeira
                 * 'fk_produto_fornecedor' definida com 'ON DELETE RESTRICT'. Como o produto
                 * "Headset Gamer Surround 7.1" possui o id_fornecedor apontando para o fornecedor em questão,
                 * a exclusão da linha pai deixaria o produto inconsistente (órfão). O RESTRICT garante
                 * a segurança dos dados impedindo exclusões sem tratamento prévio dos itens dependentes.
                 */
                System.out.println("\n[DOCUMENTAÇÃO DA RECUSA]:");
                System.out.println("O PostgreSQL recusou a operação porque a cláusula ON DELETE RESTRICT proíbe");
                System.out.println("a exclusão de um registro pai (fornecedor) enquanto existirem registros filhos");
                System.out.println("(produto) referenciando sua chave primária através da Foreign Key.");
            }

            // Passo 4: Procedimento correto de limpeza e desvinculação
            System.out.println("\n[Passo 4] Realizando o procedimento correto de exclusão ordenada...");
            System.out.println("1. Removendo primeiro o produto dependente...");
            produtoDAO.remover(produto.getIdProduto());
            System.out.println("✔ Produto ID " + produto.getIdProduto() + " removido.");

            System.out.println("2. Agora removendo o fornecedor livre de vínculos...");
            fornecedorDAO.remover(fornecedor.getIdFornecedor());
            System.out.println("✔ Fornecedor ID " + fornecedor.getIdFornecedor() + " removido com sucesso!");

            System.out.println("\n>>> ETAPA 4 CONCLUÍDA COM 100% DE SUCESSO! <<<");

        } catch (SQLException e) {
            System.err.println("✖ Falha inesperada durante o fluxo da Etapa 4: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
