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

    public Record addRecord(int uid, String title, String content) throws SQLException {

        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            return null;
        }

        String now = LocalDateTime.now().format(DATE_FORMAT);
        String hash = HashUtil.generateHash(content.trim());

        String sql = "INSERT INTO vault_records (user_id, title, content, created_at, hash) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, uid);
        ps.setString(2, title.trim());
        ps.setString(3, content.trim());
        ps.setString(4, now);
        ps.setString(5, hash);
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        int id = -1;
        if (keys.next()) {
            id = keys.getInt(1);
        }
        keys.close();
        ps.close();

        return new Record(uid, id, title.trim(), content.trim(), now, hash);
    }

    public ArrayList<Record> getAllRecords(int uid) throws SQLException {

        ArrayList<Record> records = new ArrayList<>();

        PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM vault_records WHERE user_id = ? ORDER BY record_id");
        ps.setInt(1, uid);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            records.add(extractRecord(rs));
        }

        rs.close();
        ps.close();

        return records;
    }

    public Record getRecord(int uid, int recordId) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM vault_records WHERE record_id = ? AND user_id = ?");
        ps.setInt(1, recordId);
        ps.setInt(2, uid);

        ResultSet rs = ps.executeQuery();

        Record record = null;
        if (rs.next()) {
            record = extractRecord(rs);
        }

        rs.close();
        ps.close();

        return record;
    }

    public boolean updateRecord(int uid, int recordId, String newTitle, String newContent) throws SQLException {

        if (newTitle == null || newTitle.trim().isEmpty() || newContent == null || newContent.trim().isEmpty()) {
            return false;
        }

        Record existing = getRecord(uid, recordId);
        if (existing == null) {
            return false;
        }

        String newHash = HashUtil.generateHash(newContent.trim());

        String sql = "UPDATE vault_records SET title = ?, content = ?, hash = ? WHERE record_id = ? AND user_id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, newTitle.trim());
        ps.setString(2, newContent.trim());
        ps.setString(3, newHash);
        ps.setInt(4, recordId);
        ps.setInt(5, uid);
        int rows = ps.executeUpdate();
        ps.close();

        return rows > 0;
    }

    public boolean verifyRecord(Record record) {
        String current = HashUtil.generateHash(record.getContent());
        return current.equals(record.getHash());
    }

    private Record extractRecord(ResultSet rs) throws SQLException {
        return new Record(
                rs.getInt("user_id"),
                rs.getInt("record_id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("created_at"),
                rs.getString("hash")
        );
    }
}