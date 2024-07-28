import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class NoTrainerGymApp {
    // MySQL connection variables
    static final String DB_URL = "jdbc:mysql://localhost:3306/notrainer_gym";
    static final String DB_USER = "root"; // Your MySQL username
    static final String DB_PASS = "geezer4294"; // Your MySQL password

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NoTrainerGymApp().createAndShowGUI());
    }

    public void createAndShowGUI() {
        frame = new JFrame("NoTrainer Gym");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LoginPanel");
        mainPanel.add(createUserPanel(), "UserPanel");
        mainPanel.add(createAdminPanel(), "AdminPanel");

        frame.add(mainPanel);
        cardLayout.show(mainPanel, "LoginPanel");
        frame.setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JTextField userField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");
        JLabel messageLabel = new JLabel();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(userField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(passField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);
        gbc.gridx = 1;
        loginPanel.add(signUpButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(messageLabel, gbc);
        
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (authenticateUser(username, password)) {
                cardLayout.show(mainPanel, "UserPanel");
            } else if (authenticateAdmin(username, password)) {
                cardLayout.show(mainPanel, "AdminPanel");
            } else {
                messageLabel.setText("Invalid login. Try again.");
            }
        });

        signUpButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (signUpUser(username, password)) {
                messageLabel.setText("Sign up successful! You can now log in.");
            } else {
                messageLabel.setText("Sign up failed. Try a different username.");
            }
        });

        return loginPanel;
    }

    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Welcome User!");
        JButton beginnerButton = new JButton("Beginner");
        JButton trainedButton = new JButton("Trained");
        JPanel exercisePanel = new JPanel(new CardLayout());

        JPanel beginnerExercises = createExercisesPanel();
        JPanel trainedExercises = createExercisesPanel();

        exercisePanel.add(beginnerExercises, "BeginnerExercises");
        exercisePanel.add(trainedExercises, "TrainedExercises");

        beginnerButton.addActionListener(e -> ((CardLayout) exercisePanel.getLayout()).show(exercisePanel, "BeginnerExercises"));
        trainedButton.addActionListener(e -> ((CardLayout) exercisePanel.getLayout()).show(exercisePanel, "TrainedExercises"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(beginnerButton);
        buttonPanel.add(trainedButton);

        userPanel.add(welcomeLabel, BorderLayout.NORTH);
        userPanel.add(buttonPanel, BorderLayout.CENTER);
        userPanel.add(exercisePanel, BorderLayout.SOUTH);

        return userPanel;
    }

    private JPanel createAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        JLabel adminLabel = new JLabel("Welcome Admin!");
        adminPanel.add(adminLabel, BorderLayout.NORTH);
        // Add more admin functionalities as needed
        return adminPanel;
    }

    private JPanel createExercisesPanel() {
        JPanel exercisesPanel = new JPanel(new GridLayout(3, 2));
        String[] exercises = {"Chest", "Shoulder", "Biceps", "Triceps", "Leg", "Calf", "Shrugs"};

        for (String exercise : exercises) {
            JButton exerciseButton = new JButton(exercise);
            exerciseButton.addActionListener(e -> showExerciseDetails(exercise));
            exercisesPanel.add(exerciseButton);
        }

        return exercisesPanel;
    }

    private void showExerciseDetails(String exercise) {
        JFrame exerciseFrame = new JFrame(exercise);
        exerciseFrame.setSize(400, 400);
        JLabel exerciseLabel = new JLabel(new ImageIcon("images/" + exercise.toLowerCase() + ".jpg")); // Ensure image paths are correct
        exerciseFrame.add(exerciseLabel);
        exerciseFrame.setVisible(true);
    }

    private boolean authenticateUser(String username, String password) {
        return authenticate(username, password, "user");
    }

    private boolean authenticateAdmin(String username, String password) {
        return authenticate(username, password, "admin");
    }

    private boolean authenticate(String username, String password, String userType) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ? AND user_type = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, userType);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean signUpUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password, user_type) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, "user");
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
