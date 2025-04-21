/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author pranamimishra
 */
public class Option {
    
    private String optionId;
    private String text;
    private String questionId;
    private char is_correct;
    
    //constructor
    
    public Option(String optionId,String text,char is_correct,String questionId)
    {
      this.optionId=optionId;
      this.text=text;
      this.is_correct=is_correct;
      this.questionId=questionId;        
    }
    
    //getter methods
    public String getOptionId(){ return this.optionId;  }
    public String getText(){ return this.text;  }
    public String getQuestionId(){ return this.questionId;  }
    public char is_correct() { return this.is_correct; }
    
    //setter method
    public void getOptionId(String optionId){ this.optionId=optionId;  }
    public void setText(String text){  this.text=text;  }
    public void getQuestionId(String questionId ){ this.questionId=questionId;  }
    public void is_correct(char is_correct) { this.is_correct=is_correct; }
    
}
