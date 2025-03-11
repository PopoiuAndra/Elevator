package com.example.project.repositories;

import com.example.project.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Client> clientRowMapper = (resultSet, rowNum) -> {
        Client client = new Client();
        client.setId(resultSet.getInt("id"));
        client.setName(resultSet.getString("name"));
        client.setCurrentFloor(resultSet.getInt("currentFloor"));
        client.setIdElevator(resultSet.getInt("idElevator"));
        return client;
    };

    public Client addClient(Client client) {
        jdbcTemplate.update("INSERT INTO clients (id, name, currentFloor, idElevator) VALUES (?, ?, ?, ?)",
                client.getId(), client.getName(), client.getCurrentFloor(), client.getIdElevator());
        return client;
    }

    public Client findById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM clients WHERE id = ?", clientRowMapper, id);
    }

    public List<Client> findAll() {
        return jdbcTemplate.query("SELECT * FROM clients", clientRowMapper);
    }

    public void deleteClient(int id) {
        jdbcTemplate.update("DELETE FROM clients WHERE id = ?", id);
    }
}
