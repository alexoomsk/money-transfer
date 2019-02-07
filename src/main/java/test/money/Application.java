package test.money;


import test.money.service.AccountDaoImpl;
import test.money.service.AccountServiceImpl;
import test.money.service.DataSourceFactory;

import java.io.*;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String DEFAULT_JDBC_URL = "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1";
    private static final String DEFAULT_BASE_PATH = "/api";
    private static final int DEFAULT_PORT = 4567;

    private final DataSource dataSource;
    private final String basePath;
    private final int port;

    public Application(int port, String basePath, String jdbcDriver, String jdbcUrl) {
        this.port = port;
        this.basePath = basePath;
        this.dataSource = DataSourceFactory.create(jdbcDriver, jdbcUrl);
    }


    public static void main(String[] params) {
        try {
            int port = Optional.of(params).filter(args -> args.length > 0).map(args -> args[0]).map(Integer::valueOf).orElse(DEFAULT_PORT);
            new Application(port, DEFAULT_BASE_PATH, DataSourceFactory.H2_JDBC_DRIVER, DEFAULT_JDBC_URL).start();
        } catch (SQLException e) {
            log.error("Failed to init database", e);
        }
    }

    public void start() throws SQLException {
        initDB(dataSource);
        new ServerStarter(port, basePath, new AccountServiceImpl(new AccountDaoImpl(dataSource))).start();
    }

    static void initDB(DataSource dataSource) throws SQLException{
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(readFileFromResources("dbSchema.sql"));
            statement.execute(readFileFromResources("demoData.sql"));
        }
    }

    private static String readFileFromResources(String fileName){
        StringBuilder sb = new StringBuilder();
        new BufferedReader(new InputStreamReader(Application.class.getClassLoader()
                .getResourceAsStream(fileName))).lines().forEachOrdered((s) -> sb.append(s).append(" "));
        return sb.toString();
    }
}
