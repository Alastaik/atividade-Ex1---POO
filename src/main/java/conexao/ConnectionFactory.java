package conexao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Padrão de Projeto GoF: Singleton
 * 
 * Centraliza a configuração de acesso e credenciais do banco de dados PostgreSQL.
 * Garante que exista apenas uma instância desta fábrica em toda a JVM,
 * enquanto fornece novas conexões sob demanda pelo método getConnection().
 * 
 * Disciplina: ADS1253 - Programação Orientada a Objetos com Banco de Dados
 * Professor: Welington Júlio
 */
public class ConnectionFactory {

    // 1. Constantes com parâmetros de conexão padrão
    private static final String URL = "jdbc:postgresql://localhost:5432/sistema_vendas";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "admin"; // Senha padrão do laboratório

    // Senhas alternativas para compatibilidade entre ambiente do laboratório e máquina pessoal
    private static final String[] SENHAS_FALLBACK = {"28072006", "admin", "postgres", "minha_senha"};

    // 2. Atributo estático privado que armazena a única instância da fábrica (Singleton)
    private static ConnectionFactory instancia;

    // 3. Construtor privado: impede que outras classes usem "new ConnectionFactory()"
    private ConnectionFactory() {
        // Construtor fechado para proteger a unicidade da instância
    }

    // 4. Ponto global de acesso: instanciação sob demanda (lazy initialization)
    public static ConnectionFactory getInstancia() {
        if (instancia == null) {
            instancia = new ConnectionFactory();
        }
        return instancia;
    }

    /**
     * Retorna uma nova conexão ativa com o banco PostgreSQL.
     * O padrão Singleton pertence à FÁBRICA, enquanto cada chamada
     * a este método entrega uma nova instância de Connection individualizada.
     * 
     * @return Connection ativa
     * @throws SQLException caso ocorra falha de conexão
     */
    public Connection getConnection() throws SQLException {
        // Tenta primeiro carregar credenciais customizadas de application.properties
        Properties props = carregarProperties();
        String urlConfig = props.getProperty("db.url", URL);
        String userConfig = props.getProperty("db.user", USUARIO);
        String passConfig = props.getProperty("db.password", SENHA);

        try {
            return DriverManager.getConnection(urlConfig, userConfig, passConfig);
        } catch (SQLException e1) {
            // Tentativa de fallback automático com senhas conhecidas de laboratório/máquina
            for (String senhaTentativa : SENHAS_FALLBACK) {
                if (senhaTentativa.equals(passConfig)) continue;
                try {
                    return DriverManager.getConnection(urlConfig, userConfig, senhaTentativa);
                } catch (SQLException ignored) {
                    // Continua tentando os outros fallbacks
                }
            }
            // Se nenhum fallback funcionar, lança a exceção original
            throw e1;
        }
    }

    /**
     * Carrega propriedades do arquivo application.properties do classpath se disponível.
     */
    private Properties carregarProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {
            // Em caso de falha na leitura do arquivo, utiliza os valores padrão estáticos
        }
        return props;
    }
}
