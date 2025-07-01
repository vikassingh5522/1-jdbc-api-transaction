package com.example.controller;

import com.example.model.Employee;
import com.example.model.EmployeeBatchForm;
import com.example.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.*;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "employee-list";
    }

    @GetMapping("/employee/new")
    public String showEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee-form";
    }

    @PostMapping("/employee/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee, RedirectAttributes redirectAttributes) {
        try {
            if (employee.getId() != null && employee.getId() > 0) {
                // Update existing employee
                employeeService.update(employee);
                redirectAttributes.addFlashAttribute("message", "Employee updated successfully!");
            } else {
                // Save new employee
                employeeService.save(employee);
                redirectAttributes.addFlashAttribute("message", "Employee added successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving employee: " + e.getMessage());
        }
        return "redirect:/";
    }
/*
    @GetMapping("/employee/batch")
    public String showBatchForm(Model model) {
        // Initialize with a few empty Employee objects for the form
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            employees.add(new Employee());
        }
        model.addAttribute("employees", employees);
        model.addAttribute("formTitle", "Add Batch Employees");
        return "employee-batch-form";
    }

    @PostMapping("/employee/batchSave")
    public String saveBatchEmployees(@ModelAttribute("employees") List<Employee> employees, RedirectAttributes redirectAttributes) {
        try {
            // Filter out any empty employee entries
            List<Employee> validEmployees = employees.stream()
                    .filter(emp -> emp.getFirstName() != null && !emp.getFirstName().isEmpty())
                    .toList();
            if (validEmployees.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No valid employees provided for batch save.");
            } else {
                employeeService.saveBatch(validEmployees);
                redirectAttributes.addFlashAttribute("message", "Batch employees added successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving batch employees: " + e.getMessage());
        }
        return "redirect:/";
    }
*/
@GetMapping("/employee/batch")
public String showBatchForm(Model model) {
    EmployeeBatchForm batchForm = new EmployeeBatchForm();
    List<Employee> employees = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
        employees.add(new Employee());
    }
    batchForm.setEmployees(employees);
    model.addAttribute("batchForm", batchForm);
    model.addAttribute("formTitle", "Add Batch Employees");
    return "employee-batch-form";
}

    @PostMapping("/employee/batchSave")
    public String saveBatchEmployees(@ModelAttribute("batchForm") EmployeeBatchForm batchForm, RedirectAttributes redirectAttributes) {
        try {
            List<Employee> validEmployees = batchForm.getEmployees().stream()
                    .filter(emp -> emp.getFirstName() != null && !emp.getFirstName().isEmpty())
                    .toList();
            if (validEmployees.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No valid employees provided for batch save.");
            } else {
                employeeService.saveBatch(validEmployees);
                redirectAttributes.addFlashAttribute("message", "Batch employees added successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving batch employees: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/employee/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        model.addAttribute("employee", employeeService.findById(id));
        return "employee-form";
    }

    @GetMapping("/employee/delete/{id}")
    public String deleteEmployee(@PathVariable("id") int id) {
        employeeService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/employee/details/{id}")
    public String showEmployeeDetails(@PathVariable("id") int id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeDetailsViaStoredProcedure(id));
        return "employee-details";
    }
}