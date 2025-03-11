package com.example.project.controller;

import com.example.project.model.Elevators;
import com.example.project.model.ElevatorsDetails;
import com.example.project.repositories.ElevatorDetailsRepository;
import com.example.project.repositories.ElevatorRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elevator")
@Api(value = "Book Management System", description = "Operations pertaining to books in Book Management System")
public class ApiController {

    @Autowired
    private ElevatorRepository elevatorRepository;
    @Autowired
    private ElevatorDetailsRepository elevatorDetailsRepository;

    @GetMapping("/api/data")
    @ApiOperation(value = "Try it out")
    public String getData() {
        return "Hello from the server!";
    }

    @GetMapping("/{elevatorId}/{currentFloor}")
    @ApiOperation(value = "View if the elevator should stop at the current floor")
    public ElevatorsDetails getElevatorStatus(@PathVariable int elevatorId, @PathVariable int currentFloor) {
        return elevatorDetailsRepository.findByIdAndFloor(elevatorId, currentFloor);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get an elevator by its ID (mainly to see it's floor and direction)")
    public Elevators getElevator(@PathVariable int id) {
        return elevatorRepository.findById(id);
    }

    @GetMapping("/list")
    @ApiOperation(value = "View a list of all elevators")
    public List<Elevators> getElevatorList() {
        return elevatorRepository.findAll();
    }

    @PostMapping("/add")
    @ApiOperation(value = "Add a new elevator")
    public Elevators addElevator(@RequestBody Elevators elevator) {
        return elevatorRepository.addElevator(elevator);
    }

    @PutMapping("/update/{idElevator}/{floor}")
    @ApiOperation(value = "Add in the database the fact that the elevator should stop at the floor")
    public void addElevatorDetails(@PathVariable int idElevator, @PathVariable int floor) {
        elevatorDetailsRepository.addElevatorStop(idElevator, floor);
    }

    @GetMapping("/direction/{floor}/{direction}")
    @ApiOperation(value = "Get the closest elevator to the floor")
    public int firstElevatorToCome(@PathVariable int floor, @PathVariable String direction) {
        return elevatorRepository.seeWhichElevatorComeFirst(floor, direction);
    }

    @GetMapping("/direction/{id}")
    @ApiOperation(value = "Get the elevators direction")
    public String elevatorDirection(@PathVariable int id) {
        return elevatorRepository.findById(id).getDirection();
    }

    @GetMapping("/floor/{id}")
    @ApiOperation(value = "Get the current floor of the elevator")
    public int getFloor(@PathVariable int id) {
        return elevatorRepository.findById(id).getCurrentFloor();
    }

    @GetMapping("/shouldStop/{id}/{floor}")
    @ApiOperation(value = "See if an elevator should stop at a floor")
    public int getIfAnElevatorShouldStopAtAFloor(@PathVariable int id, @PathVariable int floor) {
        return elevatorDetailsRepository.findByIdAndFloor(id, floor).getShouldStop();
    }

    @PutMapping("/update/dontStop/{idElevator}/{floor}")
    @ApiOperation(value = "Add in the database the fact that the elevator should not stop at the floor")
    public void dontStopAtFloor(@PathVariable int idElevator, @PathVariable int floor) {
        elevatorDetailsRepository.deleteElevatorStop(idElevator, floor);
    }

    @PutMapping("/update/floor/{id}/{floor}")
    @ApiOperation(value = "Update the floor of the elevator")
    public void updateFloor(@PathVariable int id, @PathVariable int floor) {
        elevatorRepository.updateFloor(id, floor);
    }

    @PutMapping("/update/direction/{id}/{direction}")
    @ApiOperation(value = "Update the direction of the elevator")
    public void updateDirection(@PathVariable int id, @PathVariable String direction) {
        elevatorRepository.updateDirection(id, direction);
    }

    @GetMapping("/countStops/{id}")
    @ApiOperation(value = "Count the number of stops of an elevator at the current time")
    public int countStops(@PathVariable int id) {
        return elevatorDetailsRepository.countStops(id);
    }

    @GetMapping("/randomStop/{id}")
    @ApiOperation(value = "Get a stop of an elevator")
    public int randomStop(@PathVariable int id) {
        return elevatorDetailsRepository.randomStop(id);
    }
}