//package fitness_tracker;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//public class UserDashboard extends JFrame {
//    private JLabel welcomeLabel;
//    private JLabel userDetailsLabel;
//    private JLabel activitySummaryLabel;
//    private JLabel goalsLabel;
//    private JLabel activitiesLabel;
//    private JLabel activitiesDetailsLabel;
//    private JButton addActivityButton;
//    private JButton updateProfileButton;
//    private JButton logoutButton;
//    private String username;
//    private Map<String, LocalDateTime> activityStartTimes;
//
//    public UserDashboard(String username) {
//        this.username = username;
//        this.activityStartTimes = new HashMap<>();
//        setTitle("User Dashboard");
//        setSize(600, 400);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        initComponents();
//        loadUserData();
//        loadActivitySummary();
//        loadGoals();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout(10, 10)); // Adding padding
//
//        // Welcome message
//        welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
//        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 20));
//        add(welcomeLabel, BorderLayout.NORTH);
//
//        // Main panel
//        JPanel mainPanel = new JPanel(new GridLayout(6, 1, 10, 10)); // Adding padding and gaps
//
//        // User details label
//        userDetailsLabel = new JLabel();
//        mainPanel.add(userDetailsLabel);
//
//        // Activities label
//        activitiesLabel = new JLabel("Activities:");
//        mainPanel.add(activitiesLabel);
//
//        // Activity summary label
//        activitySummaryLabel = new JLabel();
//        mainPanel.add(activitySummaryLabel);
//
//        // Activities details label
//        activitiesDetailsLabel = new JLabel("Activity Details:");
//        mainPanel.add(activitiesDetailsLabel);
//
//        // Goals label
//        goalsLabel = new JLabel();
//        mainPanel.add(goalsLabel);
//
//        // Buttons panel
//        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Adding padding and gaps
//        addActivityButton = new JButton("Add Activity");
//        updateProfileButton = new JButton("Update Profile");
//        logoutButton = new JButton("Logout");
//
//        addActivityButton.addActionListener(e -> showAddActivityDialog());
//        updateProfileButton.addActionListener(e -> showUpdateProfileDialog());
//        logoutButton.addActionListener(e -> logout());
//
//        buttonsPanel.add(addActivityButton);
//        buttonsPanel.add(updateProfileButton);
//        buttonsPanel.add(logoutButton);
//
//        mainPanel.add(buttonsPanel);
//        add(mainPanel, BorderLayout.CENTER);
//    }
//
//    private void loadUserData() {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT * FROM users WHERE username = ?";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                String email = rs.getString("email");
//                userDetailsLabel.setText("Username: " + username + " | Email: " + email);
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage());
//        }
//    }
//
//    private void loadActivitySummary() {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT SUM(duration) AS total_duration, SUM(distance) AS total_distance, SUM(calories_burned) AS total_calories FROM activities WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                int totalDuration = rs.getInt("total_duration");
//                float totalDistance = rs.getFloat("total_distance");
//                int totalCalories = rs.getInt("total_calories");
//                activitySummaryLabel.setText("<html>Total Duration: " + totalDuration + " mins<br>Total Distance: " + totalDistance + " km<br>Total Calories: " + totalCalories + "</html>");
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading activity summary: " + e.getMessage());
//        }
//    }
//
//    private void loadGoals() {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT * FROM goals WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            StringBuilder goalsText = new StringBuilder("<html>");
//            while (rs.next()) {
//                String activityType = rs.getString("activity_type");
//                int targetDuration = rs.getInt("target_duration");
//                float targetDistance = rs.getFloat("target_distance");
//                int targetCalories = rs.getInt("target_calories");
//                String deadline = rs.getString("deadline");
//                goalsText.append("Goal: ").append(activityType)
//                        .append(" | Target Duration: ").append(targetDuration)
//                        .append(" mins | Target Distance: ").append(targetDistance)
//                        .append(" km | Target Calories: ").append(targetCalories)
//                        .append(" | Deadline: ").append(deadline).append("<br>");
//            }
//            goalsText.append("</html>");
//            goalsLabel.setText(goalsText.toString());
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading goals: " + e.getMessage());
//        }
//    }
//
//    private void showAddActivityDialog() {
//        JFrame frame = new JFrame("Add Activity");
//        frame.setSize(400, 300);
//        frame.setLocationRelativeTo(this);
//
//        JPanel panel = new JPanel(new GridLayout(7, 1));
//
//        JLabel selectLabel = new JLabel("Select Activity:");
//        panel.add(selectLabel);
//
//        String[] activities = {"Walking", "Running", "Cycling", "Swimming", "Zumba", "Dancing"};
//        JComboBox<String> activityComboBox = new JComboBox<>(activities);
//        panel.add(activityComboBox);
//
//        JLabel timerLabel = new JLabel("00:00:00");
//        panel.add(timerLabel);
//
//        Timer timer = new Timer(1000, new ActionListener() {
//            LocalDateTime startTime;
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (startTime != null) {
//                    LocalDateTime now = LocalDateTime.now();
//                    Duration duration = Duration.between(startTime, now);
//                    long hours = duration.toHours();
//                    long minutes = duration.toMinutesPart();
//                    long seconds = duration.toSecondsPart();
//                    timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
//                }
//            }
//        });
//
//        JButton startButton = new JButton("Start");
//        startButton.addActionListener(e -> {
//            String selectedActivity = (String) activityComboBox.getSelectedItem();
//            activityStartTimes.put(selectedActivity, LocalDateTime.now());
//            timer.start();
//        });
//
//        JButton stopButton = new JButton("Stop");
//        stopButton.addActionListener(e -> {
//            String selectedActivity = (String) activityComboBox.getSelectedItem();
//            LocalDateTime startTime = activityStartTimes.remove(selectedActivity);
//            if (startTime != null) {
//                LocalDateTime endTime = LocalDateTime.now();
//                long durationSeconds = Duration.between(startTime, endTime).getSeconds();
//                int caloriesBurned = calculateCaloriesBurned(selectedActivity, durationSeconds);
//                saveActivityToDatabase(selectedActivity, startTime, endTime, durationSeconds, caloriesBurned);
//                timer.stop();
//                timerLabel.setText("00:00:00");
//                JOptionPane.showMessageDialog(frame, "Activity stopped: " + selectedActivity +
//                        "\nDuration: " + durationSeconds + " seconds\nCalories Burned: " + caloriesBurned);
//            }
//        });
//
//        panel.add(startButton);
//        panel.add(stopButton);
//
//        frame.add(panel);
//        frame.setVisible(true);
//    }
//
//    private int calculateCaloriesBurned(String activityType, long durationSeconds) {
//        // Simulated calculation based on activity type and duration
//        int caloriesPerMinute = 10; // Example: 10 calories per minute
//        long durationMinutes = durationSeconds / 60;
//        return (int) (caloriesPerMinute * durationMinutes);
//    }
//
//    private void saveActivityToDatabase(String activityType, LocalDateTime startTime, LocalDateTime endTime, long durationSeconds, int caloriesBurned) {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "INSERT INTO activities (user_id, activity_type, start_time, end_time, duration, calories_burned) VALUES (?, ?, ?, ?, ?, ?)";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setInt(1, getUserId(username));
//            pstmt.setString(2, activityType);
//            pstmt.setTimestamp(3, Timestamp.valueOf(startTime));
//            pstmt.setTimestamp(4, Timestamp.valueOf(endTime));
//            pstmt.setLong(5, durationSeconds);
//            pstmt.setInt(6, caloriesBurned);
//            pstmt.executeUpdate();
//
//            // Update activity summary after adding the activity
//            loadActivitySummary();
//
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error saving activity to database: " + e.getMessage());
//        }
//    }
//
//    private void showUpdateProfileDialog() {
//        JFrame frame = new JFrame("Update Profile");
//        frame.setSize(300, 200);
//        frame.setLocationRelativeTo(this);
//
//        JPanel panel = new JPanel(new GridLayout(3, 2));
//
//        JLabel usernameLabel = new JLabel("Username:");
//        JTextField usernameField = new JTextField(username);
//        usernameField.setEditable(false); // Username cannot be edited
//
//        JLabel emailLabel = new JLabel("Email:");
//        JTextField emailField = new JTextField();
//        emailField.setText(getUserEmail());
//        
//        JButton updateButton = new JButton("Update");
//        updateButton.addActionListener(e -> {
//            String newEmail = emailField.getText();
//            if (updateUserEmail(newEmail)) {
//                JOptionPane.showMessageDialog(frame, "Profile updated successfully!");
//                frame.dispose();
//            } else {
//                JOptionPane.showMessageDialog(frame, "Failed to update profile.");
//            }
//        });
//
//        panel.add(usernameLabel);
//        panel.add(usernameField);
//        panel.add(emailLabel);
//        panel.add(emailField);
//        panel.add(updateButton);
//
//        frame.add(panel);
//        frame.setVisible(true);
//    }
//
//    private boolean updateUserEmail(String newEmail) {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "UPDATE users SET email = ? WHERE username = ?";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, newEmail);
//            pstmt.setString(2, username);
//            int rowsUpdated = pstmt.executeUpdate();
//            return rowsUpdated > 0;
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error updating user email: " + e.getMessage());
//            return false;
//        }
//    }
//
//    private String getUserEmail() {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT email FROM users WHERE username = ?";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getString("email");
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error fetching user email: " + e.getMessage());
//        }
//        return "";
//    }
//
//    private int getUserId(String username) {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT user_id FROM users WHERE username = ?";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt("user_id");
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error fetching user ID: " + e.getMessage());
//        }
//        return -1;
//    }
//
//    private void logout() {
//        new FitnessTrackerGUI().setVisible(true);
//        dispose();
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            UserDashboard dashboard = new UserDashboard("example_user");
//            dashboard.setVisible(true);
//        });
//    }
//}





//with goals section 
//package fitness_tracker;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//
//public class UserDashboard extends JFrame {
//    private JLabel welcomeLabel;
//    private JLabel userDetailsLabel;
//    private JLabel activitySummaryLabel;
//    private JLabel goalsLabel;
//    private JLabel activitiesLabel;
//    private JLabel activitiesDetailsLabel;
//    private JButton addActivityButton;
//    private JButton updateProfileButton;
//    private JButton logoutButton;
//    private JButton setGoalButton;
//    private String username;
//    private Map<String, LocalDateTime> activityStartTimes;
//
//    public UserDashboard(String username) {
//        this.username = username;
//        this.activityStartTimes = new HashMap<>();
//        setTitle("User Dashboard");
//        setSize(600, 400);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        initComponents();
//        loadUserData();
//        loadActivitySummary();
//        loadGoals();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout(10, 10)); // Adding padding
//
//        // Welcome message
//        welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
//        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 20));
//        add(welcomeLabel, BorderLayout.NORTH);
//
//        // Main panel
//        JPanel mainPanel = new JPanel(new GridLayout(6, 1, 10, 10)); // Adding padding and gaps
//
//        // User details label
//        userDetailsLabel = new JLabel();
//        mainPanel.add(userDetailsLabel);
//
//        // Activities label
//        activitiesLabel = new JLabel("Activities:");
//        mainPanel.add(activitiesLabel);
//
//        // Activity summary label
//        activitySummaryLabel = new JLabel();
//        mainPanel.add(activitySummaryLabel);
//
//        // Activities details label
//        activitiesDetailsLabel = new JLabel("Activity Details:");
//        mainPanel.add(activitiesDetailsLabel);
//
//        // Goals label
//        goalsLabel = new JLabel();
//        mainPanel.add(goalsLabel);
//
//        // Buttons panel
//        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Adding padding and gaps
//        addActivityButton = new JButton("Add Activity");
//        setGoalButton = new JButton("Set Goal");
//        updateProfileButton = new JButton("Update Profile");
//        logoutButton = new JButton("Logout");
//
//        addActivityButton.addActionListener(e -> showAddActivityDialog());
//        setGoalButton.addActionListener(e -> showSetGoalDialog());
//        updateProfileButton.addActionListener(e -> showUpdateProfileDialog());
//        logoutButton.addActionListener(e -> logout());
//
//        buttonsPanel.add(addActivityButton);
//        buttonsPanel.add(setGoalButton);
//        buttonsPanel.add(updateProfileButton);
//        buttonsPanel.add(logoutButton);
//
//        mainPanel.add(buttonsPanel);
//        add(mainPanel, BorderLayout.CENTER);
//    }
//
//    private void loadUserData() {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT * FROM users WHERE username = ?";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                String email = rs.getString("email");
//                userDetailsLabel.setText("Username: " + username + " | Email: " + email);
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage());
//        }
//    }
//
//    private void loadActivitySummary() {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT SUM(duration) AS total_duration, SUM(distance) AS total_distance, SUM(calories_burned) AS total_calories FROM activities WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                int totalDuration = rs.getInt("total_duration");
//                float totalDistance = rs.getFloat("total_distance");
//                int totalCalories = rs.getInt("total_calories");
//                activitySummaryLabel.setText("<html>Total Duration: " + totalDuration + " mins<br>Total Distance: " + totalDistance + " km<br>Total Calories: " + totalCalories + "</html>");
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading activity summary: " + e.getMessage());
//        }
//    }
//
//    private void loadGoals() {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT * FROM goals WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            StringBuilder goalsText = new StringBuilder("<html>");
//            while (rs.next()) {
//                String activityType = rs.getString("activity_type");
//                int targetDuration = rs.getInt("target_duration");
//                float targetDistance = rs.getFloat("target_distance");
//                int targetCalories = rs.getInt("target_calories");
//                String deadline = rs.getString("deadline");
//                goalsText.append("Goal: ").append(activityType)
//                        .append("<br>Target Duration: ").append(targetDuration)
//                        .append(" mins<br>Target Distance: ").append(targetDistance)
//                        .append(" km<br>Target Calories: ").append(targetCalories)
//                        .append("<br>Deadline: ").append(deadline).append("<br><br>");
//            }
//            goalsText.append("</html>");
//            goalsLabel.setText(goalsText.toString());
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading goals: " + e.getMessage());
//        }
//    }
//
//    private void showAddActivityDialog() {
//        JFrame frame = new JFrame("Add Activity");
//        frame.setSize(400, 300);
//        frame.setLocationRelativeTo(this);
//
//        JPanel panel = new JPanel(new GridLayout(7, 1));
//
//        JLabel selectLabel = new JLabel("Select Activity:");
//        panel.add(selectLabel);
//
//        String[] activities = {"Walking", "Running", "Cycling", "Swimming", "Zumba", "Dancing"};
//        JComboBox<String> activityComboBox = new JComboBox<>(activities);
//        panel.add(activityComboBox);
//
//        JLabel timerLabel = new JLabel("00:00:00", SwingConstants.CENTER);
//        timerLabel.setFont(new Font("Serif", Font.BOLD, 20));
//        panel.add(timerLabel);
//
//        Timer timer = new Timer(1000, new ActionListener() {
//            LocalDateTime startTime;
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (startTime != null) {
//                    LocalDateTime now = LocalDateTime.now();
//                    Duration duration = Duration.between(startTime, now);
//                    long hours = duration.toHours();
//                    long minutes = duration.toMinutesPart();
//                    long seconds = duration.toSecondsPart();
//                    timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
//                }
//            }
//        });
//
//        JButton startButton = new JButton("Start");
//        startButton.addActionListener(e -> {
//            String selectedActivity = (String) activityComboBox.getSelectedItem();
//            activityStartTimes.put(selectedActivity, LocalDateTime.now());
//            timer.start();
//        });
//
//        JButton stopButton = new JButton("Stop");
//        stopButton.addActionListener(e -> {
//            String selectedActivity = (String) activityComboBox.getSelectedItem();
//            LocalDateTime startTime = activityStartTimes.remove(selectedActivity);
//            if (startTime != null) {
//                LocalDateTime endTime = LocalDateTime.now();
//                long durationSeconds = Duration.between(startTime, endTime).getSeconds();
//                int caloriesBurned = calculateCaloriesBurned(selectedActivity, durationSeconds);
//                saveActivityToDatabase(selectedActivity, startTime, endTime, durationSeconds, caloriesBurned);
//                timer.stop();
//                timerLabel.setText("00:00:00");
//                JOptionPane.showMessageDialog(frame, "Activity stopped: " + selectedActivity +
//                        "\nDuration: " + durationSeconds + " seconds\nCalories Burned: " + caloriesBurned);
//            }
//        });
//
//        panel.add(startButton);
//        panel.add(stopButton);
//
//        frame.add(panel);
//        frame.setVisible(true);
//    }
//
//    private void showSetGoalDialog() {
//        JFrame frame = new JFrame("Set Goal");
//        frame.setSize(300, 250);
//        frame.setLocationRelativeTo(this);
//
//        JPanel panel = new JPanel(new GridLayout(5, 2));
//
//        JLabel activityLabel = new JLabel("Select Activity:");
//        String[] activities = {"Walking", "Running", "Cycling", "Swimming", "Zumba", "Dancing"};
//        JComboBox<String> activityComboBox = new JComboBox<>(activities);
//        panel.add(activityLabel);
//        panel.add(activityComboBox);
//
//        JLabel durationLabel = new JLabel("Target Duration (mins):");
//        JTextField durationField = new JTextField();
//        panel.add(durationLabel);
//        panel.add(durationField);
//
//        JLabel distanceLabel = new JLabel("Target Distance (km):");
//        JTextField distanceField = new JTextField();
//        panel.add(distanceLabel);
//        panel.add(distanceField);
//
//        JLabel caloriesLabel = new JLabel("Target Calories:");
//        JTextField caloriesField = new JTextField();
//        panel.add(caloriesLabel);
//        panel.add(caloriesField);
//
//        JLabel deadlineLabel = new JLabel("Deadline (YYYY-MM-DD):");
//        JTextField deadlineField = new JTextField();
//        panel.add(deadlineLabel);
//        panel.add(deadlineField);
//
//        JButton setGoalButton = new JButton("Set Goal");
//        setGoalButton.addActionListener(e -> {
//            String activityType = (String) activityComboBox.getSelectedItem();
//            int targetDuration = Integer.parseInt(durationField.getText());
//            float targetDistance = Float.parseFloat(distanceField.getText());
//            int targetCalories = Integer.parseInt(caloriesField.getText());
//            String deadline = deadlineField.getText();
//
//            if (setGoal(activityType, targetDuration, targetDistance, targetCalories, deadline)) {
//                JOptionPane.showMessageDialog(frame, "Goal set successfully!");
//                frame.dispose();
//            } else {
//                JOptionPane.showMessageDialog(frame, "Failed to set goal.");
//            }
//        });
//
//        panel.add(setGoalButton);
//
//        frame.add(panel);
//        frame.setVisible(true);
//    }
//
//    private boolean setGoal(String activityType, int targetDuration, float targetDistance, int targetCalories, String deadline) {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            int userId = getUserId(username);
//            if (userId == -1) {
//                JOptionPane.showMessageDialog(this, "User not found.");
//                return false;
//            }
//
//            String sql = "INSERT INTO goals (user_id, activity_type, target_duration, target_distance, target_calories, deadline) " +
//                    "VALUES (?, ?, ?, ?, ?, ?)";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setInt(1, userId);
//            pstmt.setString(2, activityType);
//            pstmt.setInt(3, targetDuration);
//            pstmt.setFloat(4, targetDistance);
//            pstmt.setInt(5, targetCalories);
//            pstmt.setString(6, deadline);
//
//            int rowsInserted = pstmt.executeUpdate();
//            return rowsInserted > 0;
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error setting goal: " + e.getMessage());
//            return false;
//        }
//    }
//
//    private int calculateCaloriesBurned(String activity, long durationSeconds) {
//        // Placeholder calculation, you can replace this with actual calorie calculation logic
//        int caloriesPerMinute = 10; // Example: 10 calories burned per minute
//        return (int) (durationSeconds / 60 * caloriesPerMinute);
//    }
//
//    private void saveActivityToDatabase(String activity, LocalDateTime startTime, LocalDateTime endTime, long durationSeconds, int caloriesBurned) {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            int userId = getUserId(username);
//            if (userId == -1) {
//                JOptionPane.showMessageDialog(this, "User not found.");
//                return;
//            }
//
//            String sql = "INSERT INTO activities (user_id, activity_type, start_time, end_time, duration, calories_burned, date) " +
//                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setInt(1, userId);
//            pstmt.setString(2, activity);
//            pstmt.setTimestamp(3, Timestamp.valueOf(startTime));
//            pstmt.setTimestamp(4, Timestamp.valueOf(endTime));
//            pstmt.setLong(5, durationSeconds);
//            pstmt.setInt(6, caloriesBurned);
//            pstmt.setObject(7, LocalDate.now());
//
//            int rowsInserted = pstmt.executeUpdate();
//            if (rowsInserted > 0) {
//                loadActivitySummary();
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error saving activity: " + e.getMessage());
//        }
//    }
//
//    private boolean updateUserEmail(String newEmail) {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "UPDATE users SET email = ? WHERE username = ?";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, newEmail);
//            pstmt.setString(2, username);
//            int rowsUpdated = pstmt.executeUpdate();
//            return rowsUpdated > 0;
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error updating user email: " + e.getMessage());
//            return false;
//        }
//    }
//
//    private String getUserEmail() {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT email FROM users WHERE username = ?";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getString("email");
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error fetching user email: " + e.getMessage());
//        }
//        return "";
//    }
//
//    private int getUserId(String username) {
//        try (Connection con = DatabaseConnection.getConnection()) {
//            String sql = "SELECT user_id FROM users WHERE username = ?";
//            PreparedStatement pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt("user_id");
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error fetching user ID: " + e.getMessage());
//        }
//        return -1;
//    }
//
//    private void showUpdateProfileDialog() {
//        JFrame frame = new JFrame("Update Profile");
//        frame.setSize(300, 150);
//        frame.setLocationRelativeTo(this);
//
//        JPanel panel = new JPanel(new GridLayout(2, 2));
//
//        JLabel emailLabel = new JLabel("New Email:");
//        JTextField emailField = new JTextField();
//        panel.add(emailLabel);
//        panel.add(emailField);
//
//        JButton updateButton = new JButton("Update");
//        updateButton.addActionListener(e -> {
//            String newEmail = emailField.getText();
//            if (!newEmail.isEmpty()) {
//                if (updateUserEmail(newEmail)) {
//                    JOptionPane.showMessageDialog(frame, "Email updated successfully!");
//                    frame.dispose();
//                } else {
//                    JOptionPane.showMessageDialog(frame, "Failed to update email.");
//                }
//            } else {
//                JOptionPane.showMessageDialog(frame, "Email field cannot be empty.");
//            }
//        });
//
//        panel.add(updateButton);
//
//        frame.add(panel);
//        frame.setVisible(true);
//    }
//
//    private void logout() {
//        new FitnessTrackerGUI().setVisible(true);
//        dispose();
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            UserDashboard dashboard = new UserDashboard("example_user");
//            dashboard.setVisible(true);
//        });
//    }
//}









package fitness_tracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class UserDashboard extends JFrame {
    private JLabel welcomeLabel;
    private JLabel userDetailsLabel;
    private JLabel activitySummaryLabel;
    private JTextArea goalsLabel;
    private JLabel activitiesLabel;
    private JButton addActivityButton;
    private JButton updateProfileButton;
    private JButton logoutButton;
    private JButton setGoalButton;
    private String username;
    private Map<String, LocalDateTime> activityStartTimes;

    public UserDashboard(String username) {
        this.username = username;
        this.activityStartTimes = new HashMap<>();
        setTitle("User Dashboard");
        setSize(800, 400); // Increased width to 800 pixels
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadUserData();
        loadActivitySummary();
        loadGoals();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5)); // Adding padding

        // Welcome message
        welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel(new GridLayout(6, 1, 10, 10)); // Adding padding and gaps

        // User details label
        userDetailsLabel = new JLabel();
        mainPanel.add(userDetailsLabel);

        // Activities label
        activitiesLabel = new JLabel("Activities:");
        mainPanel.add(activitiesLabel);

        // Activity summary label
        activitySummaryLabel = new JLabel();
        mainPanel.add(activitySummaryLabel);

        // Goals label
        goalsLabel = new JTextArea(10, 50); // Adjust rows and columns as needed
        goalsLabel.setEditable(false);
        JScrollPane goalsScrollPane = new JScrollPane(goalsLabel);
        mainPanel.add(goalsScrollPane);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Adding padding and gaps
        addActivityButton = new JButton("Add Activity");
        setGoalButton = new JButton("Set Goal");
        updateProfileButton = new JButton("Update Profile");
        logoutButton = new JButton("Logout");

        addActivityButton.addActionListener(e -> showAddActivityDialog());
        setGoalButton.addActionListener(e -> showSetGoalDialog());
        updateProfileButton.addActionListener(e -> showUpdateProfileDialog());
        logoutButton.addActionListener(e -> logout());

        buttonsPanel.add(addActivityButton);
        buttonsPanel.add(setGoalButton);
        buttonsPanel.add(updateProfileButton);
        buttonsPanel.add(logoutButton);

        mainPanel.add(buttonsPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadUserData() {
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String email = rs.getString("email");
                userDetailsLabel.setText("Username: " + username + " | Email: " + email);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage());
        }
    }

    private void loadActivitySummary() {
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT SUM(duration) AS total_duration, SUM(distance) AS total_distance, SUM(calories_burned) AS total_calories FROM activities WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int totalDuration = rs.getInt("total_duration");
                float totalDistance = rs.getFloat("total_distance");
                int totalCalories = rs.getInt("total_calories");
                activitySummaryLabel.setText("<html>Total Duration: " + totalDuration + " sec<br>Total Distance: " + totalDistance + " km<br>Total Calories: " + totalCalories + "</html>");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading activity summary: " + e.getMessage());
        }
    }

    private void loadGoals() {
    	
    	
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM goals WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder goalsText = new StringBuilder("<html>");
            int index = 1;

            while (rs.next()) {
                String activityType = rs.getString("activity_type");
                int targetDuration = rs.getInt("target_duration");
                float targetDistance = rs.getFloat("target_distance");
                int targetCalories = rs.getInt("target_calories");
                String deadline = rs.getString("deadline");

                goalsText.append(index++).append("- ").append("<br>")
                        .append("Select Activity: ").append(activityType).append("<br>")
                        .append("Target Duration: ").append(targetDuration).append(" mins<br>")
                        .append("Target Distance: ").append(targetDistance).append(" km<br>")
                        .append("Target Calories: ").append(targetCalories).append("<br>")
                        .append("Deadline: ").append(deadline).append("<br><br>");
            }

            goalsText.append("</html>");
            goalsLabel.setText(goalsText.toString());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading goals: " + e.getMessage());
        }
    }

    private void showAddActivityDialog() {
        JFrame frame = new JFrame("Add Activity");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(7, 1));

        JLabel selectLabel = new JLabel("Select Activity:");
        panel.add(selectLabel);

        String[] activities = {"Walking", "Running", "Cycling", "Swimming", "Zumba", "Dancing"};
        JComboBox<String> activityComboBox = new JComboBox<>(activities);
        panel.add(activityComboBox);

        JLabel timerLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(timerLabel);

        Timer timer = new Timer(1000, new ActionListener() {
            LocalDateTime startTime;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime != null) {
                    LocalDateTime now = LocalDateTime.now();
                    Duration duration = Duration.between(startTime, now);
                    long hours = duration.toHours();
                    long minutes = duration.toMinutesPart();
                    long seconds = duration.toSecondsPart();
                    timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                }
            }
        });

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            String selectedActivity = (String) activityComboBox.getSelectedItem();
            activityStartTimes.put(selectedActivity, LocalDateTime.now());
            timer.start(); // Start the timer when activity is started
        });

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> {
            String selectedActivity = (String) activityComboBox.getSelectedItem();
            LocalDateTime startTime = activityStartTimes.remove(selectedActivity);
            if (startTime != null) {
                LocalDateTime endTime = LocalDateTime.now();
                long durationSeconds = Duration.between(startTime, endTime).getSeconds();
                int caloriesBurned = calculateCaloriesBurned(selectedActivity, durationSeconds);
                saveActivityToDatabase(selectedActivity, startTime, endTime, durationSeconds, caloriesBurned);
                timer.stop(); // Stop the timer when activity is stopped
                timerLabel.setText("00:00:00");
                JOptionPane.showMessageDialog(frame, "Activity stopped: " + selectedActivity +
                        "\nDuration: " + durationSeconds + " seconds\nCalories Burned: " + caloriesBurned);
            }
        });

        panel.add(startButton);
        panel.add(stopButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void showSetGoalDialog() {
        JFrame frame = new JFrame("Set Goal");
        frame.setSize(300, 250);
        frame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel activityLabel = new JLabel("Select Activity:");
        String[] activities = {"Walking", "Running", "Cycling", "Swimming", "Zumba", "Dancing"};
        JComboBox<String> activityComboBox = new JComboBox<>(activities);
        panel.add(activityLabel);
        panel.add(activityComboBox);

        JLabel durationLabel = new JLabel("Target Duration (mins):");
        JTextField durationField = new JTextField();
        panel.add(durationLabel);
        panel.add(durationField);

        JLabel distanceLabel = new JLabel("Target Distance (km):");
        JTextField distanceField = new JTextField();
        panel.add(distanceLabel);
        panel.add(distanceField);

        JLabel caloriesLabel = new JLabel("Target Calories:");
        JTextField caloriesField = new JTextField();
        panel.add(caloriesLabel);
        panel.add(caloriesField);

        JLabel deadlineLabel = new JLabel("Deadline (yyyy-MM-dd):");
        JTextField deadlineField = new JTextField();
        panel.add(deadlineLabel);
        panel.add(deadlineField);

        JButton setGoalButton = new JButton("Set Goal");
        setGoalButton.addActionListener(e -> {
            String activityType = (String) activityComboBox.getSelectedItem();
            int targetDuration = Integer.parseInt(durationField.getText());
            float targetDistance = Float.parseFloat(distanceField.getText());
            int targetCalories = Integer.parseInt(caloriesField.getText());
            String deadline = deadlineField.getText();

            if (setGoal(activityType, targetDuration, targetDistance, targetCalories, deadline)) {
                JOptionPane.showMessageDialog(frame, "Goal set successfully!");
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to set goal.");
            }
        });

        panel.add(setGoalButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private boolean setGoal(String activityType, int targetDuration, float targetDistance, int targetCalories, String deadline) {
        try (Connection con = DatabaseConnection.getConnection()) {
            int userId = getUserId(username);
            if (userId == -1) {
                JOptionPane.showMessageDialog(this, "User not found.");
                return false;
            }

            String sql = "INSERT INTO goals (user_id, activity_type, target_duration, target_distance, target_calories, deadline) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, activityType);
            pstmt.setInt(3, targetDuration);
            pstmt.setFloat(4, targetDistance);
            pstmt.setInt(5, targetCalories);
            pstmt.setString(6, deadline);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error setting goal: " + e.getMessage());
            return false;
        }
    }

    private int calculateCaloriesBurned(String activity, long durationSeconds) {
        // Placeholder calculation, you can replace this with actual calorie calculation logic
        int caloriesPerMinute = 10; // Example: 10 calories burned per minute
        return (int) (durationSeconds / 60 * caloriesPerMinute);
    }

    private void saveActivityToDatabase(String activity, LocalDateTime startTime, LocalDateTime endTime, long durationSeconds, int caloriesBurned) {
        try (Connection con = DatabaseConnection.getConnection()) {
            int userId = getUserId(username);
            if (userId == -1) {
                JOptionPane.showMessageDialog(this, "User not found.");
                return;
            }

            String sql = "INSERT INTO activities (user_id, activity_type, start_time, end_time, duration, calories_burned, date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, activity);
            pstmt.setTimestamp(3, Timestamp.valueOf(startTime));
            pstmt.setTimestamp(4, Timestamp.valueOf(endTime));
            pstmt.setLong(5, durationSeconds);
            pstmt.setInt(6, caloriesBurned);
            pstmt.setObject(7, LocalDate.now());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                loadActivitySummary();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving activity: " + e.getMessage());
        }
    }

    private boolean updateUserEmail(String newEmail) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "UPDATE users SET email = ? WHERE username = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, newEmail);
            pstmt.setString(2, username);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating user email: " + e.getMessage());
            return false;
        }
    }

    private String getUserEmail() {
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT email FROM users WHERE username = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching user email: " + e.getMessage());
        }
        return "";
    }

    private int getUserId(String username) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching user ID: " + e.getMessage());
        }
        return -1;
    }

    private void showUpdateProfileDialog() {
        JFrame frame = new JFrame("Update Profile");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel emailLabel = new JLabel("New Email:");
        JTextField emailField = new JTextField();
        panel.add(emailLabel);
        panel.add(emailField);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            String newEmail = emailField.getText();
            if (!newEmail.isEmpty()) {
                if (updateUserEmail(newEmail)) {
                    JOptionPane.showMessageDialog(frame, "Email updated successfully!");
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to update email.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Email field cannot be empty.");
            }
        });

        panel.add(updateButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void logout() {
        new FitnessTrackerGUI().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserDashboard dashboard = new UserDashboard("example_user");
            dashboard.setVisible(true);
        });
    }
}

