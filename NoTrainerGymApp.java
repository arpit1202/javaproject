import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;

public class NoTrainerGymApp {
    // MySQL connection variables
    static final String DB_URL = "jdbc:mysql://localhost:3306/notrainer_gym";
    static final String DB_USER = "root"; // Your MySQL username
    static final String DB_PASS = "root"; // Your MySQL password

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
        exerciseSubLists.put("Chest",
                new String[] { "Bench Press", "Incline Dumbbell Press", "Cable Crossover", "Chest Dip", "Pec Deck" });
        exerciseSubLists.put("Shoulder",
                new String[] { "Shoulder Press", "Lateral Raise", "Front Raise", "Reverse Fly", "Arnold Press" });
        exerciseSubLists.put("Leg",
                new String[] { "Squat", "Leg Press", "Leg Extension", "Hamstring Curl", "Calf Raise" });
        exerciseSubLists.put("Shrugs", new String[] { "Barbell Shrug", "Dumbbell Shrug", "Cable Shrug",
                "Behind-the-Back Shrug", "Incline Shrug" });
        exerciseSubLists.put("Biceps",
                new String[] { "Bicep Curl", "Hammer Curl", "Preacher Curl", "Concentration Curl", "Cable Curl" });
        exerciseSubLists.put("Triceps", new String[] { "Tricep Extension", "Skull Crusher", "Tricep Dip",
                "Close Grip Bench Press", "Cable Pushdown" });
        exerciseSubLists.put("Calf", new String[] { "Standing Calf Raise", "Seated Calf Raise", "Leg Press Calf Raise",
                "Smith Machine Calf Raise", "Single-Leg Calf Raise" });
    }

    public void createAndShowGUI() {
        frame = new JFrame("NoTrainer Gym");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);

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
        // Create a JLayeredPane to hold both the background image and the components
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));

        // Load the background image
        ImageIcon backgroundIcon = new ImageIcon("images/SoloFit.png");
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, 800, 600); // Initial bounds

        // Create a panel for the form elements
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Make the panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        // Form components
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JTextField userField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");
        JLabel messageLabel = new JLabel();

        // seting color
        userLabel.setForeground(Color.WHITE);
        passLabel.setForeground(Color.WHITE);
        messageLabel.setForeground(Color.WHITE);

        // size of button
        Dimension buttonSize = new Dimension(90, 30); // Adjust size as needed
        loginButton.setPreferredSize(buttonSize);
        signUpButton.setPreferredSize(buttonSize);

        // Adding components to the formPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passField, gbc);

        gbc.gridx = 1;
        gbc.gridy = -2;
        formPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = -4;
        formPanel.add(signUpButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(messageLabel, gbc);

        // Add components to the layered pane
        layeredPane.add(backgroundLabel, Integer.valueOf(0));
        layeredPane.add(formPanel, Integer.valueOf(1));

        // Add a ComponentListener to handle resizing
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = layeredPane.getSize();
                Image scaledImage = backgroundIcon.getImage().getScaledInstance(size.width, size.height,
                        Image.SCALE_SMOOTH);
                backgroundLabel.setIcon(new ImageIcon(scaledImage));
                backgroundLabel.setSize(size);

                formPanel.setSize(size);
                int formWidth = 400; // Width of form components
                int formHeight = formPanel.getPreferredSize().height; // Dynamic height based on content
                int formX = (size.width - formWidth) / 2;
                int formY = (int) (size.height * 0.7); // Position form elements near the bottom

                formPanel.setBounds(formX, formY, formWidth, formHeight);
            }
        });

        // Create a panel to hold the layered pane
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.add(layeredPane, BorderLayout.CENTER);

        // Action listeners
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

        return backgroundPanel;
    }

    private JPanel createUserPanel() {
        // Create a JLayeredPane to hold both the background image and the components
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));

        // Load the background image
        ImageIcon backgroundIcon = new ImageIcon("images/well.jpg");
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, 800, 600); // Initial bounds

        // Create a panel for the content elements
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false); // Make the panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Content components
        JLabel welcomeLabel = new JLabel("Welcome To GYM", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 40)); // Bigger letters
        welcomeLabel.setForeground(Color.WHITE);

        JButton beginnerButton = new JButton("Beginner");
        JButton trainedButton = new JButton("Trained");

        // changing button text size
        Font buttonFont = new Font("Serif", Font.BOLD, 24);
        beginnerButton.setFont(buttonFont);
        trainedButton.setFont(buttonFont);

        beginnerButton.setPreferredSize(new Dimension(200, 90)); // Increase button size
        trainedButton.setPreferredSize(new Dimension(200, 90)); // Increase button size

        // Action listeners for buttons
        beginnerButton.addActionListener(e -> showExercisesPanel("Beginner Exercises"));
        trainedButton.addActionListener(e -> showExercisesPanel("Trained Exercises"));

        // CHANGING COLOR TO GOLDEN
        Color goldenColor = new Color(255, 215, 0); // Golden color
        beginnerButton.setBackground(goldenColor);
        trainedButton.setBackground(goldenColor);
        beginnerButton.setOpaque(true); // Make sure the background is visible
        trainedButton.setOpaque(true);

        Color textColor = Color.BLACK;
        beginnerButton.setForeground(textColor);
        trainedButton.setForeground(textColor);

        // Adding components to the contentPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(welcomeLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        contentPanel.add(beginnerButton, gbc);

        gbc.gridx = 1;
        contentPanel.add(trainedButton, gbc);

        // Add components to the layered pane
        layeredPane.add(backgroundLabel, Integer.valueOf(0));
        layeredPane.add(contentPanel, Integer.valueOf(1));

        // Add a ComponentListener to handle resizing
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = layeredPane.getSize();
                Image scaledImage = backgroundIcon.getImage().getScaledInstance(size.width, size.height,
                        Image.SCALE_SMOOTH);
                backgroundLabel.setIcon(new ImageIcon(scaledImage));
                backgroundLabel.setSize(size);

                contentPanel.setSize(size);
                int contentWidth = 600; // Width of content components
                int contentHeight = contentPanel.getPreferredSize().height; // Dynamic height based on content
                int contentX = (size.width - contentWidth) / 2;
                int contentY = (size.height - contentHeight) / 2;

                contentPanel.setBounds(contentX, contentY, contentWidth, contentHeight);
            }
        });

        // Create a panel to hold the layered pane
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.add(layeredPane, BorderLayout.CENTER);

        return userPanel;
    }

    private void showExercisesPanel(String title) {
        JFrame exercisesFrame = new JFrame(title);
        exercisesFrame.setSize(800, 600);
        exercisesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        exercisesFrame.setLayout(new BorderLayout());
        exercisesFrame.setLocationRelativeTo(null); // Center the frame
        exercisesFrame.setResizable(false);

        JPanel exercisesPanel = createExercisesPanel();

        // Set background color of the exercisesPanel to gold
        Color goldenColor = new Color(255, 215, 0);
        // exercisesPanel.setBackground(goldenColor);

        // Add back button to close the exercises frame and return to the user panel
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> exercisesFrame.dispose());

        // Set color of back button
        Color buttonTextColor = Color.BLACK;
        backButton.setForeground(goldenColor);
        backButton.setOpaque(true); // Make sure the background is visible
        backButton.setBackground(buttonTextColor);

        exercisesFrame.add(exercisesPanel, BorderLayout.CENTER);
        exercisesFrame.add(backButton, BorderLayout.SOUTH);
        exercisesFrame.setVisible(true);
    }

    private JPanel createExercisesPanel() {
        JPanel exercisesPanel = new JPanel(new GridLayout(3, 2));
        String[] exercises = { "Chest", "Shoulder", "Biceps", "Triceps", "Leg", "Calf", "Shrugs" };

        for (String exercise : exercises) {
            // Create a panel for each button and label
            JPanel buttonPanel = new JPanel(new BorderLayout());

            JButton exerciseButton = new JButton();
            exerciseButton.setPreferredSize(new Dimension(200, 90)); // Set the size of the button

            // Determine the image path based on the exercise
            String imagePath = "";
            switch (exercise) {
                case "Chest":
                    imagePath = "images/Chest.jpg";
                    break;
                case "Shoulder":
                    imagePath = "images/Shoulder.jpg";
                    break;
                case "Biceps":
                    imagePath = "images/Biceps.jpg";
                    break;
                case "Triceps":
                    imagePath = "images/Triceps.jpg";
                    break;
                case "Leg":
                    imagePath = "images/Leg.jpg";
                    break;
                case "Calf":
                    imagePath = "images/Calf.jpg";
                    break;
                case "Shrugs":
                    imagePath = "images/Shrugs.jpg";
                    break;
            }

            // Set the button's background image
            ImageIcon imageIcon = new ImageIcon(imagePath);
            Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            exerciseButton.setIcon(new ImageIcon(image));

            // Set button properties
            exerciseButton.setBorderPainted(false); // Remove button border
            exerciseButton.setContentAreaFilled(false); // Remove default button background
            exerciseButton.setFocusPainted(false); // Remove focus border

            // Create a label for the exercise name
            JLabel exerciseLabel = new JLabel(exercise, SwingConstants.CENTER);
            exerciseLabel.setPreferredSize(new Dimension(200, 25)); // Adjust size as needed
            exerciseLabel.setFont(new Font("Serif", Font.BOLD, 20));
            exerciseLabel.setForeground(Color.BLACK); // Set text color (optional)

            // Add button and label to the buttonPanel
            buttonPanel.add(exerciseButton, BorderLayout.CENTER);
            buttonPanel.add(exerciseLabel, BorderLayout.SOUTH);

            // Add action listener for button
            exerciseButton.addActionListener(e -> showExerciseSubList(exercise));

            exercisesPanel.add(buttonPanel);
        }

        return exercisesPanel;
    }

    // Utility method to get a rounded image
    private Image getRoundedImage(Image image, int radius) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();

        // Anti-aliasing for smoother edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the rounded rectangle
        g2.setClip(
                new RoundRectangle2D.Double(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), radius, radius));
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return bufferedImage;
    }

    private void showExerciseSubList(String exercise) {
        JFrame subListFrame = new JFrame(exercise + " Exercises");
        subListFrame.setSize(800, 600);
        subListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        subListFrame.setLayout(new GridLayout(5, 1));
        subListFrame.setLocationRelativeTo(null); // Center the frame
        subListFrame.setResizable(false);

        // Get the sublist items for the selected exercise
        String[] subListItems = exerciseSubLists.getOrDefault(exercise, new String[] {});

        for (String item : subListItems) {
            JButton itemButton = new JButton(item);
            itemButton.addActionListener(e -> showExerciseDetails(item));
            subListFrame.add(itemButton);

            // making button item of list of exercise big
            Font buttonFont = new Font("Serif", Font.BOLD, 24);
            itemButton.setFont(buttonFont);

            // CHANGING COLOR TO GOLDEN
            // Golden color is initialized and declared in line 402.
            Color goldenColor = new Color(255, 215, 0); // Golden color
            itemButton.setBackground(goldenColor);
            // Make sure the background is visible
            itemButton.setOpaque(true);

            // black color also same in lin 404.
            Color textColor = Color.BLACK;
            itemButton.setForeground(textColor);

        }

        subListFrame.setVisible(true);
    }

    private void showExerciseDetails(String exercise) {
        JFrame exerciseFrame = new JFrame(exercise);
        exerciseFrame.setSize(800, 600);
        exerciseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        exerciseFrame.setLocationRelativeTo(null); // Center the frame
        exerciseFrame.setResizable(false);

        // Create the Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> exerciseFrame.dispose());

        Color goldenColor = new Color(255, 215, 0); // Golden color
        backButton.setForeground(goldenColor);
        backButton.setOpaque(true); // Make sure the background is visible

        Color textColor = Color.BLACK;
        backButton.setBackground(textColor);

        // Set up the layout
        exerciseFrame.setLayout(new BorderLayout());

        String imagePath = "images/default_image.gif"; // Default path
        String exerciseDescription = "Description not available."; // Default description

        // Special cases for exercises under various categories
        switch (exercise) {
            case "Bench Press":
                imagePath = "images/BenchPress.gif";
                exerciseDescription = "\t   Bench Press\n• targets the pectoral muscles, shoulders, and triceps. \n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Incline Dumbbell Press":
                imagePath = "images/InclineDumbellPress.gif";
                exerciseDescription = "\t   Incline Dumbell Press\n• Focuses on the upper chest and front shoulder muscles. \n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Cable Crossover":
                imagePath = "images/CableCrossOver.gif";
                exerciseDescription = "\t   Cable Crossover\n• Works the chest muscles through a full range of motion.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Chest Dip":
                imagePath = "images/ChestDips.gif";
                exerciseDescription = "\t   Chest Dip\n• Targets the lower chest and triceps.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Pec Deck":
                imagePath = "images/PecDeck.gif";
                exerciseDescription = "\t   Pec Deck\n• Isolates the chest muscles, specifically the pectoralis major.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Shoulder Press":
                imagePath = "images/ShoulderPress.gif";
                exerciseDescription = "\t   Shoulder Press\n• Strengthens the deltoid muscles and improves shoulder stability.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Lateral Raise":
                imagePath = "images/LateralRaise.gif";
                exerciseDescription = "\t   Lateral Raise\n• Targets the middle deltoid, helping to build shoulder width.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Front Raise":
                imagePath = "images/FrontRaise.gif";
                exerciseDescription = "\t   Front Raise\n• Focuses on the anterior deltoid, enhancing shoulder shape.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Reverse Fly":
                imagePath = "images/ReverseFly.gif";
                exerciseDescription = "\t   Reverse Fly\n• Works the rear deltoids and upper back muscles.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Arnold Press":
                imagePath = "images/ArnoldPress.gif";
                exerciseDescription = "\t   Arnold Press\n• Combines a shoulder press with a rotational movement for full shoulder engagement.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Squat":
                imagePath = "images/Squat.gif";
                exerciseDescription = "\t   Squat\n• A fundamental lower body exercise targeting quadriceps, hamstrings, and glutes.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Leg Press":
                imagePath = "images/LegPress.gif";
                exerciseDescription = "\t   Leg Press\n• An exercise for leg development, focusing on quads, glutes, and hamstrings.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Leg Extension":
                imagePath = "images/LegExtension.gif";
                exerciseDescription = "\t   Leg Extension\n• Isolates the quadriceps for targeted muscle development.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Hamstring Curl":
                imagePath = "images/HamstringCurl.gif";
                exerciseDescription = "\t   Hamstring Curl\n• Strengthens the hamstrings, crucial for knee stability.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Calf Raise":
                imagePath = "images/CalfRaise.gif";
                exerciseDescription = "\t   Calf Raise\n• Develops the calf muscles, enhancing lower leg strength.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Barbell Shrug":
                imagePath = "images/BarbellShrug.gif";
                exerciseDescription = "\t   Barbell Shrug\n• Targets the trapezius muscles, improving neck and upper back strength.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Dumbbell Shrug":
                imagePath = "images/DumbbellShrug.gif";
                exerciseDescription = "\t   Dumbbell Shrug\n• Similar to barbell shrugs, focusing on trap development.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Cable Shrug":
                imagePath = "images/CableShrug.gif";
                exerciseDescription = "\t   Cable Shrug\n• Allows for controlled shrug movements, targeting the trapezius.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Behind-the-Back Shrug":
                imagePath = "images/BehindTheBack.gif";
                exerciseDescription = "\t   Behind-the-Back Shrug\n• Targets the traps from a different angle for balanced development.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Incline Shrug":
                imagePath = "images/InclineShrug.gif";
                exerciseDescription = "\t   Incline Shrug\n• Engages the traps and shoulders using a neutral grip.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Bicep Curl":
                imagePath = "images/BicepCurl.gif";
                exerciseDescription = "\t   Bicep Curl\n• Isolates the biceps, enhancing arm strength and size.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Hammer Curl":
                imagePath = "images/HammerCurl.gif";
                exerciseDescription = "\t   Hammer Curl\n• Targets the brachialis and forearm muscles for balanced arm development.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Preacher Curl":
                imagePath = "images/PreacherCurl.gif";
                exerciseDescription = "\t   Preacher Curl\n• Provides isolation for the biceps, minimizing shoulder involvement.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Concentration Curl":
                imagePath = "images/ConcentrationCurl.gif";
                exerciseDescription = "\t   Concentration Curl\n• Focuses on peak contraction of the biceps for enhanced definition.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Cable Curl":
                imagePath = "images/CableCurl.gif";
                exerciseDescription = "\t   Cable Curl\n• Provides constant tension on the biceps throughout the movement.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Tricep Extension":
                imagePath = "images/TricepExtension.gif";
                exerciseDescription = "\t   Tricep Extension\n• Targets the triceps for improved arm size and strength.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Skull Crusher":
                imagePath = "images/SkullCrusher.gif";
                exerciseDescription = "\t   Skull Crusher\n• Isolates the triceps, focusing on the long head.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Tricep Dip":
                imagePath = "images/TricepDip.gif";
                exerciseDescription = "\t   Tricep Dip\n• A bodyweight exercise targeting the triceps and chest.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Close Grip Bench Press":
                imagePath = "images/CloseGripDumbellPress.gif";
                exerciseDescription = "\t   Close Grip Bench Press\n• Emphasizes the triceps while also working the chest.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Cable Pushdown":
                imagePath = "images/CablePushdown.gif";
                exerciseDescription = "\t   Cable Pushdown\n• A tricep exercise using a cable machine for controlled movements.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Standing Calf Raise":
                imagePath = "images/StandingCalfRaise.gif";
                exerciseDescription = "\t   Standing Calf Raise\n• Develops the gastrocnemius muscle, enhancing calf size.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Seated Calf Raise":
                imagePath = "images/SeatedCalfRaise.gif";
                exerciseDescription = "\t   Seated Calf Raise\n• Targets the soleus muscle for balanced calf development.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Leg Press Calf Raise":
                imagePath = "images/LegPressCalfRaise.gif";
                exerciseDescription = "\t   Leg Press Calf Raise\n• Combines leg press and calf raise for a compound lower body workout.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Smith Machine Calf Raise":
                imagePath = "images/SmithMachineCalfRaise.gif";
                exerciseDescription = "\t   Smith Machine Calf Raise\n• Uses the Smith machine for controlled calf exercises.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
            case "Single-Leg Calf Raise":
                imagePath = "images/SingleLegCalfRaise.gif";
                exerciseDescription = "\t   Single-Leg Calf Raise\n• Isolates each calf muscle for balanced strength and development.\n• Do : 3 sets | 8-10 reps | 1-2min rest per set ";
                break;
        }

        ImageIcon imageIcon = new ImageIcon(imagePath);
        JLabel imageLabel = new JLabel(imageIcon);

        // Create a text area for the exercise description
        JTextArea descriptionArea = new JTextArea(exerciseDescription);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setBackground(UIManager.getColor("Label.background"));
        descriptionArea.setFont(new Font("Serif", Font.PLAIN, 16));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding

        // Create a panel to hold the image and description side by side
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Scroll pane for image
        JScrollPane imageScrollPane = new JScrollPane(imageLabel);
        imageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        imageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Scroll pane for description
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(imageScrollPane);
        contentPanel.add(descriptionScrollPane);

        // Add components to the frame
        exerciseFrame.add(contentPanel, BorderLayout.CENTER);
        exerciseFrame.add(backButton, BorderLayout.SOUTH);

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
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
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
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT * FROM admins WHERE username = ? AND password = ?")) {
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
