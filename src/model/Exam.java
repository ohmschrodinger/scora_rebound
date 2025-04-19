/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author pranamimishra
 */
public class Exam {
    private String examID;
    private String subject;
    private int totalMarks;
    private String userID;
    private String duration;
    private String status;

    // Constructor
    public Exam(String examID,int totalMarks,String duration ,String subject,String status, String userID) {
        this.examID = examID;
        this.totalMarks = totalMarks;
        this.duration=duration;
        this.subject = subject;
        this.status=status;
        this.userID = userID;
    }

    // Getters
    public String getExamID() { return examID; }
    public String getSubject() { return subject; }
    public int getTotalMarks() { return totalMarks; }
    public String getExaminerID() { return userID; }
    public String getDuration() { return duration; }
    public String status() { return status; }

    // Setters
    public void setExamID(String examID) { this.examID = examID; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }
    public void setExaminerID(String examinerID) { this.userID = userID; }
     public void getDuration(String duration) { this.duration=duration; }
    public void status(String status) { this.status=status; }
}
