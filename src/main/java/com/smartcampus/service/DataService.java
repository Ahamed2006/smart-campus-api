package com.smartcampus.service;

import com.smartcampus.models.Reading;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataService {
    private static final DataService instance = new DataService();
    private final List<Room> rooms = new CopyOnWriteArrayList<>();
    private final List<Sensor> sensors = new CopyOnWriteArrayList<>();
    private final List<Reading> readings = new CopyOnWriteArrayList<>();
    private int currentReadingId = 1;

    private DataService() {}

    public static DataService getInstance() {
        return instance;
    }

    // --- ROOM METHODS ---
    public List<Room> getRooms() { return rooms; }
    
    public void addRoom(Room room) {
        rooms.add(room);
    }

    public Room getRoomById(String id) {
        if (id == null) return null;
        return rooms.stream()
                .filter(room -> id.equals(room.getId()))
                .findFirst()
                .orElse(null);
    }

    // --- SENSOR METHODS ---
    public List<Sensor> getSensors() { return sensors; }
    
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public Sensor getSensorById(String id) {
        if (id == null) return null;
        return sensors.stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst()
                .orElse(null);
    }

    // --- READING METHODS ---
    public List<Reading> getReadings() { return readings; }

    public synchronized void addReading(Reading reading) {
        reading.setId(currentReadingId++);
        readings.add(reading);
    }
}