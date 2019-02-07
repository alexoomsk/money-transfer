package test.money;

import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.*;
import spark.Spark;

import java.io.IOException;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

@DisplayName("Rest test")
public class ApplicationTest {
    private static CloseableHttpClient httpclient = HttpClients.createMinimal();

    @BeforeAll
    static void startServerAndClient() throws SQLException {
        new Application(0, "/api", DBHelper.TEST_JDBC_DRIVER, DBHelper.TEST_JDBC_URL).start();
        Spark.awaitInitialization();
    }

    @AfterAll
    static void killServerAndCloseClient() throws IOException {
        Spark.stop();
        httpclient.close();
    }

    @BeforeEach
    void reInitDB() throws SQLException {
        DBHelper.reInitDB();
    }

    @Test
    @DisplayName("Response validation for invalid request")
    void testInvalidRequest1() throws IOException {
        String response = doTransferRequest("{\"a\": \"b\"}");
        assertThat(response, is("{\"status\":\"ERROR\",\"errorMessage\":\"Input data has invalid format: \\\"fromAccountNumber\\\" is required; \\\"toAccountNumber\\\" is required; \\\"amount\\\" is required; \"}"));
    }

    @Test
    @DisplayName("Response validation for negative amount")
    void testInvalidRequest2() throws IOException {
        String response = doTransferRequest(requestJson(1, 2, -0.01));
        assertThat(response, is("{\"status\":\"ERROR\",\"errorMessage\":\"Input data has invalid format: \\\"amount\\\" should be positive\"}"));
    }

    @Test
    @DisplayName("Response validation for valid request")
    void testResponse() throws IOException {
        String response = doTransferRequest(requestJson(1L, 2L, 34.561));
        assertThat(response, is("{\"status\":\"DONE\"}"));
    }

    @Test
    @DisplayName("Invalid account")
    void testInvalidAccount() throws IOException {
        String response = doTransferRequest(requestJson(7L, 2L, 101));
        assertThat(response, is("{\"status\":\"ERROR\",\"errorMessage\":\"Account 7 does not exist\"}"));
    }

    @Test
    @DisplayName("Not enough money")
    void testTransferLotsOfMoney() throws IOException {
        String response = doTransferRequest(requestJson(1L, 2L, 101));
        assertThat(response, is("{\"status\":\"ERROR\",\"errorMessage\":\"Source account has not enough money\"}"));
        assertThat(doAccountRequest("1"), is("{\"status\":\"DONE\",\"result\":{\"number\":1,\"balance\":100.0000}}"));
    }

    @Test
    @DisplayName("Calculation validation")
    void testCalculation() throws IOException {
        doTransferRequest(requestJson(1L, 2L, 34.56341));
        doTransferRequest(requestJson(3L, 1L, 50.73126));
        doTransferRequest(requestJson(2L, 3L, 130));
        doTransferRequest(requestJson(1L, 2L, 68));
        assertThat(doAccountRequest("1"), is("{\"status\":\"DONE\",\"result\":{\"number\":1,\"balance\":48.1679}}"));
        assertThat(doAccountRequest("2"), is("{\"status\":\"DONE\",\"result\":{\"number\":2,\"balance\":172.5634}}"));
        assertThat(doAccountRequest("3"), is("{\"status\":\"DONE\",\"result\":{\"number\":3,\"balance\":379.2687}}"));
   }


    private String doTransferRequest(String requestBody) throws IOException {
        return httpclient.execute(RequestBuilder
                        .post(baseURL() + "/api/transfer")
                .setEntity(new StringEntity(requestBody))
                        .build(),
                new BasicResponseHandler()
        );
    }

    private String doAccountRequest(String accountNumber) throws IOException {
        return httpclient.execute(RequestBuilder
                .get(baseURL() + "/api/account/" + accountNumber)
                .build(),
                new BasicResponseHandler()
        );
    }

    private String baseURL() {
        return "http://localhost:" + Spark.port();
    }

    private static String requestJson(long fromAccount, long toAccount, double amount) {
        return "{\"fromAccountNumber\":" + fromAccount + ",\"toAccountNumber\":" + toAccount + ", \"amount\": " + amount + "}";
    }
}
