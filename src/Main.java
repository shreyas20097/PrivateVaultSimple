public class Main {

    public static void main(String[] args) {

        try {
            DatabaseManager db = new DatabaseManager();
            db.connect();
            db.createTables();

            UserManager userManager = new UserManager(db);
            RecordManager recordManager = new RecordManager(db);

            javax.swing.SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame(userManager, recordManager);
                login.setVisible(true);
            });

        } catch (Exception e) {
            System.out.println("Startup error: " + e.getMessage());
        }
    }
}
