import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ExaminerPortal extends JFrame {
    private String examinerId;

    public ExaminerPortal(String examinerId) {
        this.examinerId = examinerId;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Virtual Examination System - Examiner Portal");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("EXAMINER PORTAL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + getExaminerName(examinerId));
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel with tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // My Exams Tab
        JPanel myExamsPanel = createMyExamsPanel();
        tabbedPane.addTab("My Exams", myExamsPanel);

        // Create Exam Tab
        JPanel createExamPanel = createCreateExamPanel();
        tabbedPane.addTab("Create Exam", createExamPanel);

        // Questions Bank Tab
        JPanel questionsPanel = createQuestionsPanel();
        tabbedPane.addTab("Question Bank", questionsPanel);

        // Results Tab
        JPanel resultsPanel = createResultsPanel();
        tabbedPane.addTab("Exam Results", resultsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> {
            new LogonPage().setVisible(true);
            dispose();
        });
        footerPanel.add(logoutButton);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createMyExamsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get exams created by this examiner from ExamService
        List<Exam> myExams = getExaminerExams(examinerId);

        if (myExams.isEmpty()) {
            JLabel noExamsLabel = new JLabel("You haven't created any exams yet.", SwingConstants.CENTER);
            noExamsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(noExamsLabel, BorderLayout.CENTER);
            return panel;
        }

        String[] columnNames = {"Exam ID", "Subject", "Date", "Time", "Duration", "Total Marks", "Status", "Actions"};
        Object[][] data = new Object[myExams.size()][8];

        for (int i = 0; i < myExams.size(); i++) {
            Exam exam = myExams.get(i);
            data[i][0] = exam.getExamId();
            data[i][1] = exam.getSubject();
            data[i][2] = exam.getDate();
            data[i][3] = exam.getStartTime() + " - " + exam.getEndTime();
            data[i][4] = exam.getDuration();
            data[i][5] = exam.getTotalMarks();
            data[i][6] = exam.getStatus();
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            JButton editButton = new JButton("Edit");
            editButton.putClientProperty("examId", exam.getExamId());
            editButton.addActionListener(e -> {
                String examId = (String) editButton.getClientProperty("examId");
                editExam(examId);
            });
            
            JButton resultsButton = new JButton("Results");
            resultsButton.putClientProperty("examId", exam.getExamId());
            resultsButton.addActionListener(e -> {
                String examId = (String) resultsButton.getClientProperty("examId");
                viewExamResults(examId);
            });
            
            buttonPanel.add(editButton);
            buttonPanel.add(resultsButton);
            data[i][7] = buttonPanel;
        }

        JTable examsTable = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
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
        examsTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonPanelRenderer());
        examsTable.getColumnModel().getColumn(7).setCellEditor(new ButtonPanelEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(examsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCreateExamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Exam"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Subject
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        JTextField subjectField = new JTextField(20);
        formPanel.add(subjectField, gbc);

        // Total Marks
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Total Marks:"), gbc);
        gbc.gridx = 1;
        JSpinner marksSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        formPanel.add(marksSpinner, gbc);

        // Duration
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Duration (minutes):"), gbc);
        gbc.gridx = 1;
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 180, 5));
        formPanel.add(durationSpinner, gbc);

        // Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Exam Date:"), gbc);
        gbc.gridx = 1;
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        formPanel.add(dateSpinner, gbc);

        // Start Time
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        formPanel.add(timeSpinner, gbc);

        // Questions Selection
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Select Questions:"), gbc);
        gbc.gridx = 1;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        
        // Get questions from QuestionService
        List<Question> questions = getAvailableQuestions(examinerId);
        JList<Question> questionList = new JList<>(questions.toArray(new Question[0]));
        questionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        questionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Question) {
                    Question q = (Question) value;
                    setText(q.getQuestionId() + ": " + q.getText() + " (" + q.getMarks() + " marks)");
                }
                return this;
            }
        });
        JScrollPane listScrollPane = new JScrollPane(questionList);
        formPanel.add(listScrollPane, gbc);
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;

        // Create Button
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.EAST;
        JButton createButton = new JButton("Create Exam");
        createButton.setBackground(new Color(0, 102, 204));
        createButton.setForeground(Color.WHITE);
        createButton.addActionListener(e -> {
            // Validate and create exam
            createNewExam(subjectField.getText(), 
                         (Integer) marksSpinner.getValue(), 
                         (Integer) durationSpinner.getValue(), 
                         questionList.getSelectedValuesList());
        });
        formPanel.add(createButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuestionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header with add question button
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Question Bank");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton addButton = new JButton("Add New Question");
        addButton.setBackground(new Color(0, 102, 204));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> showAddQuestionDialog());
        headerPanel.add(addButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Get questions from QuestionService
        List<Question> questions = getExaminerQuestions(examinerId);

        if (questions.isEmpty()) {
            JLabel noQuestionsLabel = new JLabel("No questions found. Add your first question!", SwingConstants.CENTER);
            noQuestionsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(noQuestionsLabel, BorderLayout.CENTER);
            return panel;
        }

        String[] columnNames = {"Question ID", "Text", "Category", "Level", "Marks", "Type", "Actions"};
        Object[][] data = new Object[questions.size()][7];

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            data[i][0] = question.getQuestionId();
            data[i][1] = question.getText();
            data[i][2] = question.getCategory();
            data[i][3] = question.getLevel();
            data[i][4] = question.getMarks();
            data[i][5] = question.isMCQ() ? "MCQ" : "Free Text";
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            JButton editButton = new JButton("Edit");
            editButton.putClientProperty("questionId", question.getQuestionId());
            editButton.addActionListener(e -> {
                String questionId = (String) editButton.getClientProperty("questionId");
                editQuestion(questionId);
            });
            
            JButton deleteButton = new JButton("Delete");
            deleteButton.putClientProperty("questionId", question.getQuestionId());
            deleteButton.addActionListener(e -> {
                String questionId = (String) deleteButton.getClientProperty("questionId");
                deleteQuestion(questionId);
            });
            
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            data[i][6] = buttonPanel;
        }

        JTable questionsTable = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
            }
        };
        questionsTable.setRowHeight(30);
        questionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        questionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Make the text column wider
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(300);

        // Center-align all columns except text and actions
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < questionsTable.getColumnCount(); i++) {
            if (i != 1 && i != 6) {
                questionsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Set custom renderer and editor for the button column
        questionsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonPanelRenderer());
        questionsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonPanelEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(questionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get all exams for this examiner
        List<Exam> exams = getExaminerExams(examinerId);

        if (exams.isEmpty()) {
            JLabel noExamsLabel = new JLabel("No exams found to display results.", SwingConstants.CENTER);
            noExamsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(noExamsLabel, BorderLayout.CENTER);
            return panel;
        }

        // Exam selection combo box
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Select Exam:"));
        JComboBox<Exam> examComboBox = new JComboBox<>(exams.toArray(new Exam[0]));
        examComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Exam) {
                    Exam exam = (Exam) value;
                    setText(exam.getExamId() + " - " + exam.getSubject());
                }
                return this;
            }
        });
        filterPanel.add(examComboBox);

        JButton loadButton = new JButton("Load Results");
        loadButton.setBackground(new Color(0, 102, 204));
        loadButton.setForeground(Color.WHITE);
        loadButton.addActionListener(e -> {
            Exam selectedExam = (Exam) examComboBox.getSelectedItem();
            if (selectedExam != null) {
                loadExamResults(panel, selectedExam.getExamId());
            }
        });
        filterPanel.add(loadButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Initial empty results table
        JLabel selectExamLabel = new JLabel("Please select an exam and click 'Load Results'", SwingConstants.CENTER);
        selectExamLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(selectExamLabel, BorderLayout.CENTER);

        return panel;
    }

    private void loadExamResults(JPanel panel, String examId) {
        // Get results for the selected exam from ResultService
        List<Result> results = getExamResults(examId);

        if (results.isEmpty()) {
            panel.remove(1); // Remove the current center component
            JLabel noResultsLabel = new JLabel("No results available for this exam.", SwingConstants.CENTER);
            noResultsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(noResultsLabel, BorderLayout.CENTER);
        } else {
            panel.remove(1); // Remove the current center component

            String[] columnNames = {"Rank", "Student ID", "Name", "Score", "Total Marks", "Grade", "Accuracy"};
            Object[][] data = new Object[results.size()][7];

            for (int i = 0; i < results.size(); i++) {
                Result result = results.get(i);
                data[i][0] = i + 1;
                data[i][1] = result.getStudentId();
                data[i][2] = result.getStudentName();
                data[i][3] = result.getScore();
                data[i][4] = result.getTotalMarks();
                data[i][5] = result.getGrade();
                data[i][6] = result.getAccuracy() + "%";
            }

            JTable resultsTable = new JTable(data, columnNames);
            resultsTable.setRowHeight(30);
            resultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

            // Center-align all columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < resultsTable.getColumnCount(); i++) {
                resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

            JScrollPane scrollPane = new JScrollPane(resultsTable);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void showAddQuestionDialog() {
        JDialog dialog = new JDialog(this, "Add New Question", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Question Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Question Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Multiple Choice (MCQ)", "True/False", "Short Answer", "Essay"});
        formPanel.add(typeComboBox, gbc);

        // Question Text
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Question Text:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextArea questionTextArea = new JTextArea(3, 30);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(questionTextArea);
        formPanel.add(scrollPane, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Category
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        JTextField categoryField = new JTextField(20);
        formPanel.add(categoryField, gbc);

        // Difficulty Level
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Difficulty Level:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> levelComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        formPanel.add(levelComboBox, gbc);

        // Marks
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Marks:"), gbc);
        gbc.gridx = 1;
        JSpinner marksSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        formPanel.add(marksSpinner, gbc);

        // Options Panel (for MCQ)
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Options (for MCQ)"));

        JPanel optionListPanel = new JPanel();
        optionListPanel.setLayout(new BoxLayout(optionListPanel, BoxLayout.Y_AXIS));

        // Add initial 4 options for MCQ
        for (int i = 0; i < 4; i++) {
            JPanel optionRow = new JPanel(new BorderLayout());
            JTextField optionField = new JTextField();
            JCheckBox correctCheck = new JCheckBox("Correct");
            optionRow.add(optionField, BorderLayout.CENTER);
            optionRow.add(correctCheck, BorderLayout.EAST);
            optionListPanel.add(optionRow);
            optionListPanel.add(Box.createVerticalStrut(5));
        }

        optionsPanel.add(new JScrollPane(optionListPanel), BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(optionsPanel, gbc);
        gbc.gridwidth = 1;

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Question");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            // Validate and save question
            saveQuestion(questionTextArea.getText(), 
                        (String) typeComboBox.getSelectedItem(),
                        categoryField.getText(),
                        (String) levelComboBox.getSelectedItem(),
                        (Integer) marksSpinner.getValue());
            dialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Helper methods to get data from services (mock implementations)
    private String getExaminerName(String examinerId) {
        // In real implementation, get from database
        return "Dr. Examiner"; // Replace with actual lookup
    }

    private List<Exam> getExaminerExams(String examinerId) {
        // Mock data - replace with actual data from ExamService
        List<Exam> exams = new ArrayList<>();
        exams.add(new Exam("E001", "Data Structures", "2025-04-20", "10:00 AM", "10:20 AM", "20 mins", 8, "Active"));
        exams.add(new Exam("E002", "Introduction to AI", "2025-04-21", "11:00 AM", "11:20 AM", "20 mins", 8, "Active"));
        return exams;
    }

    private List<Question> getAvailableQuestions(String examinerId) {
        // Mock data - replace with actual data from QuestionService
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Q001", "What is a stack?", "DS", "Easy", true, false, 2));
        questions.add(new Question("Q002", "What is overfitting?", "AI", "Medium", true, false, 2));
        questions.add(new Question("Q003", "Explain polymorphism", "OOP", "Hard", false, true, 5));
        return questions;
    }

    private List<Question> getExaminerQuestions(String examinerId) {
        // Mock data - replace with actual data from QuestionService
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Q001", "What is a stack?", "DS", "Easy", true, false, 2));
        questions.add(new Question("Q002", "What is overfitting?", "AI", "Medium", true, false, 2));
        questions.add(new Question("Q003", "Explain polymorphism", "OOP", "Hard", false, true, 5));
        return questions;
    }

    private List<Result> getExamResults(String examId) {
        // Mock data - replace with actual data from ResultService
        List<Result> results = new ArrayList<>();
        results.add(new Result("RES001", "S001", "John Doe", examId, 8, 8, "A", 100));
        results.add(new Result("RES002", "S002", "Jane Smith", examId, 6, 8, "B", 75));
        results.add(new Result("RES003", "S003", "Bob Johnson", examId, 5, 8, "C", 62.5));
        return results;
    }

    // Action methods
    private void editExam(String examId) {
        JOptionPane.showMessageDialog(this, "Edit exam: " + examId, "Edit Exam", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewExamResults(String examId) {
        JOptionPane.showMessageDialog(this, "View results for exam: " + examId, "Exam Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createNewExam(String subject, int totalMarks, int duration, List<Question> selectedQuestions) {
        // In real implementation, save to database
        JOptionPane.showMessageDialog(this, 
            "Exam created successfully!\n" +
            "Subject: " + subject + "\n" +
            "Total Marks: " + totalMarks + "\n" +
            "Duration: " + duration + " minutes\n" +
            "Questions: " + selectedQuestions.size(),
            "Exam Created", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void editQuestion(String questionId) {
        JOptionPane.showMessageDialog(this, "Edit question: " + questionId, "Edit Question", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteQuestion(String questionId) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete question " + questionId + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Question deleted: " + questionId, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveQuestion(String text, String type, String category, String level, int marks) {
        // In real implementation, save to database
        JOptionPane.showMessageDialog(this, 
            "Question saved successfully!\n" +
            "Text: " + text + "\n" +
            "Type: " + type + "\n" +
            "Category: " + category + "\n" +
            "Level: " + level + "\n" +
            "Marks: " + marks,
            "Question Saved", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Model classes for demonstration
    class Exam {
        private String examId, subject, date, startTime, endTime, duration, status;
        private int totalMarks;

        public Exam(String examId, String subject, String date, String startTime, String endTime, String duration, int totalMarks, String status) {
            this.examId = examId;
            this.subject = subject;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
            this.totalMarks = totalMarks;
            this.status = status;
        }

        // Getters
        public String getExamId() { return examId; }
        public String getSubject() { return subject; }
        public String getDate() { return date; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getDuration() { return duration; }
        public int getTotalMarks() { return totalMarks; }
        public String getStatus() { return status; }
    }

    class Question {
        private String questionId, text, category, level;
        private boolean isMCQ, isFIB;
        private int marks;

        public Question(String questionId, String text, String category, String level, boolean isMCQ, boolean isFIB, int marks) {
            this.questionId = questionId;
            this.text = text;
            this.category = category;
            this.level = level;
            this.isMCQ = isMCQ;
            this.isFIB = isFIB;
            this.marks = marks;
        }

        // Getters
        public String getQuestionId() { return questionId; }
        public String getText() { return text; }
        public String getCategory() { return category; }
        public String getLevel() { return level; }
        public boolean isMCQ() { return isMCQ; }
        public boolean isFIB() { return isFIB; }
        public int getMarks() { return marks; }
    }

    class Result {
        private String resultId, studentId, studentName, examId, grade;
        private int score, totalMarks;
        private double accuracy;

        public Result(String resultId, String studentId, String studentName, String examId, int score, int totalMarks, String grade, double accuracy) {
            this.resultId = resultId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.examId = examId;
            this.score = score;
            this.totalMarks = totalMarks;
            this.grade = grade;
            this.accuracy = accuracy;
        }

        // Getters
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public int getScore() { return score; }
        public int getTotalMarks() { return totalMarks; }
        public String getGrade() { return grade; }
        public double getAccuracy() { return accuracy; }
    }

    // Custom renderer and editor for button panels in tables
    class ButtonPanelRenderer implements TableCellRenderer {
        private JPanel panel;

        public ButtonPanelRenderer() {
            panel = new JPanel();
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            panel.removeAll();
            if (value instanceof JPanel) {
                panel = (JPanel) value;
            }
            return panel;
        }
    }

    class ButtonPanelEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton button;

        public ButtonPanelEditor(JCheckBox checkBox) {
            super(checkBox);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof JPanel) {
                panel = (JPanel) value;
            }
            return panel;
        }

        public Object getCellEditorValue() {
            return panel;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ExaminerPortal("EX001").setVisible(true);
        });
    }
}