package com.example.project.model;

public class ElevatorsDetails
{
    private int floor;
    private int idElevator;
    private int shouldStop;
    private int startFloor;

    public int getStartFloor() {
        return startFloor;
    }

    public void setStartFloor(int startFloor) {
        this.startFloor = startFloor;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getIdElevator() {
        return idElevator;
    }

    public void setIdElevator(int idElevator) {
        this.idElevator = idElevator;
    }

    public int getShouldStop() {
        return shouldStop;
    }

    public void setShouldStop(int shouldStop) {
        this.shouldStop = shouldStop;
    }
}





