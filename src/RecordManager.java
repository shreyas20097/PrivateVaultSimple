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

    public Record getRecord(int recordId) throws SQLException {

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM vault_records WHERE record_id = ?");
        ps.setInt(1, recordId);

        ResultSet rs = ps.executeQuery();

        Record record = null;
        if (rs.next()) {
            record = new Record(
                    rs.getInt("record_id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("created_at"),
                    rs.getString("hash")
            );
        }

        rs.close();
        ps.close();

        return record;
    }

    public boolean updateRecord(int recordId, String newTitle, String newContent) throws SQLException {

        if (newTitle == null || newTitle.trim().isEmpty() || newContent == null || newContent.trim().isEmpty()) {
            return false;
        }

        Record existing = getRecord(recordId);
        if (existing == null) {
            return false;
        }

        String newHash = HashUtil.generateHash(newContent.trim());

        String sql = "UPDATE vault_records SET title = ?, content = ?, hash = ? WHERE record_id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, newTitle.trim());
        ps.setString(2, newContent.trim());
        ps.setString(3, newHash);
        ps.setInt(4, recordId);
        int rows = ps.executeUpdate();
        ps.close();

        return rows > 0;
    }

    public boolean verifyRecord(Record record) {
        String current = HashUtil.generateHash(record.getContent());
        return current.equals(record.getHash());
    }
}
