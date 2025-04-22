package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import db.DBConnection;
import model.User;
import services.ExamService;
import model.Exam;
import model.Question;
import model.Option;
import java.util.List;
import java.util.ArrayList;



public class ExaminerPortal extends JFrame {
    private String userId;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    public ExaminerPortal(String userId) {
        this.userId = userId;
        initializeUI();
//        loadExaminerData();
    }
    
    private void initializeUI() {
        setTitle("Examiner Portal - Online Examination System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Navigation Panel
        JPanel navPanel = createNavigationPanel();
        
        // Content Panel (CardLayout for switching views)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createDashboardPanel(), "dashboard");
        cardPanel.add(createExamsPanel(), "exams");
        cardPanel.add(createQuestionsPanel(), "questions");
        cardPanel.add(createResultsPanel(), "results");
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(73, 125, 116));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT u.Name, e.Department FROM user u JOIN examiner e ON u.userID = e.userID WHERE u.userID = ?");
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                JLabel welcomeLabel = new JLabel("Welcome, " + rs.getString("Name") + " (" + rs.getString("Department") + ")");
                welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                welcomeLabel.setForeground(Color.WHITE);
                headerPanel.add(welcomeLabel, BorderLayout.WEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(new Color(73, 125, 116));
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(234, 233, 232));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JButton dashboardBtn = createNavButton("Dashboard", "dashboard");
        JButton examsBtn = createNavButton("Manage Exams", "exams");
        JButton questionsBtn = createNavButton("Manage Questions", "questions");
        JButton resultsBtn = createNavButton("View Results", "results");
        
        navPanel.add(dashboardBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(examsBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(questionsBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(resultsBtn);
        
        return navPanel;
    }
    
    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBackground(new Color(73, 125, 116));
         button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.addActionListener(e -> cardLayout.show(cardPanel, cardName));
        return button;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Examiner Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        try {
            Connection conn = DBConnection.getConnection();
            
            // Total exams created
            PreparedStatement ps1 = conn.prepareStatement(
                "SELECT COUNT(*) AS total FROM Exam WHERE UserID = ?");
            ps1.setString(1, userId);
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();
            addStatCard(statsPanel, "Total Exams", rs1.getString("total"), new Color(100, 149, 237));
            
            // Total questions added
            PreparedStatement ps2 = conn.prepareStatement(
                "SELECT COUNT(*) AS total FROM manages WHERE UserID = ?");
            ps2.setString(1, userId);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            addStatCard(statsPanel, "Total Questions", rs2.getString("total"), new Color(60, 179, 113));
            
            // Active exams
            PreparedStatement ps3 = conn.prepareStatement(
                "SELECT COUNT(*) AS total FROM Exam WHERE UserID = ? AND Status = 'Active'");
            ps3.setString(1, userId);
            ResultSet rs3 = ps3.executeQuery();
            rs3.next();
            addStatCard(statsPanel, "Active Exams", rs3.getString("total"), new Color(238, 130, 238));
            
            // Students attempted
            PreparedStatement ps4 = conn.prepareStatement(
                "SELECT COUNT(DISTINCT a.UserID) AS total FROM Attempt a " +
                "JOIN Exam e ON a.ExamID = e.ExamID WHERE e.UserID = ?");
            ps4.setString(1, userId);
            ResultSet rs4 = ps4.executeQuery();
            rs4.next();
            addStatCard(statsPanel, "Students Attempted", rs4.getString("total"), new Color(255, 165, 0));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void addStatCard(JPanel panel, String title, String value, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);
        
        panel.add(card);
    }
    
    private JPanel createExamsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Manage Exams");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Toolbar with buttons
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton createExamBtn = new JButton("Create New Exam");
        createExamBtn.addActionListener(e -> showCreateExamDialog());
        
       
        
        toolbar.add(createExamBtn);
       
        panel.add(toolbar, BorderLayout.CENTER);
        
        // Table to display exams
        String[] columnNames = {"Exam ID", "Subject", "Total Marks", "Duration", "Actions"};
        Object[][] data = getExamsData();
        
        JTable examTable = new JTable(data, columnNames);
        examTable.setRowHeight(30);
        examTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(examTable);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
private Object[][] getExamsData() {
    try {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT ExamID, Subject, TotalMarks, Duration, Status FROM Exam WHERE UserID = ?");
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();

        // Use a dynamic list to collect rows
        java.util.List<Object[]> rows = new java.util.ArrayList<>();

        while (rs.next()) {
            Object[] row = new Object[6];
            row[0] = rs.getString("ExamID");
            row[1] = rs.getString("Subject");
            row[2] = rs.getString("TotalMarks");
            row[3] = rs.getString("Duration");
            String ex= rs.getString("Status");
            row[4] = "done";
            rows.add(row);
        }

        return rows.toArray(new Object[0][0]);
    } catch (Exception e) {
        e.printStackTrace();
        return new Object[0][0];
    }
}

    
    private JPanel createQuestionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Manage Questions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Toolbar with buttons
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addQuestionBtn = new JButton("Add New Question");
        addQuestionBtn.addActionListener(e -> showAddQuestionDialog());
        
        toolbar.add(addQuestionBtn);
        panel.add(toolbar, BorderLayout.CENTER);
        
        // Table to display questions
        String[] columnNames = {"Question ID", "Category", "Level", "Text", "Marks", "Actions"};
        Object[][] data = getQuestionsData();
        
        JTable questionTable = new JTable(data, columnNames);
        questionTable.setRowHeight(30);
        questionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(questionTable);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private Object[][] getQuestionsData() {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT q.QuestionID, q.Category, q.Level, q.Text, q.Marks " +
                "FROM question q JOIN manages m ON q.QuestionID = m.QuestionID " +
                "WHERE m.UserID = ?",   ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            // Count rows
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
            
            Object[][] data = new Object[rowCount][6];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getString("QuestionID");
                data[i][1] = rs.getString("Category");
                data[i][2] = rs.getString("Level");
                data[i][3] = rs.getString("Text");
                data[i][4] = rs.getString("Marks");
                data[i][5] = "Edit | Delete";
                i++;
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0][0];
        }
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Exam Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table to display results
        String[] columnNames = {"Exam ID", "Subject", "Student Name", "Score", "Grade", "Accuracy", "Rank"};
        Object[][] data = getExamResultsData();
        
        JTable resultsTable = new JTable(data, columnNames);
        resultsTable.setRowHeight(30);
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Object[][] getExamResultsData() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT e.ExamID, e.Subject, u.Name AS StudentName, " +
                          "r.Score, r.Grade, pr.Accuracy, pr.Ranks " +
                          "FROM Result r " +
                          "JOIN Exam e ON r.ExamID = e.ExamID " +
                          "JOIN user u ON r.UserID = u.userID " +
                          "JOIN PerformanceReport pr ON r.ResultID = pr.ResultID " +
                          "WHERE e.UserID = ?";
            PreparedStatement ps = conn.prepareStatement(query,  ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            // Count rows
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
            
            Object[][] data = new Object[rowCount][7];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getString("ExamID");
                data[i][1] = rs.getString("Subject");
                data[i][2] = rs.getString("StudentName");
                data[i][3] = rs.getString("Score");
                data[i][4] = rs.getString("Grade");
                data[i][5] = rs.getString("Accuracy");
                data[i][6] = rs.getString("Ranks");
                i++;
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0][0];
        }
    }
    
    private void showCreateExamDialog() {
        JDialog dialog = new JDialog(this, "Create New Exam", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel subjectLabel = new JLabel("Subject:");
        JTextField subjectField = new JTextField();
        
        JLabel marksLabel = new JLabel("Total Marks:");
        JTextField marksField = new JTextField();
        
        JLabel durationLabel = new JLabel("Duration (mins):");
        JTextField durationField = new JTextField();
        
        JButton createBtn = new JButton("Create");
        createBtn.addActionListener(e -> {
    String subject = subjectField.getText().trim();
    String marksText = marksField.getText().trim();
    String durationText = durationField.getText().trim();

    if (subject.isEmpty() || marksText.isEmpty() || durationText.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        int totalMarks = Integer.parseInt(marksText);
        int duration = Integer.parseInt(durationText);
        
        // Create exam using your required constructor
        Exam exam = new Exam(
            "TEMP",                            // temporary dummy ExamID
            totalMarks,
            duration + " mins",               // assuming duration column is varchar
            subject,
            "Scheduled",                      // or any default status you prefer
            userId
        );

        ExamService examService = new ExamService();
        boolean success = examService.createExam(exam);

        if (success) {
            JOptionPane.showMessageDialog(dialog, "Exam created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            // Optional: refresh exam list/table here
        } else {
            JOptionPane.showMessageDialog(dialog, "Failed to create exam!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(dialog, "Enter valid numeric values for marks and duration.", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(dialog, "Error creating exam: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
});



        
        panel.add(subjectLabel);
        panel.add(subjectField);
        panel.add(marksLabel);
        panel.add(marksField);
        panel.add(durationLabel);
        panel.add(durationField);
        panel.add(new JLabel());
        panel.add(createBtn);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    private void showAddQuestionDialog() {
    ExamService examService = new ExamService();
    JDialog dialog = new JDialog(this, "Add New Question", true);
    dialog.setSize(700, 700);
    dialog.setLocationRelativeTo(this);
    
    // Main container with border layout
    JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // North panel for header
    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel headerLabel = new JLabel("Add New Question");
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    headerPanel.add(headerLabel);
    
    // Center panel with form fields
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    
    // Basic information section
    JPanel basicInfoPanel = new JPanel(new GridLayout(3, 4, 10, 10));
    basicInfoPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "Basic Information"));
    
    basicInfoPanel.add(new JLabel("Exam ID:"));
    JTextField examIdField = new JTextField();
    basicInfoPanel.add(examIdField);
    
    basicInfoPanel.add(new JLabel("Category:"));
    JTextField categoryField = new JTextField();
    basicInfoPanel.add(categoryField);
    
    basicInfoPanel.add(new JLabel("Level:"));
    JTextField levelField = new JTextField();
    basicInfoPanel.add(levelField);
    
    basicInfoPanel.add(new JLabel("Marks:"));
    JTextField marksField = new JTextField();
    basicInfoPanel.add(marksField);
    
    // Question content section
    JPanel questionPanel = new JPanel(new GridLayout(3, 2, 10, 10));
    questionPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "Question Content"));
    
    questionPanel.add(new JLabel("MCQ:"));
    JTextField mcqField = new JTextField();
    questionPanel.add(mcqField);
    
    questionPanel.add(new JLabel("FIB:"));
    JTextField fibField = new JTextField();
    questionPanel.add(fibField);
    
    questionPanel.add(new JLabel("Question Text:"));
    JTextField textField = new JTextField();
    questionPanel.add(textField);
    
    // Options section 
    JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
    optionsPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "Answer Options"));
    
    JTextField[] optionFields = new JTextField[4];
    JRadioButton[] correctButtons = new JRadioButton[4];
    ButtonGroup group = new ButtonGroup();
    
    for (int i = 0; i < 4; i++) {
        JPanel optRow = new JPanel(new BorderLayout(10, 0));
        JLabel optLabel = new JLabel("Option " + (i + 1) + ":");
        optLabel.setPreferredSize(new Dimension(80, 25));
        
        optionFields[i] = new JTextField();
        correctButtons[i] = new JRadioButton("Correct");
        correctButtons[i].setBackground(Color.WHITE);
        group.add(correctButtons[i]);
        
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(optionFields[i], BorderLayout.CENTER);
        innerPanel.add(correctButtons[i], BorderLayout.EAST);
        
        optRow.add(optLabel, BorderLayout.WEST);
        optRow.add(innerPanel, BorderLayout.CENTER);
        optionsPanel.add(optRow);
    }
    
    // Add panels to form
    formPanel.add(basicInfoPanel);
    formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    formPanel.add(questionPanel);
    formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    formPanel.add(optionsPanel);
    
    // South panel for buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());
    
    JButton addButton = new JButton("Add Question");
    addButton.setBackground(new Color(34, 139, 34)); // Forest green
    addButton.setForeground(Color.WHITE);
    addButton.setFont(new Font("Arial", Font.BOLD, 14));
    
    addButton.addActionListener(e -> {
        try {
            String examId = examIdField.getText().trim();
            String category = categoryField.getText().trim();
            String level = levelField.getText().trim();
            String mcq = mcqField.getText().trim();
            String fib = fibField.getText().trim();
            String text = textField.getText().trim();
            int marks = Integer.parseInt(marksField.getText().trim());
            
            if (examId.isEmpty() || category.isEmpty() || level.isEmpty() || text.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Exam ID validity check
            if (!examService.examExists(examId)) {
                JOptionPane.showMessageDialog(dialog, "Invalid Exam ID. Please check again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
String tempQuestionId = "TEMP";  // Temporary ID for now
Question question = new Question(tempQuestionId, category, level, mcq, fib, text, marks);

List<Option> options = new ArrayList<>();
boolean correctSelected = false;

for (int i = 0; i < 4; i++) {
    String optionText = optionFields[i].getText().trim();
    if (optionText.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "All options must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    char isCorrect = correctButtons[i].isSelected() ? 'Y' : 'N';
    if (isCorrect == 'Y') correctSelected = true;

    // TEMP OptionID; QuestionID will be replaced in backend anyway
    Option option = new Option("TEMP_O" + (i + 1), optionText, isCorrect, tempQuestionId);
    options.add(option);
}

            
            if (!correctSelected) {
                JOptionPane.showMessageDialog(dialog, "Please select one correct option.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = examService.addQuestionAndOptions(examId, question, options,userId);
            
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Question added successfully.");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add question.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Invalid input! Make sure all fields are filled properly.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    
    buttonPanel.add(cancelButton);
    buttonPanel.add(addButton);
    
    // Add all components to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    dialog.add(mainPanel);
    dialog.setVisible(true);
}

    
//    private void loadExaminerData() {
//        // Additional initialization if needed
//    }
//    
    
}