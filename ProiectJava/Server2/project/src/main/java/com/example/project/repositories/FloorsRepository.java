package com.example.project.repositories;

import com.example.project.model.Floors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FloorsRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Floors> floorsRowMapper = (resultSet, rowNum) -> {
        Floors floors = new Floors();
        floors.setIdElevator(resultSet.getInt("idElevator"));
        floors.setParter(resultSet.getString("parter"));
        floors.setEtaj1(resultSet.getString("etaj1"));
        floors.setEtaj2(resultSet.getString("etaj2"));
        floors.setEtaj3(resultSet.getString("etaj3"));
        floors.setEtaj4(resultSet.getString("etaj4"));
        floors.setEtaj5(resultSet.getString("etaj5"));
        return floors;
    };
    public Floors getFloorsDetails(int id) {
        return jdbcTemplate.query("SELECT * FROM floors WHERE id = ?", floorsRowMapper, id).get(0);
    }
}
