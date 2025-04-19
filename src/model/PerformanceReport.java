/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package model;

public class PerformanceReport {
    private String reportID;
    private int ranks;
    private String accuracy;
    private String resultID;
    //constructor
    public PerformanceReport(String reportID, int ranks, String accuracy, String resultID) {
        this.reportID = reportID;
        this.ranks = ranks;
        this.accuracy = accuracy;
        this.resultID = resultID;
    }

    //getter methods
    public String getReportID() { return reportID; }
    public int getRanks() { return ranks; }
    public String getAccuracy() { return accuracy; }
    public String getResultID() { return resultID; }

    //setter methods
    public void setReportID(String reportID) { this.reportID = reportID; }
    public void setRanks(int ranks) { this.ranks = ranks; }
    public void setAccuracy(String accuracy) { this.accuracy = accuracy; }
    public void setResultID(String resultID) { this.resultID = resultID; }
}
