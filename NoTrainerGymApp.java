import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class NoTrainerGymApp {
    // MySQL connection variables
    static final String DB_URL = "jdbc:mysql://localhost:3306/notrainer_gym";
    static final String DB_USER = "root"; // Your MySQL username
    static final String DB_PASS = "geezer4294"; // Your MySQL password

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Map for exercises and their sublists
    private Map<String, String[]> exerciseSubLists;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NoTrainerGymApp().createAndShowGUI());
    }

    public NoTrainerGymApp() {
        // Initialize exercise sublists
        exerciseSubLists = new HashMap<>();
        exerciseSubLists.put("Chest", new String[]{"Bench Press", "Incline Dumbbell Press", "Cable Crossover", "Chest Dip", "Pec Deck"});
        exerciseSubLists.put("Shoulder", new String[]{"Shoulder Press", "Lateral Raise", "Front Raise", "Reverse Fly", "Arnold Press"});
        exerciseSubLists.put("Leg", new String[]{"Squat", "Leg Press", "Leg Extension", "Hamstring Curl", "Calf Raise"});
        exerciseSubLists.put("Shrugs", new String[]{"Barbell Shrug", "Dumbbell Shrug", "Smith Machine Shrug", "Behind-the-Back Shrug", "Trap Bar Shrug"});
        exerciseSubLists.put("Biceps", new String[]{"Bicep Curl", "Hammer Curl", "Preacher Curl", "Concentration Curl", "Cable Curl"});
        exerciseSubLists.put("Triceps", new String[]{"Tricep Extension", "Skull Crusher", "Tricep Dip", "Close Grip Bench Press", "Cable Pushdown"});
        exerciseSubLists.put("Calf", new String[]{"Standing Calf Raise", "Seated Calf Raise", "Leg Press Calf Raise", "Smith Machine Calf Raise", "Single-Leg Calf Raise"});
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
        frame.setLocationRelativeTo(null); // Center the frame
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

        // Add SoloFit image to the login panel
        ImageIcon logoIcon = new ImageIcon("images/SoloFit.png");
        JLabel logoLabel = new JLabel(logoIcon);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(logoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        loginPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        loginPanel.add(signUpButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
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
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("Welcome", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 50)); // Bigger letters

        JButton beginnerButton = new JButton("Beginner");
        JButton trainedButton = new JButton("Trained");

        beginnerButton.setPreferredSize(new Dimension(200, 90)); // Increase button size
        trainedButton.setPreferredSize(new Dimension(200, 90)); // Increase button size

        beginnerButton.addActionListener(e -> showExercisesPanel("Beginner Exercises"));
        trainedButton.addActionListener(e -> showExercisesPanel("Trained Exercises"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(welcomeLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        centerPanel.add(beginnerButton, gbc);

        gbc.gridx = 1;
        centerPanel.add(trainedButton, gbc);

        userPanel.add(centerPanel, BorderLayout.CENTER);

        return userPanel;
    }

    private void showExercisesPanel(String title) {
        JFrame exercisesFrame = new JFrame(title);
        exercisesFrame.setSize(800, 600);
        exercisesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        exercisesFrame.setLayout(new BorderLayout());
        exercisesFrame.setLocationRelativeTo(null); // Center the frame

        JPanel exercisesPanel = createExercisesPanel();

        // Add back button to close the exercises frame and return to the user panel
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> exercisesFrame.dispose());

        exercisesFrame.add(exercisesPanel, BorderLayout.CENTER);
        exercisesFrame.add(backButton, BorderLayout.SOUTH);
        exercisesFrame.setVisible(true);
    }

    private JPanel createExercisesPanel() {
        JPanel exercisesPanel = new JPanel(new GridLayout(3, 2));
        String[] exercises = {"Chest", "Shoulder", "Biceps", "Triceps", "Leg", "Calf", "Shrugs"};

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
        subListFrame.setLocationRelativeTo(null); // Center the frame

        // Get the sublist items for the selected exercise
        String[] subListItems = exerciseSubLists.getOrDefault(exercise, new String[]{});

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
        exerciseFrame.setLocationRelativeTo(null); // Center the frame

        String imagePath = "images/default_image.gif"; // Default path

        // Special cases for exercises under Chest, Shoulder, Leg, Shrugs, Biceps, Triceps, and Calf categories
        switch (exercise) {
            case "Bench Press":
                imagePath = "images/BenchPress.gif";
                break;
            case "Incline Dumbbell Press":
                imagePath = "images/InclineDumbellPress.gif";
                break;
            case "Cable Crossover":
                imagePath = "images/CableCrossOver.gif";
                break;
            case "Chest Dip":
                imagePath = "images/ChestDips.gif";
                break;
            case "Pec Deck":
                imagePath = "images/PecDeck.gif";
                break;
            case "Shoulder Press":
                imagePath = "images/ShoulderPress.gif";
                break;
            case "Lateral Raise":
                imagePath = "images/LateralRaise.gif";
                break;
            case "Front Raise":
                imagePath = "images/FrontRaise.gif";
                break;
            case "Reverse Fly":
                imagePath = "images/ReverseFly.gif";
                break;
            case "Arnold Press":
                imagePath = "images/ArnoldPress.gif";
                break;
            case "Squat":
                imagePath = "images/Squat.gif";
                break;
            case "Leg Press":
                imagePath = "images/LegPress.gif";
                break;
            case "Leg Extension":
                imagePath = "images/LegExtension.gif";
                break;
            case "Hamstring Curl":
                imagePath = "images/HamstringCurl.gif";
                break;
            case "Calf Raise":
                imagePath = "images/CalfRaise.gif";
                break;
            case "Barbell Shrug":
                imagePath = "images/BarbellShrug.gif";
                break;
            case "Dumbbell Shrug":
                imagePath = "images/DumbbellShrug.gif";
                break;
            case "Smith Machine Shrug":
                imagePath = "images/SmithMachineShrug.gif";
                break;
            case "Behind-the-Back Shrug":
                imagePath = "images/BehindTheBack.gif";
                break;
            case "Trap Bar Shrug":
                imagePath = "images/TrapBarShrug.gif";
                break;
            case "Bicep Curl":
                imagePath = "images/BicepCurl.gif";
                break;
            case "Hammer Curl":
                imagePath = "images/HammerCurl.gif";
                break;
            case "Preacher Curl":
                imagePath = "images/PreacherCurl.gif";
                break;
            case "Concentration Curl":
                imagePath = "images/ConcentrationCurl.gif";
                break;
            case "Cable Curl":
                imagePath = "images/CableCurl.gif";
                break;
            case "Tricep Extension":
                imagePath = "images/TricepExtension.gif";
                break;
            case "Skull Crusher":
                imagePath = "images/SkullCrusher.gif";
                break;
            case "Tricep Dip":
                imagePath = "images/TricepDip.gif";
                break;
            case "Close Grip Bench Press":
                imagePath = "images/CloseGripDumbellPress.gif";
                break;
            case "Cable Pushdown":
                imagePath = "images/CablePushdown.gif";
                break;
            case "Standing Calf Raise":
                imagePath = "images/StandingCalfRaise.gif";
                break;
            case "Seated Calf Raise":
                imagePath = "images/SeatedCalfRaise.gif";
                break;
            case "Leg Press Calf Raise":
                imagePath = "images/LegPressCalfRaise.gif";
                break;
            case "Smith Machine Calf Raise":
                imagePath = "images/SmithMachineCalfRaise.gif";
                break;
            case "Single-Leg Calf Raise":
                imagePath = "images/SingleLegCalfRaise.gif";
                break;
            // Add more cases as needed
        }

        ImageIcon imageIcon = new ImageIcon(imagePath);
        JLabel imageLabel = new JLabel(imageIcon);

        exerciseFrame.add(imageLabel);
        exerciseFrame.setVisible(true);
    }

    private JPanel createAdminPanel() {
        JPanel adminPanel = new JPanel();
        adminPanel.add(new JLabel("Admin Panel"));
        // Add admin functionalities here
        return adminPanel;
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean authenticateAdmin(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM admins WHERE username = ? AND password = ?")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean signUpUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
             PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                return false; // Username already exists
            }
            insertStatement.setString(1, username);
            insertStatement.setString(2, password);
            insertStatement.executeUpdate();
            return true; // Sign up successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
