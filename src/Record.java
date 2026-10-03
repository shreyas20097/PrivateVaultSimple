public class Record {

    private int recordId;
    private String title;
    private String content;
    private String createdAt;
    private String hash;

    public Record(int recordId, String title, String content, String createdAt, String hash) {
        this.recordId = recordId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.hash = hash;
    }

    public Record(String title, String content) {
        this(-1, title, content, null, null);
    }

    public int getRecordId() {
        return recordId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getHash() {
        return hash;
    }
}
