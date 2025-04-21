/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import db.DBConnection;

import java.sql.*;
import java.util.Map;

public class ResponseService {

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DBConnection.getConnection();
    }

    // Submit all responses for a given exam by a student
    public boolean submitAllResponses(String userId, String examId, Map<String, String> questionOptionMap) {
        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall("CALL addResponse(?, ?, ?)")) {
               System.out.println("DEBUG: Submitting response for UserID: " + userId);

            for (String questionId : questionOptionMap.keySet()) {
                String selectedOptionId = questionOptionMap.get(questionId);
                cs.setString(1, selectedOptionId);
                cs.setString(2, examId);
                cs.setString(3, userId);
                cs.execute();
            }
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if the user has already attempted the exam
    public boolean hasAttempted(String userId, String examId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Attempt WHERE UserID = ? AND ExamID = ?")) {

            ps.setString(1, userId);
            ps.setString(2, examId);

            ResultSet rs = ps.executeQuery();
            boolean attempted = rs.next();
            rs.close();
            return attempted;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}