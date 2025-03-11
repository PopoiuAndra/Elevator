package com.example.project.model;

public class Client {
    int id;
    String name;
    int currentFloor;
    int idElevator;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getIdElevator() {
        return idElevator;
    }

    public void setIdElevator(int idElevator) {
        this.idElevator = idElevator;
    }
}
