package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.domain.entities.PatientCategory;
import com.mrsisa.pharmacy.dto.PatientCategoryDTO;
import com.mrsisa.pharmacy.service.IPatientCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RestController
@RequestMapping(value= "/api/patient-categories")
public class PatientCategoryController {

    private final IPatientCategoryService patientCategoryService;

    @Autowired
    public PatientCategoryController(IPatientCategoryService patientCategoryService){
        this.patientCategoryService = patientCategoryService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public Page<PatientCategory> getCategories(@PageableDefault Pageable pageable){
        return this.patientCategoryService.getCategories(pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public PatientCategory createCategory(@RequestBody PatientCategoryDTO dto){
        return this.patientCategoryService.createCategory(dto.getName(), dto.getPoints(), dto.getDiscount(), dto.getColor());
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public PatientCategory updateCategory(@PathVariable("id") Long id, @RequestBody PatientCategory dto){
        Optional<PatientCategory> optionalPatientCategory = this.patientCategoryService.findById(id);
        if(optionalPatientCategory.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with id " + id + " does not exist.");
        return this.patientCategoryService.updateCategory(optionalPatientCategory.get(), dto.getName(), dto.getPoints(), dto.getDiscount(), dto.getColor());

    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public int deleteCategory(@PathVariable("id") Long id){
        Optional<PatientCategory> optionalPatientCategory = this.patientCategoryService.findById(id);
        if(optionalPatientCategory.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with id " + id + " does not exist.");
        if(optionalPatientCategory.get().getName().equals("Default category"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete the default category.");
        return this.patientCategoryService.deleteCategory(id);
    }

}
