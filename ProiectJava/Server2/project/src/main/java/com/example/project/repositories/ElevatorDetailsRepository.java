package com.example.project.repositories;

import com.example.project.model.Elevators;
import com.example.project.model.ElevatorsDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ElevatorDetailsRepository {

    private static final Logger log = LoggerFactory.getLogger(ElevatorDetailsRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<ElevatorsDetails> elevatorsRowMapper = (resultSet, rowNum) ->{
        ElevatorsDetails elevators = new ElevatorsDetails();
        elevators.setFloor(resultSet.getInt("floor"));;
        elevators.setStartFloor(resultSet.getInt("startFloor"));
        elevators.setShouldStop(resultSet.getInt("shouldStop"));
        elevators.setIdElevator(resultSet.getInt("idElevator"));
        return elevators;
    };

    public ElevatorsDetails findByIdAndFloor(int idElevator, int floor) {
        return jdbcTemplate.query("SELECT * FROM elevatorsDetails WHERE idElevator = ? AND floor = ?", elevatorsRowMapper, idElevator, floor).get(0);
    }

    public void addElevatorStop(int idElevator, int floor) {

        jdbcTemplate.update("UPDATE elevatorsDetails SET shouldStop = ? WHERE idElevator = ? AND floor = ?", 1, idElevator, floor);
    }

    public void deleteElevatorStop(int idElevator, int floor) {

        jdbcTemplate.update("UPDATE elevatorsDetails SET shouldStop = ? WHERE idElevator = ? AND floor = ?", 0, idElevator, floor);
    }

    public int countStops(int idElevator) {
        return jdbcTemplate.query("SELECT * FROM elevatorsDetails WHERE idElevator = ? AND shouldStop = 1", elevatorsRowMapper, idElevator).size();
    }

    public int randomStop(int idElevator) {
        return jdbcTemplate.query("SELECT * FROM elevatorsDetails WHERE idElevator = ? AND shouldStop = 1", elevatorsRowMapper, idElevator).get(0).getFloor();
    }

    public int getStartFloor(int id) {
        return jdbcTemplate.query("SELECT * FROM elevatorsDetails WHERE idElevator = ?", elevatorsRowMapper, id).get(0).getStartFloor();
    }
}
