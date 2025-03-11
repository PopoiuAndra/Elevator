package com.example.project.config;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "student";
    private static final String PASSWORD = "STUDENT";
    private static Connection connection = null;


    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:xe");
        dataSource.setUsername("student");
        dataSource.setPassword("STUDENT");
        return dataSource;
    }


    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    public Database() {
        createConnection();
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void createConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);

            createElevatorsTable();
            createElevatorsDetailsTable();

        } catch (SQLException e) {
            System.out.println("Connection failure.");
            System.err.println(e);
        }
    }

    public static void createElevatorsTable() throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE elevators");
            statement.execute("CREATE TABLE elevators (id NUMBER PRIMARY KEY, name VARCHAR(255), direction VARCHAR(255), currentFloor NUMBER)");
        } catch (SQLException e) {
            System.out.println("Table elevators already exists");
        }
    }

    public static void createElevatorsDetailsTable() throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE elevatorsDetails");
            statement.execute("CREATE TABLE elevatorsDetails (floor NUMBER, shouldStop NUMBER, idElevator NUMBER)");
        } catch (SQLException e) {
            System.out.println("Table elevatorsDetails already exists");
        }
    }


    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}