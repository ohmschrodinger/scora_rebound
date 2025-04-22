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

        // Check if a result already exists
        String checkQuery = "SELECT * FROM Result WHERE UserID = ? AND ExamID = ? ORDER BY ResultID DESC LIMIT 1";
        PreparedStatement psCheck = conn.prepareStatement(checkQuery);
        psCheck.setString(1, userID);
        psCheck.setString(2, examID);
        ResultSet rsCheck = psCheck.executeQuery();

        if (rsCheck.next()) {
            // Return existing result
            result = new Result(
                    rsCheck.getString("ResultID"),
                    rsCheck.getString("Grade"),
                    rsCheck.getInt("Score"),
                    rsCheck.getString("ExamID"),
                    rsCheck.getString("UserID")
            );
            rsCheck.close();
            psCheck.close();
            return result;
        }

        rsCheck.close();
        psCheck.close();

        // If not exists, call the procedure to generate result
        CallableStatement cs = conn.prepareCall("{? = call generateResult(?, ?)}");
        cs.registerOutParameter(1, Types.VARCHAR);
        cs.setString(2, userID);
        cs.setString(3, examID);
        cs.execute();

        // Fetch newly generated result
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

        // Check if report already exists
        String checkQuery = "SELECT * FROM PerformanceReport WHERE ResultID = ?";
        PreparedStatement psCheck = conn.prepareStatement(checkQuery);
        psCheck.setString(1, resultID);
        ResultSet rsCheck = psCheck.executeQuery();

        if (rsCheck.next()) {
            // Return existing report
            report = new PerformanceReport(
                    rsCheck.getString("ReportID"),
                    rsCheck.getInt("Ranks"),
                    rsCheck.getString("Accuracy"),
                    rsCheck.getString("ResultID")
            );
            rsCheck.close();
            psCheck.close();
            return report;
        }

        rsCheck.close();
        psCheck.close();

        // If not exists, call the procedure to generate report
        CallableStatement cs = conn.prepareCall("{? = call generatePerformanceReport(?)}");
        cs.registerOutParameter(1, Types.VARCHAR);
        cs.setString(2, resultID);
        cs.execute();

        // Fetch generated report
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
