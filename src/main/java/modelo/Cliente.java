package modelo;

import java.util.Objects;

//Modelo Cliente

public class Cliente {

    private Integer idCliente;
    private String nome;
    private String email;
    private Integer pontosFidelidade;

    public Cliente() {
    }

    public Cliente(String nome, String email, Integer pontosFidelidade) {
        this.nome = nome;
        this.email = email;
        this.pontosFidelidade = pontosFidelidade;
    }

    public Cliente(Integer idCliente, String nome, String email, Integer pontosFidelidade) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.email = email;
        this.pontosFidelidade = pontosFidelidade;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPontosFidelidade() {
        return pontosFidelidade;
    }

    public void setPontosFidelidade(Integer pontosFidelidade) {
        this.pontosFidelidade = pontosFidelidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(idCliente, cliente.idCliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCliente);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", pontosFidelidade=" + pontosFidelidade +
                '}';
    }
}
