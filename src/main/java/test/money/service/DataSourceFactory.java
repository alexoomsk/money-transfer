package test.money.service;

import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;

public class DataSourceFactory {

  public static final String H2_JDBC_DRIVER = "org.h2.Driver";

  public static DataSource create(String jdbcDriver, String jdbcUrl) {
    switch (jdbcDriver) {
      case H2_JDBC_DRIVER:
        return createH2DataSource(jdbcUrl);
      default:
        throw new IllegalArgumentException(jdbcDriver + "is not supported");
    }
  }

  private static DataSource createH2DataSource(String jdbcUrl) {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setUrl(jdbcUrl);
    return dataSource;
  }
}
