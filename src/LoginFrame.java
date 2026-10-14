import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    private UserManager userManager;
    private RecordManager recordManager;

    public LoginFrame(UserManager userManager, RecordManager recordManager) {

        this.userManager = userManager;
        this.recordManager = recordManager;

        setTitle("PrivateVaultSimple - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Main panel uses BorderLayout so it stretches when resized.
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(Theme.BACKGROUND);
        Theme.addPadding(main, 15);
        setContentPane(main);

        JLabel heading = new JLabel("Private Vault Login");
        heading.setFont(Theme.TITLE_FONT);
        heading.setForeground(Theme.PRIMARY);

        // GridLayout rows stretch automatically (this gives free resizing).
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBackground(Theme.BACKGROUND);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Theme.TEXT);
        usernameField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Theme.TEXT);
        passwordField = new JPasswordField(15);

        form.add(userLabel);
        form.add(usernameField);
        form.add(passLabel);
        form.add(passwordField);
        // One wrapper in NORTH: heading on top, form below.
        // (BorderLayout holds only one item per side, so we group them.)
        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setBackground(Theme.BACKGROUND);
        top.add(heading, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);
        main.add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBackground(Theme.BACKGROUND);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setBackground(Theme.BACKGROUND);

        loginButton = new JButton("Login");
        Theme.styleButton(loginButton, Theme.PRIMARY);

        registerButton = new JButton("Register");
        Theme.styleButton(registerButton, Theme.TEAL);

        buttons.add(loginButton);
        buttons.add(registerButton);
        bottom.add(buttons, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Theme.DANGER);
        bottom.add(statusLabel, BorderLayout.SOUTH);

        main.add(bottom, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> tryLogin());
        registerButton.addActionListener(e -> openRegister());

        pack();
        setSize(380, 240);
        setLocationRelativeTo(null);
    }

    private void openRegister() {

        RegisterFrame registerFrame = new RegisterFrame(userManager);
        registerFrame.setVisible(true);
    }

    private void tryLogin() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            User user = userManager.login(username, password);

            if (user != null) {
                MainFrame mainFrame = new MainFrame(userManager, recordManager, user.getUid());
                mainFrame.setVisible(true);
                dispose();
            } else {
                Theme.showStatus(statusLabel, "Wrong username or password.", false);
            }

        } catch (Exception e) {
            Theme.showStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }
}
