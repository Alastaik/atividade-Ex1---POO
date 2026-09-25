package app;

import dao.FornecedorDAO;
import dao.ProdutoDAO;
import modelo.Fornecedor;
import modelo.Produto;

import java.sql.SQLException;

// Teste Integridade Referencial
public class TesteEtapa4IntegridadeReferencial {

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("           ETAPA 4: TESTE DE ON DELETE RESTRICT         ");
        System.out.println("=====================================================");

        FornecedorDAO fornecedorDAO = new FornecedorDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        Fornecedor fornecedor = null;
        Produto produto = null;

        try {
            System.out.println("\n[Passo 1] Cadastrando fornecedor para o teste de integridade...");
            fornecedor = new Fornecedor("Distribuidora Global de Periféricos", "(11) 2345-6789");
            fornecedorDAO.inserir(fornecedor);
            System.out.println("Fornecedor cadastrado com sucesso! ID: " + fornecedor.getIdFornecedor());

            System.out.println("\n[Passo 2] Cadastrando produto vinculado ao Fornecedor ID "
                    + fornecedor.getIdFornecedor() + "...");
            produto = new Produto();
            produto.setNome("Headset");
            produto.setPreco(349.90);
            produto.setEstoque(30);
            produto.setIdFornecedor(fornecedor.getIdFornecedor());
            produtoDAO.inserir(produto);
            System.out.println("Produto cadastrado com sucesso! ID: " + produto.getIdProduto() + " | Fornecedor FK: "
                    + produto.getIdFornecedor());

            System.out.println("\n[Passo 3] Tentando remover o fornecedor (ID: " + fornecedor.getIdFornecedor()
                    + ") com produto ainda vinculado...");
            try {
                fornecedorDAO.remover(fornecedor.getIdFornecedor());
                System.err.println("ERRO: O banco permitiu a exclusão! A constraint RESTRICT falhou.");
            } catch (SQLException e) {
                System.out.println("\n SUCESSO : O PostgreSQL bloqueou a exclusão conforme esperado!");
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("DETALHES DO ERRO:");
                System.out.println(
                        "  • SQLState (Código do Erro) : " + e.getSQLState() + " (23503 = foreign_key_violation)");
                System.out.println("  • Mensagem do Banco         : " + e.getMessage());
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("DOCUMENTAÇÃO:");
                System.out.println("O PostgreSQL recusou a operação porque a cláusula ON DELETE RESTRICT proíbe");
                System.out.println("a exclusão de um registro pai (fornecedor) enquanto existirem registros filhos");
                System.out.println("(produto) referenciando sua chave primária através da Foreign Key.");
            }

            System.out.println("\n[Passo 4] Realizando o procedimento correto de exclusão ordenada...");
            System.out.println("1. Removendo primeiro o produto dependente...");
            produtoDAO.remover(produto.getIdProduto());
            System.out.println("Produto ID " + produto.getIdProduto() + " removido.");

            System.out.println("2. Agora removendo o fornecedor...");
            fornecedorDAO.remover(fornecedor.getIdFornecedor());
            System.out.println("Fornecedor ID " + fornecedor.getIdFornecedor() + " removido com sucesso!");

            System.out.println("\n>>> ETAPA 4 CONCLUÍDA COM SUCESSO! <<<");

        } catch (SQLException e) {
            System.err.println("Falha inesperada durante o fluxo da Etapa 4: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
