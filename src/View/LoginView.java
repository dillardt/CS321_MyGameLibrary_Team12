package View;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

/**
 * Login screen. Contains username/password inputs and three action buttons.
 */
public class LoginView extends JPanel {

    // ── Shared palette ────────────────────────────────────────────────────────
    static final Color BG_DARK      = new Color(30,  34,  42);   // main background
    static final Color BG_PANEL     = new Color(40,  46,  56);   // card / panel bg
    static final Color ACCENT       = new Color(99, 132, 199);   // blue accent
    static final Color BTN_FG       = Color.WHITE;
    static final Color TEXT_FG      = new Color(220, 220, 230);
    static final Color FIELD_BG     = new Color(52,  58,  70);
    static final Color FIELD_FG     = new Color(230, 230, 235);

    private final JTextField     usernameField;
    private final JPasswordField passwordField;
    private final JButton        loginButton;
    private final JButton        createAccountButton;
    private final JButton        guestLoginButton;
    private final JLabel         statusLabel;

    public LoginView() {
        setBackground(BG_DARK);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("My Game Library", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(ACCENT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username row
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(styledLabel("Username:"), gbc);
        usernameField = styledField(18);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password row
        gbc.gridy = 2; gbc.gridx = 0;
        add(styledLabel("Password:"), gbc);
        passwordField = new JPasswordField(18);
        styleField(passwordField);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Login + Create Account buttons
        loginButton = styledButton("Login", ACCENT);
        gbc.gridy = 3; gbc.gridx = 0;
        add(loginButton, gbc);
        createAccountButton = styledButton("Create Account", new Color(70, 100, 140));
        gbc.gridx = 1;
        add(createAccountButton, gbc);

        // Guest button
        guestLoginButton = styledButton("Continue as Guest", new Color(60, 68, 82));
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        add(guestLoginButton, gbc);

        // Status message label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(TEXT_FG);
        gbc.gridy = 5;
        add(statusLabel, gbc);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getUsernameInput() { return usernameField.getText().trim(); }
    public String getPasswordInput() { return new String(passwordField.getPassword()); }
    public void clearPassword()      { passwordField.setText(""); }

    public void clearAllInputs() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }

    public void setStatusMessage(String message, boolean isError) {
        statusLabel.setText(message == null || message.isBlank() ? " " : message);
        statusLabel.setForeground(isError ? new Color(220, 80, 80) : new Color(100, 200, 120));
    }

    // ── Listener registration ─────────────────────────────────────────────────

    public void addLoginListener(ActionListener l)         { loginButton.addActionListener(l); }
    public void addCreateAccountListener(ActionListener l) { createAccountButton.addActionListener(l); }
    public void addGuestLoginListener(ActionListener l)    { guestLoginButton.addActionListener(l); }

    // ── Style helpers ─────────────────────────────────────────────────────────

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_FG);
        return l;
    }

    private JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(FIELD_BG);
        f.setForeground(FIELD_FG);
        f.setCaretColor(FIELD_FG);
        f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(70, 80, 100)),
                javax.swing.BorderFactory.createEmptyBorder(4, 6, 4, 6)));
    }

    static JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(BTN_FG);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return b;
    }
}