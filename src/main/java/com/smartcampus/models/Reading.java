package com.smartcampus.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Reading {
    private int id;
    private String sensorId;
    private double value;
    private String timestamp;

    public Reading() {}
    public Reading(int id, String sensorId, double value, String timestamp) {
        this.id = id;
        this.sensorId = sensorId;
        this.value = value;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}