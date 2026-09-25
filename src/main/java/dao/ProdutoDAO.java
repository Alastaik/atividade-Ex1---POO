package dao;

import conexao.ConnectionFactory;
import modelo.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Produto DAO - Acesso
public class ProdutoDAO {

    private Connection getConnection() throws SQLException {
        return ConnectionFactory.getInstancia().getConnection();
    }

    public void inserir(Produto produto) throws SQLException {
        String sql = "INSERT INTO produto (nome, preco, estoque, id_fornecedor) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setObject(3, produto.getEstoque());
            stmt.setObject(4, produto.getIdFornecedor());

            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setIdProduto(chaves.getInt(1));
                }
            }
        }
    }

    public void atualizar(Produto produto) throws SQLException {
        String sql = "UPDATE produto SET nome = ?, preco = ?, estoque = ?, id_fornecedor = ? WHERE id_produto = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setObject(3, produto.getEstoque());
            stmt.setObject(4, produto.getIdFornecedor());
            stmt.setInt(5, produto.getIdProduto());

            stmt.executeUpdate();
        }
    }

    public void remover(int idProduto) throws SQLException {
        String sql = "DELETE FROM produto WHERE id_produto = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduto);
            stmt.executeUpdate();
        }
    }

    public Optional<Produto> buscarPorId(int idProduto) throws SQLException {
        String sql = "SELECT p.id_produto, p.nome, p.preco, p.estoque, p.id_fornecedor, f.nome AS nome_fornecedor " +
                     "FROM produto p " +
                     "LEFT JOIN fornecedor f ON p.id_fornecedor = f.id_fornecedor " +
                     "WHERE p.id_produto = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduto);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearProduto(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Produto> listarTodos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.id_produto, p.nome, p.preco, p.estoque, p.id_fornecedor, f.nome AS nome_fornecedor " +
                     "FROM produto p " +
                     "LEFT JOIN fornecedor f ON p.id_fornecedor = f.id_fornecedor " +
                     "ORDER BY p.id_produto ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(mapearProduto(rs));
            }
        }
        return produtos;
    }

    public List<Produto> buscarPorNomeParcial(String trecho) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.id_produto, p.nome, p.preco, p.estoque, p.id_fornecedor, f.nome AS nome_fornecedor " +
                     "FROM produto p " +
                     "LEFT JOIN fornecedor f ON p.id_fornecedor = f.id_fornecedor " +
                     "WHERE p.nome ILIKE ? ORDER BY p.id_produto ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + trecho + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapearProduto(rs));
                }
            }
        }
        return produtos;
    }

    public List<Produto> listarProdutosPorFornecedor(int idFornecedor) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.id_produto, p.nome, p.preco, p.estoque, p.id_fornecedor, f.nome AS nome_fornecedor " +
                     "FROM produto p " +
                     "INNER JOIN fornecedor f ON p.id_fornecedor = f.id_fornecedor " +
                     "WHERE f.id_fornecedor = ? " +
                     "ORDER BY p.id_produto ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFornecedor);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapearProduto(rs));
                }
            }
        }
        return produtos;
    }

    private Produto mapearProduto(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setIdProduto(rs.getInt("id_produto"));
        produto.setNome(rs.getString("nome"));
        produto.setPreco(rs.getDouble("preco"));
        produto.setEstoque(rs.getObject("estoque", Integer.class));
        produto.setIdFornecedor(rs.getObject("id_fornecedor", Integer.class));

        try {
            produto.setNomeFornecedor(rs.getString("nome_fornecedor"));
        } catch (SQLException ignored) {
        }

        return produto;
    }
}
