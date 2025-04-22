package services;
import java.sql.*;
import model.User;
import model.Examiner;
import model.Student;
import db.DBConnection;
import javax.swing.JOptionPane;

public class AuthService {
    
    public User login(String userId, String password) {
        try {
            Connection conn = DBConnection.getConnection();
            String str = "select name from user where userId=? and password=?";
            PreparedStatement ps = conn.prepareStatement(str);
            ps.setString(1, userId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
              
            if (rs.next())
                return new User(userId, rs.getString("name"), password);
            else 
                return null; // login failed
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return null;
        }
        catch(ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Database driver not found. Please check your project setup.",
                "Driver Error",
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    // Check if a user is a Student based on userId
    public boolean isStudent(String userId) {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT 1 FROM Student WHERE userId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            return rs.next(); // Returns true if userId exists in Student table
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Database driver not found. Please check your project setup.",
                "Driver Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Check if a user is an Examiner based on userId
    public boolean isExaminer(String userId) {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT 1 FROM Examiner WHERE userId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            return rs.next(); // Returns true if userId exists in Examiner table
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Database driver not found. Please check your project setup.",
                "Driver Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // registering student
    public Student registerStudent(Student studentWithoutId) {
        try {
            Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall("{ call registerStudent(?, ?, ?, ?, ?) }");

            cs.setString(1, studentWithoutId.getName());
            cs.setString(2, studentWithoutId.getPassword());
            cs.setString(3, studentWithoutId.getCourse());
            cs.setInt(4, studentWithoutId.getAcademicYear());
            cs.registerOutParameter(5, Types.VARCHAR); // OUT userId

            cs.execute(); //calling registerStudennt procedure from backend

            String generatedId = cs.getString(5);
            
            if (generatedId!=null) {
                // Return a complete Student object
                return new Student(generatedId,
                               studentWithoutId.getName(),
                               studentWithoutId.getPassword(),
                               studentWithoutId.getCourse(),
                               studentWithoutId.getAcademicYear());
            }
            else 
                return null;

        } catch (SQLException e) {
           
            JOptionPane.showMessageDialog(null, "Registration failed:\n" + e.getMessage());
            return null;
        }
        catch(ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Database driver not found. Please check your project setup.",
                "Driver Error",
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // registering examiner
    public Examiner registerExaminer(Examiner examinerWithoutId) {
        try {
            Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall("{ call registerExaminer(?, ?, ?, ?) }");

            cs.setString(1, examinerWithoutId.getName());
            cs.setString(2, examinerWithoutId.getPassword());
            cs.setString(3, examinerWithoutId.getDepartment());
            cs.registerOutParameter(4, Types.VARCHAR); // OUT userId

            cs.execute(); //calling registerExaminer procedure from backend

            String generatedId = cs.getString(4);
            if (generatedId!=null) {
                // Return a complete examinerr object
                return new Examiner(generatedId,
                               examinerWithoutId.getName(),
                               examinerWithoutId.getPassword(),
                               examinerWithoutId.getDepartment());
            }
            else
                return null;

        } catch (SQLException e) {
           
            JOptionPane.showMessageDialog(null, "Registration failed:\n" + e.getMessage());
            return null;
        }
        catch(ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Database driver not found. Please check your project setup.",
                "Driver Error",
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}