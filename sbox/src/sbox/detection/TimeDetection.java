/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.detection;

/**
 *
 * @author Álvaro Andrés Maldonado Pinto
 */
public class TimeDetection {

    public TimeDetection() {
    }

    private String startTime = "";
    private String stopTime = "";
    private String time = "";
    private String startFrame = "";
    private String stopFrame = "";

    public String getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(String startFrame) {
        this.startFrame = startFrame;
    }

    public String getStopFrame() {
        return stopFrame;
    }

    public void setStopFrame(String stopFrame) {
        this.stopFrame = stopFrame;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }
    private String frame = "";

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }
    
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
