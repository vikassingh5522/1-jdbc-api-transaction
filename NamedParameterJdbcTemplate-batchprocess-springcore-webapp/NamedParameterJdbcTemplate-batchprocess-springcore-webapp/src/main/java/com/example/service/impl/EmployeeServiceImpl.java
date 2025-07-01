package com.example.service.impl;

import com.example.dao.EmployeeDAO;
import com.example.model.Employee;
import com.example.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeServiceImpl(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @Override
    public List<Employee> findAll() {
        return employeeDAO.findAll();
    }

    @Override
    public Employee findById(int id) {
        return employeeDAO.findById(id);
    }

    @Override
    public void save(Employee employee) {
        employeeDAO.save(employee);
    }

    @Override
    public void saveBatch(List<Employee> employees) {
        employeeDAO.saveBatch(employees);
    }

    @Override
    public void update(Employee employee) {
        employeeDAO.update(employee);
    }

    @Override
    public void delete(int id) {
        employeeDAO.delete(id);
    }

    @Override
    public Employee getEmployeeDetailsViaStoredProcedure(int id) {
        return employeeDAO.getEmployeeDetailsViaStoredProcedure(id);
    }
}