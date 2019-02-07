package test.money;

import test.money.service.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBHelper {
    public static final String TEST_JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    public static final String TEST_JDBC_DRIVER = "org.h2.Driver";
    private static final DataSource TEST_DATASOURCE = DataSourceFactory.create(TEST_JDBC_DRIVER, TEST_JDBC_URL);

    private DBHelper() {
    }

    public static void cleanDB() throws SQLException {
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement()){
            statement.execute("DROP ALL OBJECTS");
        }
    }

    public static DataSource getDataSource(){
        return TEST_DATASOURCE;
    }

    public static Connection getConnection() throws SQLException {
        return TEST_DATASOURCE.getConnection();
    }

    public static void reInitDB() throws SQLException{
        cleanDB();
        Application.initDB(getDataSource());
    }
}
