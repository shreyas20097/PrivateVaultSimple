import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

/**
 * Small helper that keeps all colors in one place.
 * This is still basic OOPC: only public static final fields
 * and small static methods, no new library or pattern.
 */
public class Theme {

    // Backgrounds
    public static final Color BACKGROUND = new Color(240, 244, 248);
    public static final Color CARD = Color.WHITE;

    // Text
    public static final Color TEXT = new Color(30, 41, 59);
    public static final Color MUTED = new Color(100, 116, 139);

    // Accent colors for buttons and status
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(217, 119, 6);
    public static final Color TEAL = new Color(13, 148, 136);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    // Give a button a solid color with white text.
    public static void styleButton(JButton button, Color background) {
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
    }

    // Give a panel some breathing space around its contents.
    public static void addPadding(JComponent panel, int padding) {
        panel.setBorder(new EmptyBorder(padding, padding, padding, padding));
    }

    // Show a short message in green (good) or red (bad).
    public static void showStatus(javax.swing.JLabel label, String message, boolean good) {
        label.setForeground(good ? SUCCESS : DANGER);
        label.setText(message);
    }

    // Append one colored line to a JTextPane. Bold is used for headings.
    public static void append(JTextPane pane, String text, Color color, boolean bold) {
        try {
            StyledDocument doc = pane.getStyledDocument();
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setForeground(style, color);
            StyleConstants.setBold(style, bold);
            StyleConstants.setFontFamily(style, "SansSerif");
            StyleConstants.setFontSize(style, 12);
            doc.insertString(doc.getLength(), text, style);
        } catch (Exception e) {
            // If styling fails, fall back to plain text so nothing is lost.
            try {
                pane.getStyledDocument().insertString(pane.getStyledDocument().getLength(), text, null);
            } catch (Exception ignored) {
            }
        }
    }

    // Shortcut for normal (non-bold) colored text.
    public static void append(JTextPane pane, String text, Color color) {
        append(pane, text, color, false);
    }
}
