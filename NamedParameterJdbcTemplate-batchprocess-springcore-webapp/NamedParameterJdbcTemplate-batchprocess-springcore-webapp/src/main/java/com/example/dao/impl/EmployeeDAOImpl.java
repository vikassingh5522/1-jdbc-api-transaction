 package com.example.dao.impl;

import com.example.dao.EmployeeDAO;
import com.example.model.Employee;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EmployeeDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employee_summary";
        return jdbcTemplate.query(sql, new EmployeeRowMapper());
    }

    @Override
    public Employee findById(int id) {
        String sql = "SELECT * FROM employees WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Employee.class));
    }

    @Override
    public void save(Employee employee) {
        String sql = "INSERT INTO employees (first_name, last_name, email, department) VALUES (:firstName, :lastName, :email, :department)";
        SqlParameterSource params = new BeanPropertySqlParameterSource(employee);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void saveBatch(List<Employee> employees) {
        String sql = "INSERT INTO employees (first_name, last_name, email, department) VALUES (:firstName, :lastName, :email, :department)";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(employees.toArray());
        jdbcTemplate.batchUpdate(sql, batch);
    }

    @Override
    public void update(Employee employee) {
        String sql = "UPDATE employees SET first_name = :firstName, last_name = :lastName, email = :email, department = :department WHERE id = :id";
        SqlParameterSource params = new BeanPropertySqlParameterSource(employee);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM employees WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Employee getEmployeeDetailsViaStoredProcedure(int id) {
        String sql = "CALL GetEmployeeDetails(:id)";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        try {
            return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Employee.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Custom RowMapper for Employee
    private static class EmployeeRowMapper implements RowMapper<Employee> {
        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employee employee = new Employee();
            employee.setId(rs.getInt("id"));
            // Split full_name into first_name and last_name
            String fullName = rs.getString("full_name");
            if (fullName != null) {
                String[] names = fullName.split(" ", 2);
                employee.setFirstName(names[0]);
                employee.setLastName(names.length > 1 ? names[1] : "");
            }
            employee.setEmail(rs.getString("email"));
            employee.setDepartment(rs.getString("department"));
            return employee;
        }
    }
}