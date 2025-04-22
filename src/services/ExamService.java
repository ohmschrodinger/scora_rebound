/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */package services;

import db.DBConnection;
import model.Exam;
import model.Option;
import model.Question;

import java.sql.*;
import java.util.*;

public class ExamService {

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DBConnection.getConnection();
    }

    public boolean createExam(Exam exam) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Exam (ExamID, Subject, TotalMarks, Duration, Status, UserID) VALUES (?, ?, ?, ?, ?, ?)")) {

            String getMaxIdSql = "SELECT MAX(CAST(SUBSTRING(ExamID, 2) AS UNSIGNED)) AS maxId FROM Exam";
            ResultSet rs = stmt.executeQuery(getMaxIdSql);
            String newExamId = "E001";
            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                newExamId = String.format("E%03d", maxId + 1);
            }
            rs.close();

            ps.setString(1, newExamId);
            ps.setString(2, exam.getSubject());
            ps.setInt(3, exam.getTotalMarks());
            ps.setString(4, exam.getDuration());
            ps.setString(5, exam.getStatus());
            ps.setString(6, exam.getUserID());
            ps.executeUpdate();

            exam.setExamID(newExamId);
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Exam getExamById(String examId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Exam WHERE ExamID = ?")) {
            ps.setString(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Exam(
                        rs.getString("ExamID"),
                        rs.getInt("TotalMarks"),
                        rs.getString("Duration"),
                        rs.getString("Subject"),
                        rs.getString("Status"),
                        rs.getString("UserID")
                );
            }
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Exam> getAllExamsForUser(String userId) {
        List<Exam> exams = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Exam WHERE UserID = ?")) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                exams.add(new Exam(
                        rs.getString("ExamID"),
                        rs.getInt("TotalMarks"),
                        rs.getString("Duration"),
                        rs.getString("Subject"),
                        rs.getString("Status"),
                        rs.getString("UserID")
                ));
            }
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return exams;
    }

    public List<Exam> getUpcomingExams(String userId) {
        List<Exam> upcomingExams = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT e.* FROM Exam e JOIN ExamSchedule s ON e.ExamID = s.ExamID WHERE e.UserID = ? AND s.Date >= CURDATE()")) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                upcomingExams.add(new Exam(
                        rs.getString("ExamID"),
                        rs.getInt("TotalMarks"),
                        rs.getString("Duration"),
                        rs.getString("Subject"),
                        rs.getString("Status"),
                        rs.getString("UserID")
                ));
            }
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return upcomingExams;
    }

    public boolean autoSubmitExam(String examId, String userId) {
        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall("CALL autoSubmitExam(?, ?)")) {
            cs.setString(1, examId);
            cs.setString(2, userId);
            cs.execute();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<Question, List<Option>> getExamWithQuestions(String examId) {
        Map<Question, List<Option>> map = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement questionPs = conn.prepareStatement("SELECT * FROM Question WHERE QuestionID IN (SELECT QuestionID FROM Contains WHERE ExamID = ?)");) {
            questionPs.setString(1, examId);
            ResultSet questionRs = questionPs.executeQuery();
            while (questionRs.next()) {
                Question question = new Question(
                        questionRs.getString("QuestionID"),
                        questionRs.getString("Category"),
                        questionRs.getString("Level"),
                        questionRs.getString("MCQ"),
                        questionRs.getString("FIB"),
                        questionRs.getString("Text"),
                        questionRs.getInt("Marks")
                );
                List<Option> options = getOptionsForQuestion(question.getQuestionID(), conn);
                map.put(question, options);
            }
            questionRs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    private List<Option> getOptionsForQuestion(String questionId, Connection conn) throws SQLException {
        List<Option> options = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Option_table WHERE QuestionID = ?")) {
            stmt.setString(1, questionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                options.add(new Option(
                        rs.getString("OptionID"),
                        rs.getString("Text"),
                        rs.getString("Is_Correct").charAt(0),
                        rs.getString("QuestionID")
                ));
            }
            rs.close();
        }
        return options;
    }
    
   public boolean addQuestionAndOptions(String examId, Question question, List<Option> options,String userId) {
    Connection conn = null;

    try {
        conn = getConnection();
        conn.setAutoCommit(false);

        Statement stmt = conn.createStatement();

        // Step 0: Generate custom QuestionID
        String getMaxQidSql = "SELECT MAX(CAST(SUBSTRING(QuestionID, 2) AS UNSIGNED)) AS maxQid FROM Question";
        ResultSet rsQ = stmt.executeQuery(getMaxQidSql);
        String newQuestionId = "Q001";
        if (rsQ.next()) {
            int maxQid = rsQ.getInt("maxQid");
            newQuestionId = String.format("Q%03d", maxQid + 1);
        }
        rsQ.close();
        question.setQuestionID(newQuestionId); // Set the generated QuestionID

        // Step 1: Insert into Question table
        String insertQuestionSql = "INSERT INTO Question (QuestionID, Category, Level, MCQ, FIB, Text, Marks) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertQuestionSql)) {
            ps.setString(1, question.getQuestionID());
            ps.setString(2, question.getCategory());
            ps.setString(3, question.getLevel());
            ps.setString(4, question.getMcq());
            ps.setString(5, question.getFib());
            ps.setString(6, question.getText());
            ps.setInt(7, question.getMarks());
            ps.executeUpdate();
        }
        
        // Generate custom ManageID like M001
String getMaxManageID = "SELECT MAX(ManageID) FROM manages";
PreparedStatement psManageID = conn.prepareStatement(getMaxManageID);
ResultSet rsManage = psManageID.executeQuery();

String newManageID = "M001"; // default
if (rsManage.next() && rsManage.getString(1) != null) {
    String lastID = rsManage.getString(1); // e.g., "M012"
    int num = Integer.parseInt(lastID.substring(1)) + 1;
    newManageID = String.format("M%03d", num); // e.g., M013
}
rsManage.close();
psManageID.close();

// Insert into manages table
String insertManage = "INSERT INTO manages (ManageID, QuestionID, UserID) VALUES (?, ?, ?)";
PreparedStatement psManage = conn.prepareStatement(insertManage);
psManage.setString(1, newManageID);
psManage.setString(2, newQuestionId);
psManage.setString(3, userId); // make sure examinerID is accessible in this scope
psManage.executeUpdate();
psManage.close();


        // Step 2: Insert Options with custom OptionIDs
        String getMaxOpIdSql = "SELECT MAX(CAST(SUBSTRING(OptionID, 3) AS UNSIGNED)) AS maxOpId FROM Option_table";
        ResultSet rsOp = stmt.executeQuery(getMaxOpIdSql);
        int baseOptionId = 0;
        if (rsOp.next()) {
            baseOptionId = rsOp.getInt("maxOpId");
        }
        rsOp.close();

        String insertOptionSql = "INSERT INTO Option_table (OptionID, Text, Is_Correct, QuestionID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertOptionSql)) {
            for (int i = 0; i < options.size(); i++) {
                Option option = options.get(i);
                String newOptionId = String.format("OP%03d", baseOptionId + i + 1);
                option.setOptionId(newOptionId); // Set generated OptionID

                ps.setString(1, option.getOptionId());
                ps.setString(2, option.getText());
                ps.setString(3, String.valueOf(option.getIsCorrect()));
                ps.setString(4, question.getQuestionID());
                ps.addBatch();
            }
            ps.executeBatch();
        }

        // Step 3: Link question with exam in Contains table
// Step 1: Generate custom ContainID like C001, C002, etc.
String getMaxContainIdSql = "SELECT MAX(ContainID) FROM Contains";
String newContainId = "C001";

try (PreparedStatement psMax = conn.prepareStatement(getMaxContainIdSql);
     ResultSet rs = psMax.executeQuery()) {

    if (rs.next() && rs.getString(1) != null) {
        String maxId = rs.getString(1);  // e.g., C007
        int numericPart = Integer.parseInt(maxId.substring(1)); // get 7
        numericPart++;
        newContainId = "C" + String.format("%03d", numericPart); // C008
    }
}

// Step 2: Insert using new ContainID
String insertContainsSql = "INSERT INTO Contains (ContainID, ExamID, QuestionID) VALUES (?, ?, ?)";

try (PreparedStatement ps = conn.prepareStatement(insertContainsSql)) {
    ps.setString(1, newContainId);
    ps.setString(2, examId);
    ps.setString(3, question.getQuestionID());
    ps.executeUpdate();
}


        conn.commit();
        return true;

    } catch (SQLException | ClassNotFoundException e) {
        e.printStackTrace();
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return false;
    } finally {
        try {
            if (conn != null) conn.setAutoCommit(true);
            if (conn != null) conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    
    
    
}
  
   public boolean examExists(String examId) {
    String sql = "SELECT COUNT(*) FROM Exam WHERE ExamID = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, examId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException | ClassNotFoundException e) {
        e.printStackTrace();
    }
    return false;
}

    public boolean deleteExam(String examId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Exam WHERE ExamID = ?");
            ps.setString(1, examId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

    

    
