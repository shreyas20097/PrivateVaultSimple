public class User {

    private int uid;
    private String uname;
    private String pass;

    public User(int uid, String uname, String pass) {
        this.uid = uid;
        this.uname = uname;
        this.pass = pass;
    }

    public User(String uname, String pass) {
        this(-1, uname, pass);
    }

    public int getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public String getPass() {
        return pass;
    }
}
