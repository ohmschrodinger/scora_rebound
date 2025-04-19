/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author pranamimishra
 */
public class Student extends User {
    
    private String course;
    private int academicYear;
    //constructor
    public Student(String userId, String name, String password,String course,int academicYear)
    {
        super(userId, name, password);
        this.course=course;
        this.academicYear=academicYear;
    }
    //getter methods
    
    public String getCourse(){return this.course;}
    public int getAcademicYear(){return this.academicYear;}
    
    //setter methods
    public void getCourse(String course){ this.course=course;}
    public void getAcademicYear(int academicYear){ this.academicYear=academicYear;}
    
    
}
