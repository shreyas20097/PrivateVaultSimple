import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    private Connection connection;

    public UserManager(DatabaseManager db) {
        this.connection = db.getConnection();
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
