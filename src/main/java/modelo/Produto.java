package modelo;

import java.util.Objects;

// Modelo Produto
public class Produto {

    private Integer idProduto;
    private String nome;
    private Double preco;
    private Integer estoque;
    private Integer idFornecedor;
    private String nomeFornecedor;

    public Produto() {
    }

    public Produto(String nome, Double preco, Integer estoque, Integer idFornecedor) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
        this.idFornecedor = idFornecedor;
    }

    public Produto(Integer idProduto, String nome, Double preco, Integer estoque, Integer idFornecedor) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
        this.idFornecedor = idFornecedor;
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Integer idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public Integer getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Integer idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(idProduto, produto.idProduto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduto);
    }

    @Override
    public String toString() {
        return "Produto{" +
                "idProduto=" + idProduto +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", estoque=" + estoque +
                ", idFornecedor=" + idFornecedor +
                (nomeFornecedor != null ? ", fornecedor='" + nomeFornecedor + '\'' : "") +
                '}';
    }
}
