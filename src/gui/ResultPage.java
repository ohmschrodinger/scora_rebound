package gui;

import gui.StudentPortal;
import model.Result;
import model.PerformanceReport;
import services.ResultDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ResultPage extends JFrame {
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel gaugePanel;
    private JPanel chartPanel;
    private JLabel studentLabel;
    private JLabel resultIdLabel;
    private JLabel scoreLabel;
    private JLabel gradeLabel;
    private JLabel accuracyLabel;
    private JLabel rankLabel;
    private JLabel speedometerLabel;
    private JButton backButton;
    
    private Result result;
    private PerformanceReport performanceReport;
    private String userId;
    private String examId;
    
    // This constructor accepts userId and examId to fetch data
    public ResultPage(String userId, String examId) {
        this.userId = userId;
        this.examId = examId;
        
        initComponents();
        fetchData();
        updateUI();
        
        setTitle("Result Summary");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    // This constructor accepts already fetched Result and PerformanceReport
    public ResultPage(Result result, PerformanceReport performanceReport) {
        this.result = result;
        this.performanceReport = performanceReport;
        this.userId = result.getUserID();
        
        initComponents();
        updateUI();
        
        setTitle("Result Summary");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void initComponents() {
        // Initialize main layout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel with title, student name, and result ID
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Result Summary");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        studentLabel = new JLabel("Student: ");
        studentLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        studentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        resultIdLabel = new JLabel("Result ID: ");
        resultIdLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        resultIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add separator line
        JSeparator separator1 = new JSeparator();
        separator1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator1.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(studentLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(resultIdLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(separator1);
        
        // Content panel with score/grade and accuracy/rank
        contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Left panel for score/grade and speedometer
        gaugePanel = new JPanel();
        gaugePanel.setLayout(new BoxLayout(gaugePanel, BoxLayout.Y_AXIS));
        gaugePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 228, 226), 2));
        gaugePanel.setBackground(new Color(200, 228, 226));
        
        JPanel scoreGradePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        scoreGradePanel.setOpaque(false);
        
        scoreLabel = new JLabel("Score: ");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        gradeLabel = new JLabel("Grade: ");
        gradeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        scoreGradePanel.add(scoreLabel);
        scoreGradePanel.add(gradeLabel);
        
        speedometerLabel = new JLabel();
        speedometerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel legendPanel = new JPanel(new GridLayout(4, 1));
        legendPanel.setOpaque(false);
        
        addLegendItem(legendPanel, new Color(34, 139, 34), "Excellent");
        addLegendItem(legendPanel, new Color(255, 215, 0), "Good");
        addLegendItem(legendPanel, new Color(255, 140, 0), "Fair");
        addLegendItem(legendPanel, new Color(255, 0, 0), "Poor");
        
        gaugePanel.add(scoreGradePanel);
        gaugePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gaugePanel.add(speedometerLabel);
        gaugePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gaugePanel.add(legendPanel);
        
        // Right panel for accuracy/rank and pie chart
        chartPanel = new JPanel();
        chartPanel.setLayout(new BoxLayout(chartPanel, BoxLayout.Y_AXIS));
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 228, 226), 2));
        chartPanel.setBackground(new Color(200, 228, 226));
        
        JPanel accuracyRankPanel = new JPanel();
        accuracyRankPanel.setLayout(new BoxLayout(accuracyRankPanel, BoxLayout.Y_AXIS));
        accuracyRankPanel.setOpaque(false);
        
        accuracyLabel = new JLabel("Accuracy: ");
        accuracyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        accuracyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rankLabel = new JLabel("Rank: ");
        rankLabel.setFont(new Font("Arial", Font.BOLD, 18));
        rankLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Container for custom pie chart
        JPanel pieChartContainer = new JPanel(new BorderLayout());
        pieChartContainer.setOpaque(false);
        
        accuracyRankPanel.add(accuracyLabel);
        accuracyRankPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        accuracyRankPanel.add(rankLabel);
        
        chartPanel.add(accuracyRankPanel);
        chartPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        chartPanel.add(pieChartContainer);
        
        contentPanel.add(gaugePanel);
        contentPanel.add(chartPanel);
        
        // Back button
        backButton = new JButton("Back to Student Portal");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
                 new StudentPortal(userId); 
            }
        });
        
        // Add all components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void addLegendItem(JPanel panel, Color color, String text) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        itemPanel.setOpaque(false);
        
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBackground(color);
        
        JLabel textLabel = new JLabel(text);
        
        itemPanel.add(colorBox);
        itemPanel.add(textLabel);
        panel.add(itemPanel);
    }
    
    private void fetchData() {
        try {
            ResultDAO resultDAO = new ResultDAO();
            // Generate and get result
            result = resultDAO.generateAndGetResult(userId, examId);
            
            if (result != null) {
                // Generate and get performance report
                performanceReport = resultDAO.generateAndGetPerformanceReport(result.getResultID());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to retrieve result data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUI() {
        if (result == null || performanceReport == null) {
            return;
        }
        
        // Update labels with data
        studentLabel.setText("Student: " + userId);
        resultIdLabel.setText("Result ID: " + result.getResultID());
        scoreLabel.setText("Score: " + result.getScore() + "/10");
        gradeLabel.setText("Grade: " + result.getGrade());
        
        // Parse accuracy from string to percentage
        String accuracyStr = performanceReport.getAccuracy();
        double accuracy = 0;
        try {
            // Extract numeric part from string like "50%" or "50.5%"
            accuracyStr = accuracyStr.replaceAll("[^0-9.]", "");
            accuracy = Double.parseDouble(accuracyStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        accuracyLabel.setText("Accuracy: " + performanceReport.getAccuracy());
        rankLabel.setText("Rank: " + performanceReport.getRanks());
        
        // Load speedometer image based on grade
        String imageName;
        switch (result.getGrade()) {
            case "A":
                imageName = "excellent.jpg";
                break;
            case "B":
                imageName = "good.jpg";
                break;
            case "C":
                imageName = "fair.jpg";
                break;
            default:
                imageName = "poor.jpg";
                break;
        }
        
//        try {
//            // Try multiple paths to find the image
//            BufferedImage speedometerImage = null;
//            
//            // Try to read from class resources
//            URL resourceUrl = getClass().getResource("/images/" + imageName);
//            if (resourceUrl != null) {
//                speedometerImage = ImageIO.read(resourceUrl);
//            }
//            
//            // Try to read from project root
//            if (speedometerImage == null) {
//                File file = new File("images/" + imageName);
//                if (file.exists()) {
//                    speedometerImage = ImageIO.read(file);
//                }
//            }
//            
//            // Try to read from current directory
//            if (speedometerImage == null) {
//                File file = new File(imageName);
//                if (file.exists()) {
//                    speedometerImage = ImageIO.read(file);
//                }
//            }
//            
//            if (speedometerImage != null) {
//                Image resizedImage = speedometerImage.getScaledInstance(300, 200, Image.SCALE_SMOOTH);
//                speedometerLabel.setIcon(new ImageIcon(resizedImage));
//            } else {
//                speedometerLabel.setText("Image not found: " + imageName);
//                System.out.println("Searched for image in multiple locations but could not find: " + imageName);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            speedometerLabel.setText("Error loading image: " + imageName);
//        }
try {
    BufferedImage speedometerImage = null;

    // Use class loader-based resource loading
//    URL resourceUrl = getClass().getClassLoader().getResource("images/" + imageName);
//    URL resourceUrl = getClass().getResource("images/" + imageName);
URL resourceUrl = getClass().getClassLoader().getResource("gui/images/" + imageName);


    if (resourceUrl != null) {
        speedometerImage = ImageIO.read(resourceUrl);
    }

    // Fallback: try from file system
    if (speedometerImage == null) {
        File file = new File("images/" + imageName);
        if (file.exists()) {
            speedometerImage = ImageIO.read(file);
        }
    }

    if (speedometerImage != null) {
        Image resizedImage = speedometerImage.getScaledInstance(300, 200, Image.SCALE_SMOOTH);
        speedometerLabel.setIcon(new ImageIcon(resizedImage));
    } else {
        speedometerLabel.setText("Image not found: " + imageName);
        System.out.println("Searched for image in multiple locations but could not find: " + imageName);
    }
} catch (IOException e) {
    e.printStackTrace();
    speedometerLabel.setText("Error loading image: " + imageName);
}

        
        // Create a simple custom pie chart
        createSimplePieChart(accuracy);
    }
    
    private void createSimplePieChart(double accuracy) {
        // Get the container for the pie chart
        JPanel pieChartContainer = (JPanel) chartPanel.getComponent(2);
        pieChartContainer.removeAll();
        
        // Create a custom pie chart panel
        JPanel customPieChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int size = Math.min(width, height) - 40;
                int x = (width - size) / 2;
                int y = (height - size) / 2;
                
                // Draw the pie chart
                g2d.setColor(new Color(33, 66, 99)); // Correct questions
                g2d.fillArc(x, y, size, size, 0, (int)(3.6 * accuracy));
                
                g2d.setColor(new Color(132, 189, 201)); // Incorrect questions
                g2d.fillArc(x, y, size, size, (int)(3.6 * accuracy), (int)(3.6 * (100 - accuracy)));
                
                // Draw hole in the center to make it a donut chart
                g2d.setColor(getBackground());
                int innerSize = size / 2;
                g2d.fillOval(x + size/4, y + size/4, innerSize, innerSize);
            }
        };
        customPieChart.setPreferredSize(new Dimension(300, 250));
        customPieChart.setBackground(new Color(200, 228, 226));
        
        // Create percentage labels
        JPanel percentageLabels = new JPanel(new GridLayout(2, 1, 0, 5));
        percentageLabels.setOpaque(false);
        
        JLabel correctLabel = new JLabel("Correct Questions: " + accuracy + "%", SwingConstants.RIGHT);
        JLabel incorrectLabel = new JLabel("Incorrect Questions: " + (100 - accuracy) + "%", SwingConstants.RIGHT);
        
        percentageLabels.add(correctLabel);
        percentageLabels.add(incorrectLabel);
        
        pieChartContainer.setLayout(new BorderLayout());
        pieChartContainer.add(customPieChart, BorderLayout.CENTER);
        pieChartContainer.add(percentageLabels, BorderLayout.SOUTH);
    }
    
    // Main method to test the result page
    public static void main(String[] args) {
        // For testing purpose
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Replace with actual user ID and exam ID
                // Use valid IDs that exist in your database
                new ResultPage("U001", "E001");
            }
        });
    }
}