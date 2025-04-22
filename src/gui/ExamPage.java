package gui;

import model.Exam;
import model.Option;
import model.Question;
import services.ExamService;
import services.ResponseService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

public class ExamPage extends JFrame {
    private final String userId;
    private final String examId;
    private final ExamService examService;
    private final ResponseService responseService;
    private final Map<Question, List<Option>> questionMap;
    private final List<Question> questionList;
    private int currentQuestionIndex = 0;
    
    // UI Components
    private JLabel timerLabel;
    private JLabel subjectLabel;
    private JLabel marksLabel;
    private JLabel questionTextLabel;
    private JPanel optionsPanel;
    private JButton previousButton;
    private JButton nextButton;
    private JButton submitButton;
    
    // Timer related variables
    private Timer timer;
    private int secondsRemaining;
    private int warningCount = 0;
    private final int MAX_WARNINGS = 4;
    
    // Storage for selected options
    private final Map<String, String> questionOptionMap;
    
    // Flag to track if submit button was clicked
    private boolean isSubmitButtonClicked = false;
    
    public ExamPage(String userId, String examId) {
        this.userId = userId;
        this.examId = examId;
        this.examService = new ExamService();
        this.responseService = new ResponseService();
        this.questionMap = examService.getExamWithQuestions(examId);
        this.questionList = new ArrayList<>(questionMap.keySet());
        this.questionOptionMap = new ConcurrentHashMap<>();
        
        // Check if user has already attempted this exam
        if (responseService.hasAttempted(userId, examId)) {
            JOptionPane.showMessageDialog(this, 
                    "You have already attempted this exam!", 
                    "Exam Attempted", JOptionPane.WARNING_MESSAGE);
            this.dispose();
            return;
        }
        
        // Setup the UI
        setupUI();
        
        // Add focus listeners for anti-cheating
        setupAntiCheating();
        
        // Start the timer
        startExamTimer();
    }
    
    private void setupUI() {
        // Get exam details
        Exam exam = examService.getExamById(examId);
        if (exam == null) {
            JOptionPane.showMessageDialog(this, "Exam not found!");
            this.dispose();
            return;
        }
        
        // Configure frame
        setTitle("Virtual Examination System - Exam");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Prevent closing the window directly
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isSubmitButtonClicked = true; // Set flag to prevent triggering anti-cheat
                int confirm = JOptionPane.showConfirmDialog(
                        ExamPage.this,
                        "Are you sure you want to submit the exam and exit?",
                        "Submit Confirmation",
                        JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    submitExam();
                } else {
                    isSubmitButtonClicked = false; // Reset flag if canceled
                }
            }
        });
        
        // Set layout
        setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(73, 125, 116)); // Teal green color
        headerPanel.setPreferredSize(new Dimension(900, 80));
        
        // Marks Label
        marksLabel = new JLabel("Marks: ");
        marksLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        marksLabel.setForeground(Color.WHITE);
        marksLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        headerPanel.add(marksLabel, BorderLayout.WEST);
        
        // Right side panel for Subject and Timer
        JPanel rightHeaderPanel = new JPanel(new GridLayout(2, 1));
        rightHeaderPanel.setBackground(new Color(73, 125, 116)); // Teal green color
        
        // Subject label
        subjectLabel = new JLabel("Subject: " + exam.getSubject());
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        subjectLabel.setForeground(Color.WHITE);
        subjectLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        subjectLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        
        // Timer label
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timerLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        
        rightHeaderPanel.add(subjectLabel);
        rightHeaderPanel.add(timerLabel);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        contentPanel.setBackground(new Color(234, 233, 232)); // Light gray background
        
        // Question Label
        questionTextLabel = new JLabel();
        questionTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        questionTextLabel.setForeground(new Color(73, 125, 116)); // Teal green color
        questionTextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Options Panel
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(new Color(234, 233, 232)); // Light gray background
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(questionTextLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(optionsPanel);
        
        // Footer Panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(10, 40, 20, 40));
        footerPanel.setBackground(new Color(234, 233, 232)); // Light gray background
        
        // Navigation buttons panel
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navigationPanel.setBackground(new Color(234, 233, 232)); // Light gray background
        
        previousButton = new JButton("Previous");
        previousButton.setPreferredSize(new Dimension(120, 45));
        previousButton.setBackground(new Color(231, 76, 60)); // Red color
        previousButton.setForeground(Color.WHITE);
        previousButton.setOpaque(true);
        previousButton.setContentAreaFilled(true);
        previousButton.setFocusPainted(false);
        previousButton.setBorderPainted(false);
        previousButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        previousButton.addActionListener(e -> navigateToPreviousQuestion());
        
        nextButton = new JButton("Next");
        nextButton.setPreferredSize(new Dimension(120, 45));
        nextButton.setBackground(new Color(46, 204, 113)); // Green color
        nextButton.setForeground(Color.WHITE);
        nextButton.setOpaque(true);
        nextButton.setContentAreaFilled(true);
        nextButton.setFocusPainted(false);
        nextButton.setBorderPainted(false);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e -> navigateToNextQuestion());
        
        navigationPanel.add(previousButton);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(nextButton);
        
        // Submit button panel
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        submitPanel.setBackground(new Color(234, 233, 232)); // Light gray background
        
        submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(120, 45));
        submitButton.setBackground(new Color(46, 204, 113)); // Green color
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setContentAreaFilled(true);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> submitExam());
        
        submitPanel.add(submitButton);
        
        footerPanel.add(navigationPanel, BorderLayout.WEST);
        footerPanel.add(submitPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        // Load the first question
        loadQuestion(currentQuestionIndex);
    }
    
    private void loadQuestion(int index) {
        if (index < 0 || index >= questionList.size()) {
            return;
        }
        
        Question question = questionList.get(index);
        List<Option> options = questionMap.get(question);
        
        // Set question text
        questionTextLabel.setText("Question " + (index + 1) + ": " + question.getText());
        
        // Set marks
        marksLabel.setText("Marks: " + question.getMarks());
        
        // Clear options panel
        optionsPanel.removeAll();
        
        // Get previously selected option for this question
        String selectedOptionId = questionOptionMap.get(question.getQuestionID());
        
        // Create a button group for radio buttons
        ButtonGroup optionGroup = new ButtonGroup();
        
        // Add options as radio buttons
        for (Option option : options) {
            JRadioButton optionButton = new JRadioButton(option.getText());
            optionButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            optionButton.setBackground(new Color(240, 240, 240));
            optionButton.setFocusPainted(false);
            
            // Select this option if it was previously selected
            if (selectedOptionId != null && selectedOptionId.equals(option.getOptionId())) {
                optionButton.setSelected(true);
            }
            
            // Add action listener to save the selected option
            optionButton.addActionListener(e -> {
                questionOptionMap.put(question.getQuestionID(), option.getOptionId());
            });
            
            // Disable text selection to prevent copying
            optionButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    e.consume();
                }
            });
            
            optionGroup.add(optionButton);
            
            JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            optionPanel.setBackground(new Color(240, 240, 240));
            optionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            optionPanel.add(optionButton);
            
            optionsPanel.add(optionPanel);
            optionsPanel.add(Box.createVerticalStrut(10));
        }
        
        // Update button states
        previousButton.setEnabled(index > 0);
        nextButton.setEnabled(index < questionList.size() - 1);
        
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }
    
    private void navigateToPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            loadQuestion(currentQuestionIndex);
        }
    }
    
    private void navigateToNextQuestion() {
        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
        }
    }
    
   private void startExamTimer() {
    // Get exam duration
    Exam exam = examService.getExamById(examId);
    if (exam == null) {
        JOptionPane.showMessageDialog(this, "Error: Exam not found.");
        return;
    }

    String durationStr = exam.getDuration();
    int hours = 0;
    int minutes = 0;

    try {
        if (durationStr.contains(":")) {
            // Format: HH:MM
            String[] parts = durationStr.split(":");
            hours = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
            minutes = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
        } else {
            // Format: "20 mins" or "45"
            minutes = Integer.parseInt(durationStr.replaceAll("[^0-9]", ""));
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid exam duration format: " + durationStr);
        return;
    }

    // Calculate total seconds
    secondsRemaining = (hours * 60 + minutes) * 60;

    // Start the timer
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            if (secondsRemaining > 0) {
                secondsRemaining--;
                SwingUtilities.invokeLater(() -> updateTimerDisplay());
            } else {
                // Time's up, auto-submit the exam
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(ExamPage.this,
                            "Time's up! Your exam will be submitted.",
                            "Time's Up", JOptionPane.INFORMATION_MESSAGE);
                    submitExam();
                });
                timer.cancel();
            }
        }
    }, 0, 1000);
}

    
    private void updateTimerDisplay() {
        int hours = secondsRemaining / 3600;
        int minutes = (secondsRemaining % 3600) / 60;
        int seconds = secondsRemaining % 60;
        
        String timeString;
        if (hours > 0) {
            timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeString = String.format("%02d:%02d", minutes, seconds);
        }
        
        timerLabel.setText(timeString);
        
        // Change timer color to red when less than 5 minutes remaining
        if (secondsRemaining < 300) {
            timerLabel.setForeground(Color.RED);
        }
    }
    
    private void setupAntiCheating() {
        // Disable copy-paste on question and options
        if (questionTextLabel != null) {
            questionTextLabel.setTransferHandler(null);
        }
        if (optionsPanel != null) {
            optionsPanel.setTransferHandler(null);
        }

        // Add focus listener to detect when window loses focus
        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // Nothing to do when window gains focus
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!isSubmitButtonClicked) { // Check flag before triggering warning
                    handlePotentialCheating();
                }
            }
        });

        // Add mouse listener to detect when mouse leaves window
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // Check if the submit button was clicked
                if (isSubmitButtonClicked) {
                    return; // Prevent triggering the warning if the submit button is clicked
                }

                // Check if mouse is actually outside window bounds
                Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mouseLocation, ExamPage.this);

                if (mouseLocation.x < 0 || mouseLocation.y < 0 || 
                    mouseLocation.x > getWidth() || mouseLocation.y > getHeight()) {
                    handlePotentialCheating();
                }
            }
        });
    }
    
    private void handlePotentialCheating() {
        warningCount++;
        
        if (warningCount < MAX_WARNINGS) {
            JOptionPane.showMessageDialog(this,
                "Warning! Leaving the exam window is not allowed.\n" +
                "Your exam will be auto-submitted after " + MAX_WARNINGS + " warnings.",
                "Cheating Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "You have reached the maximum number of warnings!\n" +
                "Your exam will now be submitted.",
                "Exam Terminated", JOptionPane.ERROR_MESSAGE);
            submitExam();
        }
    }
    
    private void submitExam() {
        // Cancel timer
        if (timer != null) {
            timer.cancel();
        }
        
        // Submit responses
        boolean success = responseService.submitAllResponses(userId, examId, questionOptionMap);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                    "Exam submitted successfully!", 
                    "Exam Submitted", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                    "Failed to submit exam. Please try again.", 
                    "Submission Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Return to student portal
        new StudentPortal(userId).setVisible(true);
        this.dispose();
    }
    
    // This main method is just for testing - remove in production
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            // Replace with actual user ID and exam ID
            new ExamPage("U007", "E001").setVisible(true);
        });
    }
}