package com.example.dao;

import com.example.model.Employee;
import java.util.List;

public interface EmployeeDAO {
    List<Employee> findAll();
    Employee findById(int id);
    void save(Employee employee);
    void saveBatch(List<Employee> employees);
    void update(Employee employee);
    void delete(int id);
    Employee getEmployeeDetailsViaStoredProcedure(int id);
}