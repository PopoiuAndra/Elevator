package com.example.project.repositories;

import com.example.project.model.Elevators;
import com.example.project.model.ElevatorsDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ElevatorRepository {
    private static final Logger log = LoggerFactory.getLogger(ElevatorRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Elevators> elevatorsRowMapper = (resultSet, rowNum) -> {
        Elevators elevators = new Elevators();
        elevators.setId(resultSet.getInt("id"));
        elevators.setName(resultSet.getString("name"));
        elevators.setDirection(resultSet.getString("direction"));
        elevators.setCurrentFloor(resultSet.getInt("currentFloor"));
        return elevators;
    };

    public Elevators findById(int id) {
        return jdbcTemplate.query("SELECT * FROM elevators WHERE id = ?", elevatorsRowMapper, id).get(0);
    }

    public List<Elevators> findAll() {
        return jdbcTemplate.query("SELECT * FROM elevators", elevatorsRowMapper);
    }

    public Elevators addElevator(Elevators elevators) {
        jdbcTemplate.update("INSERT INTO elevators (id, name, currentFloor, direction) VALUES (?, ?, ?, ?)",
                elevators.getId(), elevators.getName(), elevators.getCurrentFloor(), elevators.getDirection());
        return elevators;
    }

    public int seeWhichElevatorComeFirst(int floor, String direction) {
        List<Elevators> allElevators = jdbcTemplate.query("SELECT * FROM elevators", elevatorsRowMapper);


        Elevators closestElevator = allElevators.get(0);
        int minDistance = Integer.MAX_VALUE;
        for(var elevator: allElevators)
        {
            switch (elevator.getDirection()){
                case "UP":
                    if (elevator.getCurrentFloor() <= floor && minDistance >  floor - elevator.getCurrentFloor()) {
                        closestElevator = elevator;
                        minDistance = floor - elevator.getCurrentFloor();
                    }
                    else if (minDistance > (5 - elevator.getCurrentFloor()) + (5 - floor)) {
                        closestElevator = elevator;
                        minDistance = (5 - elevator.getCurrentFloor()) + (5 - floor);
                    }
                    break;
                case "DOWN":
                    if (elevator.getCurrentFloor() >= floor && minDistance > elevator.getCurrentFloor() - floor) {
                        closestElevator = elevator;
                        minDistance = elevator.getCurrentFloor() - floor;
                    }
                    else if (minDistance > elevator.getCurrentFloor() + floor) {
                        closestElevator = elevator;
                        minDistance = elevator.getCurrentFloor() + floor;
                    }
                default:
                    if(minDistance > Math.abs(elevator.getCurrentFloor() - floor))
                    {
                        closestElevator = elevator;
                        minDistance = Math.abs(elevator.getCurrentFloor() - floor);
                    }

            }
        }

        return closestElevator.getId();
    }

    public void updateFloor(int id, int floor) {
        jdbcTemplate.update("UPDATE elevators SET currentFloor = ? WHERE id = ?", floor, id);
    }

    public void updateDirection(int id, String direction) {
        jdbcTemplate.update("UPDATE elevators SET direction = ? WHERE id = ?", direction, id);
    }

}

