import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserManager {

    private Connection connection;

    public UserManager(DatabaseManager db) {
        this.connection = db.getConnection();
    }

    public boolean userExists(String username) throws SQLException {

        PreparedStatement ps = connection.prepareStatement("SELECT user_id FROM users WHERE username = ?");
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();

        rs.close();
        ps.close();

        return exists;
    }

    public boolean register(String username, String password) throws SQLException {

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        if (userExists(username.trim())) {
            return false;
        }

        PreparedStatement ps = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
        ps.setString(1, username.trim());
        ps.setString(2, password);
        ps.executeUpdate();
        ps.close();

        removeFallbackAdmin();

        return true;
    }

    private void removeFallbackAdmin() throws SQLException {

        Statement stmt = connection.createStatement();
        stmt.executeUpdate(
                "DELETE FROM users WHERE username = 'admin' AND password = 'admin123' " +
                        "AND (SELECT COUNT(*) FROM users) > 1"
        );
        stmt.close();
    }

    public User login(String username, String password) throws SQLException {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        User user = null;
        if (rs.next()) {
            user = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"));
        }

        rs.close();
        ps.close();

        return user;
    }
}
