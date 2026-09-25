package app;

import dao.FornecedorDAO;
import modelo.Fornecedor;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

// Teste CRUD Fornecedor
public class TesteEtapa2FornecedorCRUD {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("            ETAPA 2: TESTE DO FORNECEDOR DAO (CRUD)                    ");
        System.out.println("================================================================================");

        FornecedorDAO fornecedorDAO = new FornecedorDAO();

        try {
            System.out.println("\n[1] TESTANDO INSERÇÃO (CREATE):");
            Fornecedor novo = new Fornecedor();
            novo.setNome("Distribuidora Teste1");
            novo.setTelefone("(62) 3999-4455");

            fornecedorDAO.inserir(novo);
            System.out.println("Fornecedor inserido com sucesso!");
            System.out.println("ID Gerado pelo PostgreSQL: " + novo.getIdFornecedor());
            System.out.println("Dados: " + novo);

            System.out.println("\n[2] TESTANDO BUSCA POR ID (READ):");
            Optional<Fornecedor> opt = fornecedorDAO.buscarPorId(novo.getIdFornecedor());
            if (opt.isPresent()) {
                System.out.println("Fornecedor localizado via buscarPorId(): " + opt.get());
            } else {
                System.err.println("Falha: Fornecedor não encontrado com o ID " + novo.getIdFornecedor());
            }

            System.out.println("\n[3] TESTANDO ATUALIZAÇÃO (UPDATE):");
            novo.setNome("Distr Teste2");
            novo.setTelefone("(62) 99888-7766");
            fornecedorDAO.atualizar(novo);
            System.out.println("Fornecedor atualizado com sucesso!");

            Optional<Fornecedor> atualizadoOpt = fornecedorDAO.buscarPorId(novo.getIdFornecedor());
            atualizadoOpt.ifPresent(f -> System.out.println("Estado pós-update: " + f));

            System.out.println("\n[4] TESTANDO BUSCA PARCIAL POR NOME:");
            List<Fornecedor> buscaParcial = fornecedorDAO.buscarPorNomeParcial("Teste2");
            System.out.println("Fornecedores encontrados com 'Teste': " + buscaParcial.size());
            for (Fornecedor f : buscaParcial) {
                System.out.println("-> " + f);
            }

            System.out.println("\n[5] TESTANDO LISTAGEM GERAL (listarTodos):");
            List<Fornecedor> todos = fornecedorDAO.listarTodos();
            System.out.println("✔ Total de fornecedores cadastrados: " + todos.size());
            for (Fornecedor f : todos) {
                System.out
                        .println("  -> [ID " + f.getIdFornecedor() + "] " + f.getNome() + " | Tel: " + f.getTelefone());
            }

            System.out.println("\n[6] TESTANDO REMOÇÃO (DELETE):");
            fornecedorDAO.remover(novo.getIdFornecedor());
            System.out.println("Fornecedor ID " + novo.getIdFornecedor() + " removido com sucesso!");

            Optional<Fornecedor> excluidoOpt = fornecedorDAO.buscarPorId(novo.getIdFornecedor());
            if (excluidoOpt.isEmpty()) {
                System.out.println("Comprovado: Registro não existe mais no banco de dados.");
            }

            System.out.println("\n>>> ETAPA 2 CONCLUÍDA! <<<");

        } catch (SQLException e) {
            System.err.println("Erro ao executar operações da Etapa 2: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
