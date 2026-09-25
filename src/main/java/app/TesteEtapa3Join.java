package app;

import dao.FornecedorDAO;
import dao.ProdutoDAO;
import modelo.Fornecedor;
import modelo.Produto;

import java.sql.SQLException;
import java.util.List;

// Teste JOIN
public class TesteEtapa3Join {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("        ETAPA 3: CONSULTA COM INNER JOIN (PRODUTO + FORNECEDOR)   ");
        System.out.println("================================================================================");

        FornecedorDAO fornecedorDAO = new FornecedorDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        Fornecedor fornecedor = null;
        Produto prod1 = null;
        Produto prod2 = null;

        try {
            System.out.println("\n[1] Criando Fornecedor para o teste de vínculo...");
            fornecedor = new Fornecedor("Alpha Ltda", "(62) 3555-8888");
            fornecedorDAO.inserir(fornecedor);
            System.out.println("Fornecedor criado com ID: " + fornecedor.getIdFornecedor());

            System.out.println(
                    "\n[2] Cadastrando produtos vinculados ao Fornecedor ID " + fornecedor.getIdFornecedor() + "...");

            prod1 = new Produto();
            prod1.setNome("Monitor");
            prod1.setPreco(1299.90);
            prod1.setEstoque(20);
            prod1.setIdFornecedor(fornecedor.getIdFornecedor());
            produtoDAO.inserir(prod1);
            System.out.println("Produto 1 criado [ID " + prod1.getIdProduto() + "]: " + prod1.getNome());

            prod2 = new Produto();
            prod2.setNome("Mouse");
            prod2.setPreco(189.50);
            prod2.setEstoque(45);
            prod2.setIdFornecedor(fornecedor.getIdFornecedor());
            produtoDAO.inserir(prod2);
            System.out.println("Produto 2 criado [ID " + prod2.getIdProduto() + "]: " + prod2.getNome());

            System.out.println(
                    "\n[3] Executando produtoDAO.listarProdutosPorFornecedor(" + fornecedor.getIdFornecedor() + "):");
            List<Produto> produtosDoFornecedor = produtoDAO.listarProdutosPorFornecedor(fornecedor.getIdFornecedor());

            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-6s | %-35s | %-12s | %-8s | %-25s%n",
                    "ID_PRD", "PRODUTO", "PREÇO", "ESTOQUE", "FORNECEDOR (JOIN)");
            System.out.println("--------------------------------------------------------------------------------");

            for (Produto p : produtosDoFornecedor) {
                System.out.printf("%-6d | %-35s | R$ %9.2f | %-8d | %-25s%n",
                        p.getIdProduto(),
                        p.getNome(),
                        p.getPreco(),
                        p.getEstoque(),
                        p.getNomeFornecedor());
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println(
                    "Consulta INNER JOIN executada com sucesso! Itens retornados: " + produtosDoFornecedor.size());

            System.out.println("\n[4] Limpando registros temporários de teste...");
            produtoDAO.remover(prod1.getIdProduto());
            produtoDAO.remover(prod2.getIdProduto());
            fornecedorDAO.remover(fornecedor.getIdFornecedor());
            System.out.println("Registros de teste removidos com sucesso.");

            System.out.println("\n>>> ETAPA 3 CONCLUÍDA COM SUCESSO! <<<");

        } catch (SQLException e) {
            System.err.println("Erro ao executar teste da Etapa 3: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
