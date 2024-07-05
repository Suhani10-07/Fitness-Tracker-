//import javax.swing.*;
//import java.awt.*;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class LoginFrame extends JFrame {
//    private JTextField usernameField;
//    private JPasswordField passwordField;
//    private JButton loginButton;
//
//    public LoginFrame() {
//        setTitle("Login");
//        setSize(300, 200);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        initComponents();
//    }
//
//    private void initComponents() {
//        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
//
//        JLabel usernameLabel = new JLabel("Username:");
//        usernameField = new JTextField();
//        JLabel passwordLabel = new JLabel("Password:");
//        passwordField = new JPasswordField();
//
//        loginButton = new JButton("Login");
//        loginButton.addActionListener(e -> login());
//
//        panel.add(usernameLabel);
//        panel.add(usernameField);
//        panel.add(passwordLabel);
//        panel.add(passwordField);
//        panel.add(new JLabel());
//        panel.add(loginButton);
//
//        add(panel);
//    }
//
//    private void login() {
//        String username = usernameField.getText();
//        String password = new String(passwordField.getPassword());
//
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            pstmt.setString(2, password);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                // Successful login
//                SwingUtilities.invokeLater(() -> {
//                    new UserDashboard(username).setVisible(true);
//                    this.dispose();
//                });
//            } else {
//                // Invalid login
//                JOptionPane.showMessageDialog(this, "Invalid username or password.");
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error logging in: " + e.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new LoginFrame().setVisible(true);
//        });
//    }
//}
