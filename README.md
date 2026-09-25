# Atividade 1: POO com Banco de Dados (PostgreSQL + JDBC + Maven)

**Disciplina:** ADS1253 - Programação Orientada a Objetos com Banco de Dados  
**Curso:** Análise e Desenvolvimento de Sistemas (ADS) - PUC Goiás  
**Docente:** Prof. Welington Júlio  
**Aluno:** Yuri Silva  
**Tecnologias:** Java 21 (JDK 21), Maven 3.9+, PostgreSQL 18 (JDBC Driver 42.7.3), Padrão GoF Singleton.

---

## 📌 Visão Geral do Cenário e Domínio do Desafio

No sistema de vendas desenvolvido nos encontros anteriores, o sistema gerenciava **clientes**, **produtos**, **pedidos** e **itens de pedido**, mas ainda não rastreava a origem de cada produto no estoque.

Nesta atividade, expandimos o domínio para incluir a entidade **Fornecedor**, estabelecendo um relacionamento **1:N** entre `fornecedor` e `produto` (um fornecedor pode fornecer múltiplos produtos; cada produto é fornecido por um fornecedor).

Toda a infraestrutura de conexão segue rigorosamente o padrão de projeto **Singleton** com a classe utilitária `ConnectionFactory`, garantindo uma única instância da fábrica com instanciação preguiçosa (*lazy initialization*), construtor fechado e entrega de novas conexões sob demanda via `try-with-resources`.

---

## 🏗️ Estrutura do Projeto (Modelo IntelliJ IDEA + Maven)

```text
atividade-ex1-poo/
├── pom.xml                               # Configurações do Maven, dependências (PostgreSQL) e plugins
├── README.md                             # Documentação técnica e guia de execução
├── .gitignore                            # Arquivos e diretórios ignorados pelo Git
├── sql/                                  # Scripts SQL para execução manual ou via psql
│   ├── 01_etapa1_ddl.sql                 # DDL: criação da tabela fornecedor e FK em produto (RESTRICT)
│   └── 02_dados_exemplo.sql              # DML: sementes e dados de demonstração
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── conexao/
    │   │   │   └── ConnectionFactory.java # Singleton GoF: fábrica centralizada de conexões JDBC
    │   │   ├── modelo/
    │   │   │   ├── Cliente.java          # Entidade Cliente
    │   │   │   ├── Fornecedor.java       # Entidade Fornecedor (id, nome, telefone)
    │   │   │   └── Produto.java          # Entidade Produto (id, nome, preco, estoque, idFornecedor)
    │   │   ├── dao/
    │   │   │   ├── Cliente.DAO.java      # DAO de Cliente
    │   │   │   ├── FornecedorDAO.java    # DAO completo da Entidade Fornecedor (CRUD com PreparedStatement)
    │   │   │   └── ProdutoDAO.java       # DAO de Produto com método de INNER JOIN (Etapa 3)
    │   │   └── app/
    │   │       ├── Main.java             # Menu interativo principal da aplicação
    │   │       ├── TesteEtapasCompleto.java # Orquestrador integrado (executa Fases 0 a 4)
    │   │       ├── TesteEtapa2FornecedorCRUD.java # Teste isolado do CRUD de Fornecedor
    │   │       ├── TesteEtapa3Join.java           # Teste isolado do INNER JOIN parametrizado
    │   │       └── TesteEtapa4IntegridadeReferencial.java # Teste da FK e captura do erro do PostgreSQL
    │   └── resources/
    │       ├── application.properties    # Parâmetros de conexão (URL, usuário, senhas)
    │       └── sql/
    │           └── 01_etapa1_ddl.sql     # Script DDL embutido nos recursos
    └── test/
        └── java/
```

---

## 📋 Resumo das Etapas Desenvolvidas

### 🔹 ETAPA 1: Modelagem e DDL (0,4 pt)

1. **Criação da tabela `fornecedor`**:
   - `id_fornecedor` (`SERIAL`, `PRIMARY KEY`);
   - `nome` (`VARCHAR(100)`, `NOT NULL`);
   - `telefone` (`VARCHAR(20)`, opcional/`NULL`).

2. **Adição da coluna `id_fornecedor` na tabela `produto`**:
   - Utilizado `ALTER TABLE produto ADD COLUMN id_fornecedor INTEGER;`
   - Adicionada constraint de chave estrangeira com regra de exclusão restrita:
     ```sql
     ALTER TABLE produto
         ADD CONSTRAINT fk_produto_fornecedor
         FOREIGN KEY (id_fornecedor)
         REFERENCES fornecedor(id_fornecedor)
         ON DELETE RESTRICT;
     ```

3. **Justificativa da escolha do `ON DELETE RESTRICT` (vs. `ON DELETE CASCADE`)**:
   > A opção **RESTRICT** é indispensável para preservar a integridade dos dados cadastrais e financeiros do negócio. Se fosse utilizado `ON DELETE CASCADE`, a exclusão involuntária ou acidental de um fornecedor removeria de forma silenciosa e irreversível todos os produtos associados a ele no catálogo. Isso provocaria quebra de histórico de estoque, inconsistência relacional com a tabela de itens de pedido (`pedido_item`) e prejuízo operacional. Com `RESTRICT`, o PostgreSQL recusa a exclusão enquanto houver produtos dependentes, obrigando a equipe/sistema a reatribuir ou gerenciar os produtos antes de remover o fornecedor.

---

### 🔹 ETAPA 2: FornecedorDAO Completo (1,0 pt)

Implementação da classe [`FornecedorDAO`](src/main/java/dao/FornecedorDAO.java) com as seguintes rotinas:
* `inserir(Fornecedor fornecedor)`: Executa `INSERT` via `PreparedStatement` configurado com `Statement.RETURN_GENERATED_KEYS`, recuperando a chave primária autogerada e atribuindo-a ao objeto com `fornecedor.setIdFornecedor(...)`.
* `buscarPorId(int id)`: Executa `SELECT` filtrado por ID e retorna `Optional<Fornecedor>`.
* `listarTodos()`: Retorna `List<Fornecedor>` ordenada pelo identificador.
* `buscarPorNomeParcial(String trecho)`: Consulta segura com operador `ILIKE ?` parametrizado.
* `atualizar(Fornecedor fornecedor)`: Atualiza `nome` e `telefone` via `PreparedStatement`.
* `remover(int id)`: Remove o registro via `PreparedStatement`.
* `mapearFornecedor(ResultSet rs)`: Método privado auxiliar que centraliza e padroniza o mapeamento relacional-objeto, eliminando código duplicado.

*Regra de Ouro mantida:* Zero concatenação de strings em comandos SQL; todos os parâmetros utilizam `?` e `PreparedStatement`.

---

### 🔹 ETAPA 3: Consulta Parametrizada com INNER JOIN (0,3 pt)

Implementado no [`ProdutoDAO`](src/main/java/dao/ProdutoDAO.java) o método:
```java
public List<Produto> listarProdutosPorFornecedor(int idFornecedor) throws SQLException
```
* **SQL utilizado:**
  ```sql
  SELECT p.id_produto, p.nome, p.preco, p.estoque, p.id_fornecedor, f.nome AS nome_fornecedor
  FROM produto p
  INNER JOIN fornecedor f ON p.id_fornecedor = f.id_fornecedor
  WHERE f.id_fornecedor = ?
  ORDER BY p.id_produto ASC
  ```
* **Importância:** Esse método combina dados de tabelas relacionadas de forma eficiente e segura, sustentando telas de relatórios, listagens operacionais e painéis de compras sem risco de *SQL Injection*.

---

### 🔹 ETAPA 4: Teste de Integridade Referencial (0,3 pt)

Implementado na classe [`TesteEtapa4IntegridadeReferencial`](src/main/java/app/TesteEtapa4IntegridadeReferencial.java):
1. Cadastra um fornecedor via `FornecedorDAO`;
2. Cadastra um produto vinculado a esse fornecedor via `ProdutoDAO`;
3. Tenta remover o fornecedor diretamente chamando `fornecedorDAO.remover(idFornecedor)`;
4. Captura a exceção `SQLException` real disparada pelo PostgreSQL:
   - **SQLState:** `23001` / `23503` (`foreign_key_violation`);
   - **Mensagem real do PostgreSQL:** `ERRO: update or delete on table "fornecedor" violates RESTRICT setting of foreign key constraint "fk_produto_fornecedor" on table "produto"`;
5. **Documentação da Recusa:** O banco bloqueou a operação porque a restrição `ON DELETE RESTRICT` exige que nenhum produto aponte para o fornecedor no momento de sua exclusão;
6. Limpeza controlada realizada na ordem correta: remoção do produto filho em primeiro lugar, seguida pela remoção do fornecedor pai.

---

## ⚙️ Como Executar a Aplicação

### Pré-requisitos
* Java JDK 21+ instalado e configurado na variável de ambiente `JAVA_HOME`.
* Apache Maven 3.9+ instalado.
* PostgreSQL ativo com o banco de dados `sistema_vendas` criado.

### Execução via Terminal / Maven

1. **Compilar o projeto:**
   ```bash
   mvn clean compile
   ```

2. **Executar a Bateria Completa de Testes Integrados (Fase 0 a Etapa 4):**
   ```bash
   mvn exec:java
   ```

3. **Executar Testes Isolados de Cada Etapa:**
   * Etapa 2 (CRUD Fornecedor):
     ```bash
     mvn exec:java -Dexec.mainClass="app.TesteEtapa2FornecedorCRUD"
     ```
   * Etapa 3 (INNER JOIN Produto x Fornecedor):
     ```bash
     mvn exec:java -Dexec.mainClass="app.TesteEtapa3Join"
     ```
   * Etapa 4 (Integridade Referencial e Erro de FK):
     ```bash
     mvn exec:java -Dexec.mainClass="app.TesteEtapa4IntegridadeReferencial"
     ```
   * Menu Interativo:
     ```bash
     mvn exec:java -Dexec.mainClass="app.Main"
     ```

---

## 🔒 Padrão Singleton na Conexão (`ConnectionFactory`)

```java
public class ConnectionFactory {
    private static ConnectionFactory instancia;

    // Construtor privado: impede new ConnectionFactory()
    private ConnectionFactory() {}

    // Ponto de acesso global com lazy initialization
    public static ConnectionFactory getInstancia() {
        if (instancia == null) {
            instancia = new ConnectionFactory();
        }
        return instancia;
    }

    // Retorna uma nova conexão ativa por chamada
    public Connection getConnection() throws SQLException {
        // Gerenciamento com fallback inteligente e leitura de application.properties
        return DriverManager.getConnection(url, user, password);
    }
}
```

> **Atenção conceitual:** A *Fábrica* é única na JVM (Singleton). As *Conexões* entregues por `getConnection()` são instâncias distintas e individuais, sendo gerenciadas de forma segura através de blocos `try-with-resources`.
