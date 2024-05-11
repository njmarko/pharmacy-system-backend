package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.PatientCategory;
import com.mrsisa.pharmacy.repository.IPatientCategoryRepository;
import com.mrsisa.pharmacy.repository.IPatientRepository;
import com.mrsisa.pharmacy.service.IPatientCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PatientCategoryService extends JPAService<PatientCategory> implements IPatientCategoryService {
    private final IPatientCategoryRepository patientCategoryRepository;
    private final IPatientRepository patientRepository;
    private static final String ALREADY_EXISTS = " already exists.";

    @Autowired
    public PatientCategoryService(IPatientCategoryRepository patientCategoryRepository, IPatientRepository patientRepository) {
        this.patientCategoryRepository = patientCategoryRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    protected JpaRepository<PatientCategory, Long> getEntityRepository() {
        return patientCategoryRepository;
    }


    @Override
    public Optional<PatientCategory> findByName(String name) {
        return this.patientCategoryRepository.findByName(name);
    }

    @Override
    public Optional<PatientCategory> findByPoints(Integer points) {
        return this.patientCategoryRepository.findByPoints(points);
    }

    @Override
    public Optional<PatientCategory> findByColor(String color) {
        return this.patientCategoryRepository.findByColor(color);
    }

    @Override
    public Optional<PatientCategory> findByDiscount(Integer discount) {
        return this.patientCategoryRepository.findByDiscount(discount);
    }

    @Override
    public Optional<PatientCategory> findById(Long id) {
        return this.patientCategoryRepository.findByIdAndActiveTrue(id);
    }


    @Override
    public PatientCategory createCategory(String name, Integer points, Integer discount, String color) {
        if(this.findByName(name).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with name " + name + ALREADY_EXISTS);
        if(this.findByPoints(points).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with points " + points + ALREADY_EXISTS);
        if(this.findByDiscount(discount).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with discount " + discount + ALREADY_EXISTS);
        if(this.findByColor(color).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with color " + discount + ALREADY_EXISTS);
        var category = new PatientCategory(name, points, discount, color);
        this.patientCategoryRepository.save(category);
        return category;
    }

    @Override
    public PatientCategory updateCategory(PatientCategory category, String name, Integer points, Integer discount, String color) {
        Optional<PatientCategory> foundByName = this.findByName(name);
        if(category.getName().equals("Default category") && category.getPoints().equals(0) && category.getDiscount().equals(0) && category.getColor().equals("#ffffff"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot update the default category.");
        if(foundByName.isPresent()){
            var patientCategory = foundByName.get();
            if(!patientCategory.getId().equals(category.getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with name " + name + ALREADY_EXISTS);
        }

        Optional<PatientCategory> foundByPoints = this.findByPoints(points);
        if(foundByPoints.isPresent()){
            var patientCategory = foundByPoints.get();
            if(!patientCategory.getId().equals(category.getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with points " + points + ALREADY_EXISTS);
        }

        Optional<PatientCategory> foundByDiscount = this.findByDiscount(discount);
        if(foundByDiscount.isPresent()){
            var patientCategory = foundByDiscount.get();
            if(!patientCategory.getId().equals(category.getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with discount " + discount + ALREADY_EXISTS);
        }

        Optional<PatientCategory> foundByColor = this.findByColor(color);
        if(foundByColor.isPresent()) {
            var patientCategory = foundByColor.get();
            if(!patientCategory.getId().equals(category.getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with color " + color + ALREADY_EXISTS);
        }
        category.setName(name);
        category.setPoints(points);
        category.setDiscount(discount);
        category.setColor(color);
        this.patientCategoryRepository.save(category);
        return category;
    }

    @Override
    public int deleteCategory(Long id) {
        if(this.patientRepository.countPatientWithCategoryId(id) != 0)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this category because there are patients that belong to it.");
        return this.patientCategoryRepository.deletePatientCategoryById(id);
    }

    @Override
    public Page<PatientCategory> getCategories(Pageable pageable) {
        return this.patientCategoryRepository.getCategories(pageable);
    }

    @Override
    public PatientCategory getNextCategory(Integer points) {
        List<PatientCategory> patientCategoryList = this.patientCategoryRepository.getNextCategories(points);
        if(patientCategoryList.isEmpty())
            return null;
        return patientCategoryList.get(0);
    }

}
