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
}