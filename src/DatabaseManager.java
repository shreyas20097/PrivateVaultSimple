import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:vault_simple.db";

    private Connection connection;

    public void connect() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL);
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTables() throws SQLException {

        Statement stmt = connection.createStatement();

        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT UNIQUE," +
                        "password TEXT)"
        );

        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS vault_records (" +
                        "record_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT," +
                        "content TEXT," +
                        "created_at TEXT," +
                        "hash TEXT)"
        );

        stmt.close();

        seedDefaultUser();
    }

    private void seedDefaultUser() throws SQLException {

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        rs.next();
        int count = rs.getInt(1);
        rs.close();

        if (count == 0) {
            stmt.executeUpdate("INSERT INTO users (username, password) VALUES ('admin', 'admin123')");
            System.out.println("Default login created -> username: admin, password: admin123");
        }

        stmt.close();
    }
}
