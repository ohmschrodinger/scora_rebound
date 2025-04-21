package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import db.DBConnection;
import model.User;
import services.ExamService;

public class StudentPortal extends JFrame {
    private String userId;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    public StudentPortal(String userId) {
        this.userId = userId;
        initializeUI();
        loadStudentData();
    }
    
    private void initializeUI() {
        setTitle("Student Portal - Online Examination System");
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
        cardPanel.add(createUpcomingExamsPanel(), "upcoming");
        cardPanel.add(createPastResultsPanel(), "results");
        cardPanel.add(createProfilePanel(), "profile");
        
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
            PreparedStatement ps = conn.prepareStatement("SELECT Name FROM user WHERE userID = ?");
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                JLabel welcomeLabel = new JLabel("Welcome, " + rs.getString("Name"));
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
        
        JButton upcomingExamsBtn = createNavButton("Upcoming Exams", "upcoming");
        JButton pastResultsBtn = createNavButton("Past Results", "results");
        JButton profileBtn = createNavButton("My Profile", "profile");
        
        navPanel.add(upcomingExamsBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(pastResultsBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(profileBtn);
        
        return navPanel;
    }
    
    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBackground(new Color(73, 125, 116));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> cardLayout.show(cardPanel, cardName));
        return button;
    }
    
    private JPanel createUpcomingExamsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Upcoming Exams");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table to display upcoming exams
        String[] columnNames = {"Exam ID", "Subject", "Total Marks", "Duration", "Date", "Time", "Action"};
        Object[][] data = getUpcomingExamsData();
        
        JTable examTable = new JTable(data, columnNames);
        examTable.setRowHeight(30);
        examTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(examTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Object[][] getUpcomingExamsData() {
        // This would query your database for upcoming exams
        // For now, returning sample data
        return new Object[][] {
            {"E001", "Data Structures", "8", "20 mins", "2025-04-15", "10:00 AM", "Start Exam"},
            {"E002", "Introduction to AI", "8", "20 mins", "2025-04-15", "11:00 AM", "Start Exam"}
        };
    }
    
    private JPanel createPastResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Past Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table to display past results
        String[] columnNames = {"Exam ID", "Subject", "Score", "Grade", "Accuracy", "Rank"};
        Object[][] data = getPastResultsData();
        
        JTable resultsTable = new JTable(data, columnNames);
        resultsTable.setRowHeight(30);
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Object[][] getPastResultsData() {
    try {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT r.ResultID, e.ExamID, e.Subject, r.Score, r.Grade, pr.Accuracy, pr.Ranks " +
                       "FROM Result r " +
                       "JOIN Exam e ON r.ExamID = e.ExamID " +
                       "JOIN PerformanceReport pr ON r.ResultID = pr.ResultID " +
                       "WHERE r.UserID = ?";
        
        PreparedStatement ps = conn.prepareStatement(
            query,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        );
        
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();
        
        // Count rows
        rs.last();
        int rowCount = rs.getRow();
        rs.beforeFirst();
        
        Object[][] data = new Object[rowCount][6];
        int i = 0;
        while (rs.next()) {
            data[i][0] = rs.getString("ExamID");
            data[i][1] = rs.getString("Subject");
            data[i][2] = rs.getString("Score");
            data[i][3] = rs.getString("Grade");
            data[i][4] = rs.getString("Accuracy");
            data[i][5] = rs.getString("Ranks");
            i++;
        }
        return data;
    } catch (Exception e) {
        e.printStackTrace();
        return new Object[0][0];
    }
}

    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel profilePanel = new JPanel(new GridLayout(5, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT u.Name, u.userID, s.Course, s.AcademicYear " +
                          "FROM user u JOIN student s ON u.userID = s.userID " +
                          "WHERE u.userID = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                addProfileField(profilePanel, "Student ID:", rs.getString("userID"));
                addProfileField(profilePanel, "Name:", rs.getString("Name"));
                addProfileField(profilePanel, "Course:", rs.getString("Course"));
                addProfileField(profilePanel, "Academic Year:", rs.getString("AcademicYear"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        panel.add(profilePanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void addProfileField(JPanel panel, String label, String value) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(fieldLabel);
        
        JLabel fieldValue = new JLabel(value);
        fieldValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(fieldValue);
    }
    
    private void loadStudentData() {
        // Additional initialization if needed
    }
}