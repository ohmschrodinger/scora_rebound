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
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JRadioButton studentRadio;
    private JRadioButton examinerRadio;
    private ButtonGroup roleGroup;
    private static AuthService authService;

    public LoginPage() {
        authService = new AuthService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Virtual Examination System - Login");
        setSize(900, 600);
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

        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Please enter your details");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subLabel.setForeground(Color.GRAY);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // UserID field with underline style
        JLabel userIdLabel = new JLabel("User ID");
        userIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userIdField = new JTextField(20);
        userIdField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        userIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        userIdField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password field with underline style
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField(20);
        passwordField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Radio buttons
        studentRadio = new JRadioButton("Student");
        examinerRadio = new JRadioButton("Examiner");
        studentRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        examinerRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentRadio.setBackground(Color.WHITE);
        examinerRadio.setBackground(Color.WHITE);
        
        roleGroup = new ButtonGroup();
        roleGroup.add(studentRadio);
        roleGroup.add(examinerRadio);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rolePanel.setBackground(Color.WHITE);
        rolePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rolePanel.add(studentRadio);
        rolePanel.add(Box.createHorizontalStrut(20));
        rolePanel.add(examinerRadio);

        // Login button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 1));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton loginButton = createStyledButton("Log In");
        loginButton.addActionListener(e -> loginUser());
        buttonPanel.add(loginButton);

        // Add components to left panel
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(subLabel);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(userIdLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(userIdField);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(passwordLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(passwordField);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(rolePanel);
        leftPanel.add(Box.createVerticalStrut(40));
        leftPanel.add(buttonPanel);
        leftPanel.add(Box.createVerticalGlue());

        // Right Panel (Image)
        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(234, 233, 232));
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/gui/Login.png"));
                    Image img = icon.getImage();
                    if (img != null) {
                        g.drawImage(img, getWidth() / 2 - img.getWidth(null) / 2,
                                getHeight() / 2 - img.getHeight(null) / 2, this);
                    }
                } catch (Exception e) {
                    // Handle image loading error silently
                }
            }
        };
        rightPanel.setPreferredSize(new Dimension(450, 600));

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(73, 125, 116));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loginUser() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userId.isEmpty() || password.isEmpty() || (!studentRadio.isSelected() && !examinerRadio.isSelected())) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select a role.");
            return;
        }

        User user = authService.login(userId, password);
        if (user != null) {
            // Check if the selected role matches the user's actual role
            boolean isStudent = user instanceof Student;
            boolean isExaminer = user instanceof Examiner;
            
            if (studentRadio.isSelected() && !isStudent) {
                JOptionPane.showMessageDialog(this, 
                    "You are not authorized to login as a student!", 
                    "Access Denied", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (examinerRadio.isSelected() && !isExaminer) {
                JOptionPane.showMessageDialog(this, 
                    "You are not authorized to login as an examiner!", 
                    "Access Denied", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // If role matches, proceed with login
            if (isStudent) {
                new StudentPortal(user.getUserId()).setVisible(true);
            } else {
                new ExaminerPortal(user.getUserId()).setVisible(true);
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
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