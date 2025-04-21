package gui;
        
import javax.swing.*;
import java.awt.*;
import model.User;
import model.Student;
import model.Examiner;
import services.AuthService;
import javax.swing.border.MatteBorder;

import java.io.IOException;

public class LoginPage extends JFrame {
    private JTextField nameField;
    private JPasswordField passwordField;
    private JRadioButton studentRadio;
    private JRadioButton examinerRadio;
    private ButtonGroup roleGroup;

    private AuthService authService = new AuthService();

    public LoginPage() {
        setTitle("Virtual Examination System - Login");
        setSize(900, 600); // Increased height to ensure buttons fit
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Left Panel (Form)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
        leftPanel.setPreferredSize(new Dimension(450, 600));
        leftPanel.setBackground(Color.WHITE);

        // Sun icon
        JLabel title = new JLabel("☀️");
        title.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome again!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Please enter your details");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subLabel.setForeground(Color.GRAY);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Name field with underline style
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = new JTextField(20);
        nameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password field with underline style
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField(20);
        passwordField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Radio buttons styled as circles like in the reference image
        studentRadio = new JRadioButton("Student");
        examinerRadio = new JRadioButton("Examiner");
        studentRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        examinerRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentRadio.setBackground(Color.WHITE);
        examinerRadio.setBackground(Color.WHITE);
        
        // Use button group to maintain single selection
        roleGroup = new ButtonGroup();
        roleGroup.add(studentRadio);
        roleGroup.add(examinerRadio);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rolePanel.setBackground(Color.WHITE);
        rolePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rolePanel.add(studentRadio);
        rolePanel.add(Box.createHorizontalStrut(20));
        rolePanel.add(examinerRadio);

        // Fixed button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JButton loginButton = new JButton("Log In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(73, 125, 116));
        loginButton.setOpaque(true);
        loginButton.setContentAreaFilled(true);

        loginButton.setForeground(Color.BLACK);
        loginButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton signupButton = new JButton("Sign up");
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupButton.setBackground(new Color(73, 125, 116));
        
signupButton.setOpaque(true);
signupButton.setContentAreaFilled(true);
        signupButton.setForeground(Color.BLACK);
        signupButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        signupButton.setFocusPainted(false);
        signupButton.setBorderPainted(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addActionListener(e -> loginUser());
        signupButton.addActionListener(e -> signupUser());

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        // Add components to left panel with enough spacing
        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(subLabel);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(nameField);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(passwordLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(passwordField);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(rolePanel);
        leftPanel.add(Box.createVerticalStrut(40));
        leftPanel.add(buttonPanel);
        leftPanel.add(Box.createVerticalGlue()); // Push everything up

        // Right Panel (Image)
JPanel rightPanel = new JPanel() {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(234, 233, 232));

        // Load and display image
        ImageIcon icon = new ImageIcon(getClass().getResource("/gui/Login.png"));
        Image img = icon.getImage();
        if (img != null) {
            g.drawImage(img, getWidth() / 2 - img.getWidth(null) / 2,
                        getHeight() / 2 - img.getHeight(null) / 2, this);
        }
    }
};




        rightPanel.setPreferredSize(new Dimension(450, 600));

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private void loginUser() {
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        User user = authService.login(name, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            if (studentRadio.isSelected()) {
                new StudentPortal(user.getUserId()).setVisible(true);
            } else {
                new ExaminerPortal(user.getUserId()).setVisible(true);
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void signupUser() {
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || password.isEmpty() || 
        (!studentRadio.isSelected() && !examinerRadio.isSelected()) || 
        name.matches(".*\\d.*")) {

            JOptionPane.showMessageDialog(this, "Please fill all fields, select a role, and make sure the name doesn't contain numbers.");
            return;
        }
        //ADD NUMBER ERROR HANDLING
        if (studentRadio.isSelected()) {
            String course = JOptionPane.showInputDialog(this, "Enter Course:");
            String yearStr = JOptionPane.showInputDialog(this, "Enter Academic Year:");
            try {
                int year = Integer.parseInt(yearStr);
                Student student = new Student(null, name, password, course, year);
                Student registered = authService.registerStudent(student);
                if (registered != null) {
                    JOptionPane.showMessageDialog(this, "Student registered! Your ID: " + registered.getUserId());
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Academic year must be a number.");
            }
        } else if (examinerRadio.isSelected()) {
            String dept = JOptionPane.showInputDialog(this, "Enter Department:");
            Examiner examiner = new Examiner(null, name, password, dept);
            Examiner registered = authService.registerExaminer(examiner);
            if (registered != null) {
                JOptionPane.showMessageDialog(this, "Examiner registered! Your ID: " + registered.getUserId());
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    } 
}