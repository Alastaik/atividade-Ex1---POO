package dao;

import conexao.ConnectionFactory;
import modelo.Fornecedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Camada de Acesso a Dados (Data Access Object): FornecedorDAO
 * 
 * Implementa o CRUD completo para a entidade Fornecedor utilizando JDBC.
 * Todas as operações utilizam PreparedStatement parametrizado, garantindo segurança contra SQL Injection.
 * As conexões são obtidas centralizadamente a partir da fábrica Singleton ConnectionFactory.
 * 
 * Disciplina: ADS1253 - POO com Banco de Dados
 * Professor: Welington Júlio
 */
public class FornecedorDAO {

    /**
     * Obtém uma nova conexão isolada a partir da fábrica centralizada Singleton.
     */
    private Connection getConnection() throws SQLException {
        return ConnectionFactory.getInstancia().getConnection();
    }

    /**
     * ETAPA 2.1 - Inserir Fornecedor
     * Recupera o id_fornecedor gerado automaticamente pelo PostgreSQL (SERIAL),
     * atribuindo o valor de volta ao objeto fornecedor recebido.
     * 
     * @param fornecedor Objeto contendo os dados a serem gravados
     * @throws SQLException Caso ocorra erro de execução no banco
     */
    public void inserir(Fornecedor fornecedor) throws SQLException {
        String sql = "INSERT INTO fornecedor (nome, telefone) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fornecedor.getNome());
            stmt.setString(2, fornecedor.getTelefone());
            stmt.executeUpdate();

            // Recupera a chave primária autogerada
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    fornecedor.setIdFornecedor(chaves.getInt(1));
                }
            }
        }
    }

    /**
     * ETAPA 2.2 - Buscar Fornecedor por ID
     * 
     * @param idFornecedor Identificador único do fornecedor
     * @return Optional contendo o Fornecedor se encontrado, ou Optional.empty()
     * @throws SQLException Caso ocorra erro de execução
     */
    public Optional<Fornecedor> buscarPorId(int idFornecedor) throws SQLException {
        String sql = "SELECT id_fornecedor, nome, telefone FROM fornecedor WHERE id_fornecedor = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFornecedor);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFornecedor(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * ETAPA 2.2 - Listar Todos os Fornecedores
     * 
     * @return Lista com todos os fornecedores cadastrados
     * @throws SQLException Caso ocorra erro de execução
     */
    public List<Fornecedor> listarTodos() throws SQLException {
        List<Fornecedor> fornecedores = new ArrayList<>();
        String sql = "SELECT id_fornecedor, nome, telefone FROM fornecedor ORDER BY id_fornecedor ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fornecedores.add(mapearFornecedor(rs));
            }
        }
        return fornecedores;
    }

    /**
     * Busca parcial por nome utilizando LIKE parametrizado (seguro contra SQL Injection).
     * 
     * @param trecho Trecho do nome a ser pesquisado
     * @return Lista de fornecedores correspondentes
     * @throws SQLException Caso ocorra erro de execução
     */
    public List<Fornecedor> buscarPorNomeParcial(String trecho) throws SQLException {
        List<Fornecedor> fornecedores = new ArrayList<>();
        String sql = "SELECT id_fornecedor, nome, telefone FROM fornecedor WHERE nome ILIKE ? ORDER BY id_fornecedor ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // O curinga '%' é inserido no parâmetro, preservando a query imutável
            stmt.setString(1, "%" + trecho + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fornecedores.add(mapearFornecedor(rs));
                }
            }
        }
        return fornecedores;
    }

    /**
     * ETAPA 2.3 - Atualizar Fornecedor
     * Atualiza os dados de um fornecedor já existente no banco.
     * 
     * @param fornecedor Fornecedor com os dados modificados e id_fornecedor válido
     * @throws SQLException Caso ocorra erro de execução
     */
    public void atualizar(Fornecedor fornecedor) throws SQLException {
        String sql = "UPDATE fornecedor SET nome = ?, telefone = ? WHERE id_fornecedor = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fornecedor.getNome());
            stmt.setString(2, fornecedor.getTelefone());
            stmt.setInt(3, fornecedor.getIdFornecedor());

            stmt.executeUpdate();
        }
    }

    /**
     * ETAPA 2.3 - Remover Fornecedor
     * Exclui o fornecedor correspondente ao ID informado.
     * Nota: Caso haja produtos vinculados e a FK possua ON DELETE RESTRICT,
     * este método lançará SQLException informando violação de chave estrangeira.
     * 
     * @param idFornecedor Identificador do fornecedor a remover
     * @throws SQLException Caso ocorra erro ou violação de integridade referencial
     */
    public void remover(int idFornecedor) throws SQLException {
        String sql = "DELETE FROM fornecedor WHERE id_fornecedor = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFornecedor);
            stmt.executeUpdate();
        }
    }

    /**
     * ETAPA 2.2 - Método Privado de Mapeamento Centralizado
     * Converte o registro posicionado no ResultSet em uma instância de Fornecedor.
     * Evita duplicação de código de leitura entre buscarPorId, listarTodos e busca parcial.
     * 
     * @param rs ResultSet posicionado no registro atual
     * @return Objeto Fornecedor populado
     * @throws SQLException Em caso de erro na leitura das colunas
     */
    private Fornecedor mapearFornecedor(ResultSet rs) throws SQLException {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setIdFornecedor(rs.getInt("id_fornecedor"));
        fornecedor.setNome(rs.getString("nome"));
        fornecedor.setTelefone(rs.getString("telefone"));
        return fornecedor;
    }
}
