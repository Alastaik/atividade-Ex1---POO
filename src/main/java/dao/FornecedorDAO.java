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

// Fornecedor DAO - Acesso
public class FornecedorDAO {

    private Connection getConnection() throws SQLException {
        return ConnectionFactory.getInstancia().getConnection();
    }

    private Fornecedor mapearFornecedor(ResultSet rs) throws SQLException {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setIdFornecedor(rs.getInt("id_fornecedor"));
        fornecedor.setNome(rs.getString("nome"));
        fornecedor.setTelefone(rs.getString("telefone"));
        return fornecedor;
    }

    public void inserir(Fornecedor fornecedor) throws SQLException {
        String sql = "INSERT INTO fornecedor (nome, telefone) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fornecedor.getNome());
            stmt.setString(2, fornecedor.getTelefone());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    fornecedor.setIdFornecedor(chaves.getInt(1));
                }
            }
        }
    }

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

    public List<Fornecedor> buscarPorNomeParcial(String trecho) throws SQLException {
        List<Fornecedor> fornecedores = new ArrayList<>();
        String sql = "SELECT id_fornecedor, nome, telefone FROM fornecedor WHERE nome ILIKE ? ORDER BY id_fornecedor ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + trecho + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fornecedores.add(mapearFornecedor(rs));
                }
            }
        }
        return fornecedores;
    }

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

    public void remover(int idFornecedor) throws SQLException {
        String sql = "DELETE FROM fornecedor WHERE id_fornecedor = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFornecedor);
            stmt.executeUpdate();
        }
    }

    public List<String> listarProdutosPorFornecedor(int idFornecedor) throws SQLException {
        List<String> listaFormatada = new ArrayList<>();
        String sql = "SELECT p.id_produto, p.nome AS nome_produto, p.preco, p.estoque, f.nome AS nome_fornecedor " +
                     "FROM produto p " +
                     "INNER JOIN fornecedor f ON p.id_fornecedor = f.id_fornecedor " +
                     "WHERE f.id_fornecedor = ? " +
                     "ORDER BY p.id_produto ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFornecedor);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String item = String.format("Produto [ID: %d, Nome: %s, Preço: R$ %.2f, Estoque: %d] - Fornecedor: %s",
                            rs.getInt("id_produto"),
                            rs.getString("nome_produto"),
                            rs.getDouble("preco"),
                            rs.getInt("estoque"),
                            rs.getString("nome_fornecedor"));
                    listaFormatada.add(item);
                }
            }
        }
        return listaFormatada;
    }
}
