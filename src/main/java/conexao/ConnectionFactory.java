package conexao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Conexao - Singleton
public class ConnectionFactory {

    private static final String URL = "jdbc:postgresql://localhost:5432/sistema_vendas";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "admin";
    private static final String[] SENHAS_FALLBACK = {"28072006", "admin", "postgres", "minha_senha"};

    private static ConnectionFactory instancia;

    private ConnectionFactory() {
    }

    public static ConnectionFactory getInstancia() {
        if (instancia == null) {
            instancia = new ConnectionFactory();
        }
        return instancia;
    }

    public Connection getConnection() throws SQLException {
        Properties props = carregarProperties();
        String urlConfig = props.getProperty("db.url", URL);
        String userConfig = props.getProperty("db.user", USUARIO);
        String passConfig = props.getProperty("db.password", SENHA);

        try {
            return DriverManager.getConnection(urlConfig, userConfig, passConfig);
        } catch (SQLException e1) {
            for (String senhaTentativa : SENHAS_FALLBACK) {
                if (senhaTentativa.equals(passConfig)) continue;
                try {
                    return DriverManager.getConnection(urlConfig, userConfig, senhaTentativa);
                } catch (SQLException ignored) {
                }
            }
            throw e1;
        }
    }

    private Properties carregarProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {
        }
        return props;
    }
}
