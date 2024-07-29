import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class NoTrainerGymApp {
    // MySQL connection variables
    static final String DB_URL = "jdbc:mysql://localhost:3306/notrainer_gym";
    static final String DB_USER = "root"; // Your MySQL username
    static final String DB_PASS = "root"; // Your MySQL password

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

        beginnerButton.addActionListener(e -> showExercisesPanel("Beginner Exercises"));
        trainedButton.addActionListener(e -> showExercisesPanel("Trained Exercises"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(beginnerButton);
        buttonPanel.add(trainedButton);

        userPanel.add(welcomeLabel, BorderLayout.NORTH);
        userPanel.add(buttonPanel, BorderLayout.CENTER);

        return userPanel;
    }

    private void showExercisesPanel(String title) {
        JFrame exercisesFrame = new JFrame(title);
        exercisesFrame.setSize(800, 600);
        exercisesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        exercisesFrame.setLayout(new BorderLayout());

        JPanel exercisesPanel = createExercisesPanel();

        exercisesFrame.add(exercisesPanel, BorderLayout.CENTER);
        exercisesFrame.setVisible(true);
    }

    private JPanel createExercisesPanel() {
        JPanel exercisesPanel = new JPanel(new GridLayout(3, 2));
        String[] exercises = { "Chest", "Shoulder", "Biceps", "Triceps", "Leg", "Calf", "Shrugs" };

        for (String exercise : exercises) {
            JButton exerciseButton = new JButton(exercise);
            exerciseButton.addActionListener(e -> showExerciseSubList(exercise));
            exercisesPanel.add(exerciseButton);
        }

        return exercisesPanel;
    }

    private void showExerciseSubList(String exercise) {
        JFrame subListFrame = new JFrame(exercise + " Exercises");
        subListFrame.setSize(400, 400);
        subListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        subListFrame.setLayout(new GridLayout(5, 1));

        // Example sub-list items, replace with actual exercises
        String[] subListItems = { "Exercise 1", "Exercise 2", "Exercise 3", "Exercise 4", "Exercise 5" };
        for (String item : subListItems) {
            JButton itemButton = new JButton(item);
            itemButton.addActionListener(e -> showExerciseDetails(item));
            subListFrame.add(itemButton);
        }

        subListFrame.setVisible(true);
    }

    private void showExerciseDetails(String exercise) {
        JFrame exerciseFrame = new JFrame(exercise);
        exerciseFrame.setSize(400, 400);
        exerciseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel exerciseLabel = new JLabel(new ImageIcon("images/" + exercise.toLowerCase() + ".jpg")); // Ensure image
        // paths are
        // correct
        exerciseFrame.add(exerciseLabel);
        exerciseFrame.setVisible(true);
    }

    private JPanel createAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        JLabel adminLabel = new JLabel("Welcome Admin!");
        // adminLabel.setSize(200, 200);
        adminPanel.add(adminLabel, BorderLayout.NORTH);
        // calling manageusers()
        JButton manageUsersButton = new JButton("Manage Users");
        manageUsersButton.addActionListener(e -> manageUsers());
        adminPanel.add(manageUsersButton, BorderLayout.CENTER);
        return adminPanel;
    }

    private void manageUsers() {
        JFrame manageUsersFrame = new JFrame("Manage Users");
        manageUsersFrame.setSize(800, 600);
        manageUsersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add user management functionalities here (to be added later)

        manageUsersFrame.setVisible(true);
    }

    private boolean authenticateUser(String username, String password) {
        return authenticate(username, password, "user");
    }

    private boolean authenticateAdmin(String username, String password) {
        return authenticate(username, password, "admin");
    }

    private boolean authenticate(String username, String password, String userType) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM users WHERE username = ? AND password = ? AND user_type = ?")) {
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
                PreparedStatement ps = conn
                        .prepareStatement("INSERT INTO users (username, password, user_type) VALUES (?, ?, ?)")) {
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
