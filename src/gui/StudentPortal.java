package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import db.DBConnection;
import model.User;
import services.ExamService;

/**
 * StudentPortal class represents the main interface for student users. It
 * provides access to upcoming exams, past results, and profile information.
 */
public class StudentPortal extends JFrame {

    private String userId;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    /**
     * Constructor for StudentPortal
     *
     * @param userId The ID of the student user
     */
    public StudentPortal(String userId) {
        this.userId = userId;
        initializeUI();
        loadStudentData();
        
        setVisible(true);
    }

    /**
     * Initializes the user interface components Sets up the main layout with
     * header, navigation, and content panels
     */
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

    /**
     * Creates the header panel with welcome message and logout button
     *
     * @return JPanel containing the header elements
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(73, 125, 116));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        try {
            // Get the user's name from the database to display welcome message
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

            // Close resources
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            // When logout is clicked, return to login page and dispose this window
            new LoginPage().setVisible(true);
            dispose();
        });
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(new Color(73, 125, 116));
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Creates the navigation panel with buttons for switching between views
     *
     * @return JPanel containing navigation buttons
     */
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(234, 233, 232));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Create buttons for each section
        JButton upcomingExamsBtn = createNavButton("Upcoming Exams", "upcoming");
        JButton pastResultsBtn = createNavButton("Past Results", "results");
        JButton profileBtn = createNavButton("My Profile", "profile");

        // Add buttons to navigation panel with spacing
        navPanel.add(upcomingExamsBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(pastResultsBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(profileBtn);

        return navPanel;
    }

    /**
     * Helper method to create uniform navigation buttons
     *
     * @param text Label text for the button
     * @param cardName The card name for the CardLayout
     * @return Configured JButton
     */
    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBackground(new Color(73, 125, 116));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        // When clicked, show the corresponding card in the card layout
        button.addActionListener(e -> cardLayout.show(cardPanel, cardName));
        return button;
    }

    /**
     * Creates the panel showing upcoming exams
     *
     * @return JPanel with upcoming exams data
     */
    private JPanel createUpcomingExamsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Eligible Exams");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table to display eligible exams
        String[] columnNames = {"Exam ID", "Subject", "Total Marks", "Duration", "Status", "Action"};

        // Get eligible exam data from database for the logged-in student
        Object[][] data = getEligibleExamsData();

        // Create table model that makes only the Action column editable
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only the Action column editable (for the button)
                return column == 5;
            }
        };

        JTable examTable = new JTable(model);
        examTable.setRowHeight(30);
        examTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Set up the button column with custom renderer and editor
        examTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        examTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(examTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Retrieves eligible exams data from the database for the logged-in student
     *
     * @return 2D array of exam data to display in table
     */
    private Object[][] getEligibleExamsData() {
        try {
            Connection conn = DBConnection.getConnection();

            // SQL query to find eligible exams for the logged-in student
            // Matches students with exams where:
            // 1. Examiner's department matches student's course
            // 2. Examiner is associated with the exam
            String query = "SELECT "
                    + "    s.userID, "
                    + "    u.Name AS StudentName, "
                    + "    e.ExamID, "
                    + "    e.TotalMarks, "
                    + "    e.Duration, "
                    + "    e.Subject, "
                    + "    e.Status "
                    + "FROM "
                    + "    Student s "
                    + "JOIN "
                    + "    User u ON s.userID = u.userID "
                    + "JOIN "
                    + "    Examiner ex ON s.Course = ex.Department "
                    + "JOIN "
                    + "    Exam e ON ex.userID = e.UserID "
                    + "WHERE "
                    + "    s.userID = ? "
                    + "ORDER BY "
                    + "    e.ExamID";

            PreparedStatement pstmt = conn.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pstmt.setString(1, userId);

            ResultSet rs = pstmt.executeQuery();

            // Count the number of rows
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            Object[][] data = new Object[rowCount][6];
            int i = 0;

            while (rs.next()) {
                data[i][0] = rs.getString("ExamID");
                data[i][1] = rs.getString("Subject");
                data[i][2] = rs.getString("TotalMarks");
                data[i][3] = rs.getString("Duration");
                data[i][4] = rs.getString("Status");
                data[i][5] = "Start Exam";
                i++;
            }

            rs.close();
            pstmt.close();
            conn.close();

            return data;
        } catch (Exception e) {
            e.printStackTrace(); // Keep this for debugging
            // Return a fallback dataset if there's an error
            return new Object[][]{
                {"E001", "Data Structures", "8", "20 mins", "Available", "Start Exam"},
                {"E002", "Introduction to AI", "8", "20 mins", "Available", "Start Exam"}
            };
        }
    }

    /**
     * Creates the panel showing past exam results
     *
     * @return JPanel with past results data
     */
private JPanel createPastResultsPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel titleLabel = new JLabel("Past Results");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    panel.add(titleLabel, BorderLayout.NORTH);

    // Table to display past results - MODIFIED: changed columns
    String[] columnNames = {"Exam ID", "Subject", "Result"};
    Object[][] data = getPastResultsData();

    // Create table model that makes only the Result column editable (for the button)
    DefaultTableModel model = new DefaultTableModel(data, columnNames) {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Make only the Result column editable (for the button)
            return column == 2;
        }
    };

    JTable resultsTable = new JTable(model);
    resultsTable.setRowHeight(30);
    resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    
    // Set up the button column with custom renderer and editor
    resultsTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
    resultsTable.getColumnModel().getColumn(2).setCellEditor(new ResultButtonEditor(new JCheckBox(), this));

    JScrollPane scrollPane = new JScrollPane(resultsTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}

    /**
     * Retrieves past results data from the database
     *
     * @return 2D array of result data to display in table
     */
 private Object[][] getPastResultsData() {
    try {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT a.ExamID, e.Subject " +
                       "FROM Attempt a " +
                       "JOIN Exam e ON a.ExamID = e.ExamID " +
                       "WHERE a.UserID = ?";
        PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                     ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();

        // Count rows
        rs.last();
        int rowCount = rs.getRow();
        rs.beforeFirst();

        Object[][] data = new Object[rowCount][3]; // 3 columns: ExamID, Subject, Button
        int i = 0;
        while (rs.next()) {
            data[i][0] = rs.getString("ExamID");
            data[i][1] = rs.getString("Subject");
            data[i][2] = "View Result"; // Button text, result page will handle generation
            i++;
        }

        // Close resources
        rs.close();
        ps.close();
        conn.close();

        return data;
    } catch (Exception e) {
        e.printStackTrace();
        return new Object[0][0];
    }
}


    /**
     * Creates the panel showing user profile information
     *
     * @return JPanel with profile data
     */
   
   /**
 * Custom button editor class for the result view button
 * Handles button click events in result table cells
 */
class ResultButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private StudentPortal parent;
    private int row, column;
    private JTable table;
    
    /**
     * Constructor sets up the button and stores reference to parent
     * @param checkBox Required by DefaultCellEditor
     * @param parent Reference to StudentPortal for access to userId
     */
    public ResultButtonEditor(JCheckBox checkBox, StudentPortal parent) {
        super(checkBox);
        this.parent = parent;
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(new Color(73, 125, 116));
        button.setForeground(Color.WHITE);
        // When button is clicked, stop editing to trigger getCellEditorValue
        button.addActionListener(e -> fireEditingStopped());
    }
    
    /**
     * Called when editing starts, sets up the component
     * @return The button component configured for this cell
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Store table, row, and column for later use when button is clicked
        this.table = table;
        this.row = row;
        this.column = column;
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }
    
    /**
     * Called when editing stops, handles the button click action
     * @return The value to display in the cell
     */
    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            // Get the exam ID from the correct row index
            String examId = table.getValueAt(row, 0).toString();
            
            // Actually create and display the ResultPage instead of just showing a message
            SwingUtilities.invokeLater(() -> {
                new ResultPage(parent.getUserId(), examId).setVisible(true);
                parent.dispose(); // Close the student portal
            });
        }
        isPushed = false;
        return label;
    }
    
    /**
     * Called to stop editing, resets the isPushed flag
     */
    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
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
            // Query joins user and student tables to get complete profile information
            Connection conn = DBConnection.getConnection();
            String query = "SELECT u.Name, u.userID, s.Course, s.AcademicYear " +
                          "FROM user u JOIN student s ON u.userID = s.userID " +
                          "WHERE u.userID = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Display profile fields with labels
                addProfileField(profilePanel, "Student ID:", rs.getString("userID"));
                addProfileField(profilePanel, "Name:", rs.getString("Name"));
                addProfileField(profilePanel, "Course:", rs.getString("Course"));
                addProfileField(profilePanel, "Academic Year:", rs.getString("AcademicYear"));
            }
            
            // Close resources
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        panel.add(profilePanel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Helper method to add a label-value pair to the profile panel
     * @param panel The panel to add fields to
     * @param label The field label
     * @param value The field value
     */
    private void addProfileField(JPanel panel, String label, String value) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(fieldLabel);
        
        JLabel fieldValue = new JLabel(value);
        fieldValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(fieldValue);
    }
    
    /**
     * Loads additional student data if needed
     * Currently a placeholder for future functionality
     */
    private void loadStudentData() {
        // Additional initialization if needed
    }
    
    /**
     * Public getter for userId to be used by ButtonEditor
     * @return The current user ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Custom button renderer class for the table
     * Renders a cell as a button with custom styling
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {
        /**
         * Constructor sets up the button appearance
         */
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(73, 125, 116));
            setForeground(Color.BLACK);
             setContentAreaFilled(true);
        }
        
        /**
         * Configures the button's appearance when rendering
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    /**
     * Custom button editor class for the table
     * Handles button click events in table cells
     */
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private StudentPortal parent;
        private int row, column;
        private JTable table;
        
        /**
         * Constructor sets up the button and stores reference to parent
         * @param checkBox Required by DefaultCellEditor
         * @param parent Reference to StudentPortal for access to userId
         */
        public ButtonEditor(JCheckBox checkBox, StudentPortal parent) {
            super(checkBox);
            this.parent = parent;
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(73, 125, 116));
            button.setForeground(Color.WHITE);
            // When button is clicked, stop editing to trigger getCellEditorValue
            button.addActionListener(e -> fireEditingStopped());
        }
        
        /**
         * Called when editing starts, sets up the component
         * @return The button component configured for this cell
         */
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Store table, row, and column for later use when button is clicked
            this.table = table;
            this.row = row;
            this.column = column;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        /**
         * Called when editing stops, handles the button click action
         * @return The value to display in the cell
         */
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get the exam ID from the correct row index
                String examId = table.getValueAt(row, 0).toString();
                
                // Launch ExamPage with the exam ID and user ID
                // Use SwingUtilities.invokeLater to ensure UI operations happen on EDT
                SwingUtilities.invokeLater(() -> {
//                    new ExamPage(examId, parent.getUserId()).setVisible(true);
                       new ExamPage(parent.getUserId(), examId).setVisible(true);
                    parent.dispose(); // Close the student portal
                });
            }
            isPushed = false;
            return label;
        }
        
        /**
         * Called to stop editing, resets the isPushed flag
         */
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}