/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author pranamimishra
 */
public class User {
    private String userId;
    private String name;
    private String password;
    
    //constructor
    public User(String userId, String name, String password)
    {
        this.name=name;
        this.password=password;
        this.userId=userId;
    }
    //getter methods
    public String getUserId(){ return this.userId;}
    public String getName(){ return this.name;}
    public String getPassword(){ return this.password;}
    
    //setter methods
    public void setUserId(String userId){this.userId=userId;}
    public void setName(String name){this.name=name;}
    public void setPassword(String password){this.password=password;}
    
        
    }
    

