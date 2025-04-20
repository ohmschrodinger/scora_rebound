import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class StudentPortal extends JFrame {
    private String studentId;

    public StudentPortal(String studentId) {
        this.studentId = studentId;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Virtual Examination System - Student Portal");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("STUDENT PORTAL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + getStudentName(studentId));
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel with tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Upcoming Exams Tab
        JPanel upcomingExamsPanel = createUpcomingExamsPanel();
        tabbedPane.addTab("Upcoming Exams", upcomingExamsPanel);

        // Exam History Tab
        JPanel examHistoryPanel = createExamHistoryPanel();
        tabbedPane.addTab("Exam History", examHistoryPanel);

        // Profile Tab
        JPanel profilePanel = createProfilePanel();
        tabbedPane.addTab("Profile", profilePanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
        footerPanel.add(logoutButton);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createUpcomingExamsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get upcoming exams from your ExamService
        List<Exam> upcomingExams = getUpcomingExams(studentId);

        if (upcomingExams.isEmpty()) {
            JLabel noExamsLabel = new JLabel("No upcoming exams scheduled.", SwingConstants.CENTER);
            noExamsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(noExamsLabel, BorderLayout.CENTER);
            return panel;
        }

        String[] columnNames = {"Exam ID", "Subject", "Date", "Time", "Duration", "Total Marks", "Action"};
        Object[][] data = new Object[upcomingExams.size()][7];

        for (int i = 0; i < upcomingExams.size(); i++) {
            Exam exam = upcomingExams.get(i);
            data[i][0] = exam.getExamId();
            data[i][1] = exam.getSubject();
            data[i][2] = exam.getDate(); // You would format this properly
            data[i][3] = exam.getStartTime() + " - " + exam.getEndTime();
            data[i][4] = exam.getDuration();
            data[i][5] = exam.getTotalMarks();
            
            JButton startButton = new JButton("Start Exam");
            startButton.putClientProperty("examId", exam.getExamId());
            startButton.addActionListener(e -> {
                String examId = (String) startButton.getClientProperty("examId");
                startExam(examId);
            });
            data[i][6] = startButton;
        }

        JTable examsTable = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only action column is editable
            }
        };
        examsTable.setRowHeight(30);
        examsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        examsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Center-align all columns except the last one
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < examsTable.getColumnCount() - 1; i++) {
            examsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set custom renderer and editor for the button column
        examsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        examsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(examsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExamHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get exam history from your ResultService
        List<Result> examResults = getExamResults(studentId);

        if (examResults.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No exam results available.", SwingConstants.CENTER);
            noResultsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(noResultsLabel, BorderLayout.CENTER);
            return panel;
        }

        String[] columnNames = {"Exam ID", "Subject", "Date", "Score", "Total Marks", "Grade", "View"};
        Object[][] data = new Object[examResults.size()][7];

        for (int i = 0; i < examResults.size(); i++) {
            Result result = examResults.get(i);
            data[i][0] = result.getExamId();
            data[i][1] = result.getSubject();
            data[i][2] = result.getExamDate(); // You would format this properly
            data[i][3] = result.getScore();
            data[i][4] = result.getTotalMarks();
            data[i][5] = result.getGrade();
            
            JButton viewButton = new JButton("View Details");
            viewButton.putClientProperty("resultId", result.getResultId());
            viewButton.addActionListener(e -> {
                String resultId = (String) viewButton.getClientProperty("resultId");
                viewResultDetails(resultId);
            });
            data[i][6] = viewButton;
        }

        JTable resultsTable = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only action column is editable
            }
        };
        resultsTable.setRowHeight(30);
        resultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Center-align all columns except the last one
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < resultsTable.getColumnCount() - 1; i++) {
            resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set custom renderer and editor for the button column
        resultsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        resultsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Get student details from your database
        Student student = getStudentDetails(studentId);

        JPanel detailsPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel idValue = new JLabel(studentId);
        idValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel nameValue = new JLabel(student.getName());
        nameValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel courseValue = new JLabel(student.getCourse());
        courseValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel yearLabel = new JLabel("Academic Year:");
        yearLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel yearValue = new JLabel(String.valueOf(student.getAcademicYear()));
        yearValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel emailValue = new JLabel(student.getEmail());
        emailValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        detailsPanel.add(idLabel);
        detailsPanel.add(idValue);
        detailsPanel.add(nameLabel);
        detailsPanel.add(nameValue);
        detailsPanel.add(courseLabel);
        detailsPanel.add(courseValue);
        detailsPanel.add(yearLabel);
        detailsPanel.add(yearValue);
        detailsPanel.add(emailLabel);
        detailsPanel.add(emailValue);

        panel.add(detailsPanel, BorderLayout.CENTER);

        return panel;
    }

    // Helper methods to get data from services (mock implementations)
    private String getStudentName(String studentId) {
        // In real implementation, get from database
        return "Student Name"; // Replace with actual lookup
    }

    private List<Exam> getUpcomingExams(String studentId) {
        // Mock data - replace with actual data from ExamService
        List<Exam> exams = new ArrayList<>();
        exams.add(new Exam("E001", "Data Structures", "2025-04-20", "10:00 AM", "10:20 AM", "20 mins", 8));
        exams.add(new Exam("E002", "Introduction to AI", "2025-04-21", "11:00 AM", "11:20 AM", "20 mins", 8));
        return exams;
    }

    private List<Result> getExamResults(String studentId) {
        // Mock data - replace with actual data from ResultService
        List<Result> results = new ArrayList<>();
        results.add(new Result("RES001", "E001", "Data Structures", "2025-04-15", 8, 8, "A"));
        results.add(new Result("RES002", "E002", "Introduction to AI", "2025-04-16", 6, 8, "B"));
        return results;
    }

    private Student getStudentDetails(String studentId) {
        // Mock data - replace with actual data from database
        return new Student(studentId, "Student Name", "Computer Science", 2, "student@university.edu");
    }

    private void startExam(String examId) {
        new ExamPage(studentId, examId).setVisible(true);
        this.dispose();
    }

    private void viewResultDetails(String resultId) {
        new ResultPage(studentId, resultId).setVisible(true);
    }

    // Model classes for demonstration
    class Exam {
        private String examId, subject, date, startTime, endTime, duration;
        private int totalMarks;

        public Exam(String examId, String subject, String date, String startTime, String endTime, String duration, int totalMarks) {
            this.examId = examId;
            this.subject = subject;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
            this.totalMarks = totalMarks;
        }

        // Getters
        public String getExamId() { return examId; }
        public String getSubject() { return subject; }
        public String getDate() { return date; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getDuration() { return duration; }
        public int getTotalMarks() { return totalMarks; }
    }

    class Result {
        private String resultId, examId, subject, examDate, grade;
        private int score, totalMarks;

        public Result(String resultId, String examId, String subject, String examDate, int score, int totalMarks, String grade) {
            this.resultId = resultId;
            this.examId = examId;
            this.subject = subject;
            this.examDate = examDate;
            this.score = score;
            this.totalMarks = totalMarks;
            this.grade = grade;
        }

        // Getters
        public String getResultId() { return resultId; }
        public String getExamId() { return examId; }
        public String getSubject() { return subject; }
        public String getExamDate() { return examDate; }
        public int getScore() { return score; }
        public int getTotalMarks() { return totalMarks; }
        public String getGrade() { return grade; }
    }

    class Student {
        private String studentId, name, course, email;
        private int academicYear;

        public Student(String studentId, String name, String course, int academicYear, String email) {
            this.studentId = studentId;
            this.name = name;
            this.course = course;
            this.academicYear = academicYear;
            this.email = email;
        }

        // Getters
        public String getName() { return name; }
        public String getCourse() { return course; }
        public int getAcademicYear() { return academicYear; }
        public String getEmail() { return email; }
    }

    // Button renderer and editor for JTable
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                return (JButton) value;
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof JButton) {
                button = (JButton) value;
                label = button.getText();
            }
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle button action here if needed
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}