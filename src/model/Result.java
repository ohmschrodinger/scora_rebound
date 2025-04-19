/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class Result {
    private String resultID;
    private String grade;
    private int score;
    private String examID;
    private String userID;
//constructor
    public Result(String resultID, String grade, int score, String examID, String userID) {
        this.resultID = resultID;
        this.grade = grade;
        this.score = score;
        this.examID = examID;
        this.userID = userID;
    }
   //getter methods
    public String getResultID() { return resultID; }
    public String getGrade() { return grade; }
    public int getScore() { return score; }
    public String getExamID() { return examID; }
    public String getUserID() { return userID; }

    
    //setter methods
    public void setResultID(String resultID) { this.resultID = resultID; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setScore(int score) { this.score = score; }
    public void setExamID(String examID) { this.examID = examID; }
    public void setUserID(String userID) { this.userID = userID; }
}
