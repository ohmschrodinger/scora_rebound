/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author pranamimishra
 */
public class Examiner extends User {
    private String department;
    //constructor
    public Examiner(String userId, String name, String password,String department)
    {
        super(userId,name,password);
        this.department=department;
        
    }
    //getter method
    public String getDepartment(){ return this.department; }
    
    //setter method
    
    public void setDepartment(String department){ this.department=department;} 
    
}
