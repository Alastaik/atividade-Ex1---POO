package modelo;

import java.util.Objects;

//Modelo Fornecedor

public class Fornecedor {

    private Integer idFornecedor;
    private String nome;
    private String telefone;

    public Fornecedor() {
    }

    public Fornecedor(String nome, String telefone) {
        this.nome = nome;
        this.telefone = telefone;
    }

    public Fornecedor(Integer idFornecedor, String nome, String telefone) {
        this.idFornecedor = idFornecedor;
        this.nome = nome;
        this.telefone = telefone;
    }

    public Integer getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Integer idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fornecedor that = (Fornecedor) o;
        return Objects.equals(idFornecedor, that.idFornecedor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFornecedor);
    }

    @Override
    public String toString() {
        return "Fornecedor{" +
                "idFornecedor=" + idFornecedor +
                ", nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
