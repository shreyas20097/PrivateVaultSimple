import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RecordManager {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Connection connection;

    public RecordManager(DatabaseManager db) {
        this.connection = db.getConnection();
    }

    public Record addRecord(String title, String content) throws SQLException {

        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            return null;
        }

        String now = LocalDateTime.now().format(DATE_FORMAT);
        String hash = HashUtil.generateHash(content.trim());

        String sql = "INSERT INTO vault_records (title, content, created_at, hash) VALUES (?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, title.trim());
        ps.setString(2, content.trim());
        ps.setString(3, now);
        ps.setString(4, hash);
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        int id = -1;
        if (keys.next()) {
            id = keys.getInt(1);
        }
        keys.close();
        ps.close();

        return new Record(id, title.trim(), content.trim(), now, hash);
    }

    public ArrayList<Record> getAllRecords() throws SQLException {

        ArrayList<Record> records = new ArrayList<>();

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM vault_records ORDER BY record_id");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            records.add(new Record(
                    rs.getInt("record_id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("created_at"),
                    rs.getString("hash")
            ));
        }

        rs.close();
        ps.close();

        return records;
    }

    public boolean verifyRecord(Record record) {
        String current = HashUtil.generateHash(record.getContent());
        return current.equals(record.getHash());
    }
}
