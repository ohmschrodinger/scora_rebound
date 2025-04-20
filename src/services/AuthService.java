/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;
import java.sql.*;
import model.User;
import model.Examiner;
import model.Student;
import db.DBConnection;
import javax.swing.JOptionPane;


/**
 *
 * @author pranamimishra
 */
public class AuthService {
    
    public User login(String name, String password ) 
    {
        try
        {
         Connection conn=DBConnection.getConnection();
         String str="select userID from user where name=? and password=?";
         PreparedStatement ps=conn.prepareStatement(str);
         ps.setString(1,name);
         ps.setString(2,password);
         ResultSet rs=ps.executeQuery();
          
         if (rs.next())
            return new User(rs.getString("userId"),name,password) ; //returning the user object for given user
         else 
             return null;// login failed
        }
       catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        return null;
    }
        catch(ClassNotFoundException e)
        {
            JOptionPane.showMessageDialog(null,
    "Database driver not found. Please check your project setup.",
    "Driver Error",
    JOptionPane.ERROR_MESSAGE);
          return null;
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
        
        if (generatedId!=null)
        {
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
    catch(ClassNotFoundException e)
        {
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

        cs.execute(); //calling registerExamindr procedure from backend

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
    catch(ClassNotFoundException e)
        {
            JOptionPane.showMessageDialog(null,
    "Database driver not found. Please check your project setup.",
    "Driver Error",
    JOptionPane.ERROR_MESSAGE);
          return null;
        }
}
     
    
}
