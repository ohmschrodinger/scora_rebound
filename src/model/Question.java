/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class Question {
    private String questionID;
    private String category;
    private String level;
    private String mcq;  // 'Y' or 'N'
    private String fib;  // 'Y' or 'N'
    private String text;
    private int marks;
     //constructor
    public Question(String questionID, String category, String level, String mcq, String fib, String text, int marks) {
        this.questionID = questionID;
        this.category = category;
        this.level = level;
        this.mcq = mcq;
        this.fib = fib;
        this.text = text;
        this.marks = marks;
    }
    //getter method
    public String getQuestionID() { return questionID; }
    public String getCategory() { return category; }
    public String getLevel() { return level; }
    public String getMcq() { return mcq; }
    public String getFib() { return fib; }
    public String getText() { return text; }
    public int getMarks() { return marks; }
    //setter method
    
    
    public void setQuestionID(String questionID) { this.questionID = questionID; }
    public void setCategory(String category) { this.category = category; }
    public void setLevel(String level) { this.level = level; }
    public void setMcq(String mcq) { this.mcq = mcq; }
    public void setFib(String fib) { this.fib = fib; }
    public void setText(String text) { this.text = text; }
    public void setMarks(int marks) { this.marks = marks; }
}
