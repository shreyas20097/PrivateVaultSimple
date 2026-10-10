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
                        "user_id INTEGER," +
                        "title TEXT," +
                        "content TEXT," +
                        "created_at TEXT," +
                        "hash TEXT)"
        );

        stmt.close();

        migrateSchema();
        seedDefaultUser();
    }

    private void migrateSchema() throws SQLException {

        Statement stmt = connection.createStatement();

        ResultSet rs = stmt.executeQuery("PRAGMA table_info(vault_records)");

        boolean hasUserId = false;
        while (rs.next()) {
            if ("user_id".equals(rs.getString("name"))) {
                hasUserId = true;
            }
        }
        rs.close();

        if (!hasUserId) {
            stmt.executeUpdate("ALTER TABLE vault_records ADD COLUMN user_id INTEGER");
            stmt.executeUpdate(
                    "UPDATE vault_records SET user_id = " +
                            "(SELECT MIN(user_id) FROM users) WHERE user_id IS NULL"
            );
            System.out.println("Database upgraded: added user_id column to vault_records.");
        }

        stmt.close();
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
