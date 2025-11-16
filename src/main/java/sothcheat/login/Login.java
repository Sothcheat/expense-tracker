package sothcheat.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import sothcheat.dashboard.Dashboard;
import sothcheat.manager.FormsManager;
import sothcheat.manager.SessionManager;
import sothcheat.models.User;
import sothcheat.services.UserService;
import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JPanel {
    public Login() {
        userService = new UserService();
        init();
    }

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel emailLabel;
    private JLabel passwordLabel;
    private UserService userService;

    private void signInButtonClicked() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password!");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userService.loginUser(email, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();

                    if (user != null) {
                        SessionManager.getInstance().setCurrentUser(user);

                        if (SessionManager.getInstance().getCurrentUser() != null) {
                            JOptionPane.showMessageDialog(Login.this,
                                    "Login Successful! Welcome " + user.getUsername());
                            FormsManager.getInstance().showForm(new Dashboard());
                        } else {
                            JOptionPane.showMessageDialog(Login.this,
                                    "Session error! Please try again.");
                            loginButton.setEnabled(true);
                            loginButton.setText("SIGN IN");
                        }
                    } else {
                        JOptionPane.showMessageDialog(Login.this,
                                "Invalid email or password!");
                        loginButton.setEnabled(true);
                        loginButton.setText("SIGN IN");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Login.this,
                            "Login failed: " + e.getMessage());
                    e.printStackTrace();
                    loginButton.setEnabled(true);
                    loginButton.setText("SIGN IN");
                }
            }
        };

        worker.execute();
    }

    private void init() {
        setBackground(Color.WHITE);
        // setLayout(new MigLayout("fill, insets 0", "[40%,grow][60%,grow]", "[grow]"));
        setLayout(new MigLayout("fill, insets 20", "[center,grow]", "[center,grow]"));
        emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        loginButton = new JButton("SIGN IN");
        loginButton.setIcon(new ImageIcon(getClass().getResource("/assets/icons/login.png")));
        loginButton.addActionListener(e -> signInButtonClicked());


        JPanel panel = new JPanel(new MigLayout("wrap,fillx, insets 35 45 30 45","[fill, 420]"));
        panel.setBackground(Color.WHITE);
        panel.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:0;");
        JLabel titleLabel = new JLabel("Expense Tracker");
        JLabel descriptionLabel = new JLabel("Manage your expense today with Expense Tracker");
        JLabel signInHeader = new JLabel("Sign in");

        titleLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "font:bold +23;" +
                "foreground:#3D3828");
        descriptionLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "[light]foreground:lighten(@foreground,30%)");
        signInHeader.putClientProperty(FlatClientProperties.STYLE,"" +
                "font:bold +14;" +
                "foreground:#3D3828");
        emailLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "foreground:#3D3828");
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Enter your email");

        passwordLabel.putClientProperty(FlatClientProperties.STYLE,"" +
                "foreground:#3D3828");
        passwordField.putClientProperty(FlatClientProperties.STYLE,"" +
                "showRevealButton:true");
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Enter your password");

        loginButton.putClientProperty(FlatClientProperties.STYLE,"" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        panel.add(titleLabel);
        panel.add(descriptionLabel);
        panel.add(signInHeader, "gapy 24");

        panel.add(emailLabel, "gapy 8");
        panel.add(emailField, "wrap, w 468!, h 36!");
        panel.add(passwordLabel, "gapy 8");
        panel.add(passwordField, "wrap, h 36!");
        panel.add(loginButton, "gapy 30, h 36!");
        panel.add(createSignUpLabel(), "gapy 10");
        add(panel);
    }

    private Component createSignUpLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));
        Color normal = new Color(0x3D3828);
        Color hover = new Color(0x4876FD);

        panel.putClientProperty(FlatClientProperties.STYLE,"" +
                "background:null");
        JButton registerButton = new JButton("Register Now!"); //<html><a href="#">Register Now!</a></html>
        registerButton.setContentAreaFilled(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        registerButton.setForeground(normal);

        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setForeground(hover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setForeground(normal);
            }
        });

        registerButton.addActionListener(actionEvent -> {

            LoginContainer container = LoginContainer.getInstance();
            if(container != null) {
                container.showForm(new Register());
            } else {
                FormsManager.getInstance().showForm(new Register());
            }
        });
        JLabel label = new JLabel("Don't have an account?");
        label.putClientProperty(FlatClientProperties.STYLE,"" +
                "[light]foreground:lighten(@foreground,30%)");
        panel.add(label);
        panel.add(registerButton);

        return panel;
    }

}
