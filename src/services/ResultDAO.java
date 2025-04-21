/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;
import db.DBConnection;
import model.Result;
import model.PerformanceReport;
import java.sql.*;
/**
 *
 * @author omdha
 */
public class ResultDAO {
    public Result generateAndGetResult(String userID, String examID) {
        Result result = null;
        try {
            Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall("{? = call generateResult(?, ?)}");
            cs.registerOutParameter(1, Types.VARCHAR);
            cs.setString(2, userID);
            cs.setString(3, examID);
            cs.execute();

            // Fetch the latest result for this user & exam
            String query = "SELECT * FROM Result WHERE UserID = ? AND ExamID = ? ORDER BY ResultID DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userID);
            ps.setString(2, examID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                result = new Result(
                        rs.getString("ResultID"),
                        rs.getString("Grade"),
                        rs.getInt("Score"),
                        rs.getString("ExamID"),
                        rs.getString("UserID")
                );
            }

            rs.close();
            ps.close();
            cs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public PerformanceReport generateAndGetPerformanceReport(String resultID) {
        PerformanceReport report = null;
        try {
            Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall("{? = call generatePerformanceReport(?)}");
            cs.registerOutParameter(1, Types.VARCHAR);
            cs.setString(2, resultID);
            cs.execute();

            // Fetch report by ID
            String query = "SELECT * FROM PerformanceReport WHERE ResultID = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, resultID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                report = new PerformanceReport(
                        rs.getString("ReportID"),
                        rs.getInt("Ranks"),
                        rs.getString("Accuracy"),
                        rs.getString("ResultID")
                );
            }

            rs.close();
            ps.close();
            cs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return report;
    }
    
}
