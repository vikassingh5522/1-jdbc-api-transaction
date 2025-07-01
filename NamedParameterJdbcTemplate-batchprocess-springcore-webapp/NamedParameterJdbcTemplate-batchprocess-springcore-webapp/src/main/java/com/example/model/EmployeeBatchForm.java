package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class EmployeeBatchForm {
    private List<Employee> employees = new ArrayList<>();

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}