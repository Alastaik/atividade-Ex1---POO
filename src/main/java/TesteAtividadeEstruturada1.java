import dao.FornecedorDAO;
import dao.ProdutoDAO;
import modelo.Fornecedor;
import modelo.Produto;

import java.sql.SQLException;
import java.util.List;

// Teste Atividade Estruturada 1
public class TesteAtividadeEstruturada1 {

    public static void main(String[] args) {
        FornecedorDAO dao = new FornecedorDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        Produto produtoCriado = null;
        Fornecedor fornecedorCriado = null;

        try {
            System.out.println("=== ATIVIDADE ESTRUTURADA 1 (Ex1) ===");

            System.out.println("\n[4.1] Instanciando e inserindo novo Fornecedor...");
            fornecedorCriado = new Fornecedor();
            fornecedorCriado.setNome("Tech Distribuidora Brasil Ltda");
            fornecedorCriado.setTelefone("(62) 3333-4444");

            dao.inserir(fornecedorCriado);
            System.out.println("Fornecedor cadastrado com sucesso! ID Gerado: " + fornecedorCriado.getIdFornecedor());

            System.out.println("\n[4.2] Inserindo Produto associado ao Fornecedor recém-gerado...");
            produtoCriado = new Produto();
            produtoCriado.setNome("Teclado Mecânico RGB Switch Blue");
            produtoCriado.setPreco(289.90);
            produtoCriado.setEstoque(15);
            produtoCriado.setIdFornecedor(fornecedorCriado.getIdFornecedor());

            produtoDAO.inserir(produtoCriado);
            System.out.println("Produto cadastrado com sucesso! ID Gerado: " + produtoCriado.getIdProduto()
                    + " | Vinculado ao Fornecedor ID: " + produtoCriado.getIdFornecedor());

            System.out.println("\n[4.3] Executando listarProdutosPorFornecedor(" + fornecedorCriado.getIdFornecedor() + "):");
            List<String> produtosFormatados = dao.listarProdutosPorFornecedor(fornecedorCriado.getIdFornecedor());
            for (String item : produtosFormatados) {
                System.out.println(" -> " + item);
            }

            System.out.println("\n[4.4] Tentando remover o fornecedor com produto vinculado (Teste ON DELETE RESTRICT)...");
            try {
                dao.remover(fornecedorCriado.getIdFornecedor());
                System.err.println("ERRO: O fornecedor foi removido! A chave estrangeira com RESTRICT falhou.");
            } catch (SQLException e) {
                System.out.println("SUCESSO: Exclusão bloqueada pelo PostgreSQL como esperado!");
                System.out.println("Mensagem do banco: " + e.getMessage());
            }

            System.out.println("\n[LIMPEZA] Desfazendo registros temporários na ordem correta...");
            if (produtoCriado != null && produtoCriado.getIdProduto() != null) {
                produtoDAO.remover(produtoCriado.getIdProduto());
                System.out.println("Produto ID " + produtoCriado.getIdProduto() + " removido.");
            }
            if (fornecedorCriado != null && fornecedorCriado.getIdFornecedor() != null) {
                dao.remover(fornecedorCriado.getIdFornecedor());
                System.out.println("Fornecedor ID " + fornecedorCriado.getIdFornecedor() + " removido.");
            }
            System.out.println("Limpeza concluída com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
