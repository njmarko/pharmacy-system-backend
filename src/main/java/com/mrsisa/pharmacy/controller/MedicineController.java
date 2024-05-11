package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwnsEntity;
import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.dto.medicine.MedicineDTO;
import com.mrsisa.pharmacy.dto.medicine.MedicineDetailsDTO;
import com.mrsisa.pharmacy.dto.medicine.MedicineRegistrationDTO;
import com.mrsisa.pharmacy.dto.medicine.MedicineSearchDTO;
import com.mrsisa.pharmacy.service.IMedicineService;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value= "/api/medicines")
public class MedicineController {

    private final IMedicineService medicineService;
    private final IConverter<Medicine, MedicineDTO> toMedicineDTO;
    private final IConverter<Medicine, MedicineDetailsDTO> toMedicineDetailsDTO;


    @Autowired
    public MedicineController(IMedicineService medicineService, IConverter<Medicine, MedicineDTO> toMedicineDTO,
                              IConverter<Medicine, MedicineDetailsDTO> toMedicineDetailsDTO){
        this.medicineService = medicineService;
        this.toMedicineDTO = toMedicineDTO;
        this.toMedicineDetailsDTO = toMedicineDetailsDTO;
    }


    @GetMapping
    public Page<MedicineDTO> getMedicines(@Valid MedicineSearchDTO searchDTO, @PageableDefault Pageable pageAndSortParams) {
        return this.medicineService.getAllMedicineSearchAndFilter(searchDTO.getName(), searchDTO.getLowGrade(), searchDTO.getHighGrade(),
                searchDTO.getIssueOnRecipe(), searchDTO.getMedicineType(), pageAndSortParams).map(toMedicineDTO::convert);
    }

    @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_PHARMACY_ADMIN')")
    @GetMapping("/all")
    public List<MedicineDTO> getAllMedicine(){
        List<MedicineDTO> medicineDTOS = new ArrayList<>();
        for(Medicine medicine : this.medicineService.getAllMedicine()) {
            medicineDTOS.add(new MedicineDTO(medicine.getId(), medicine.getName(), medicine.getCode(), medicine.getMedicineType(), medicine.getAverageGrade(), medicine.getIssueOnRecipe()));
        }
        return medicineDTOS;
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicineDetailsDTO registerMedicine(@Valid @RequestBody MedicineRegistrationDTO dto){
            Medicine medicine;
            try{
                medicine = this.medicineService.registerMedicine(dto.getCode(), dto.getName(), dto.getMedicineShape(), dto.getMedicineType(),
                        dto.getComposition(), dto.getManufacturer(), dto.getIssueOnRecipe(), dto.getAdditionalNotes(), dto.getPoints(),dto.getReplacements());
            } catch (DataIntegrityViolationException ex){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine with code " + dto.getCode() + " already exists.");
            }

            return toMedicineDetailsDTO.convert(medicine);

    }

    @GetMapping(value = "/{id}")
    public MedicineDetailsDTO getMedicineDetails(@PathVariable("id") Long id){
        var medicine = this.medicineService.getMedicine(id);
        return toMedicineDetailsDTO.convert(medicine);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsEntity(entityId = "orderId", ownerField = "pharmacyAdmin", entity = Order.class)
    @GetMapping(value = "/not-in-order/{orderId}")
    public Page<MedicineDTO> findAllNotInOrder(@PathVariable("orderId") Long orderId, @Valid MedicineSearchDTO searchDTO, @PageableDefault Pageable pageable) {
        Page<Medicine> medicinePage = medicineService.getAllNotInOrder(orderId, searchDTO.getName(), searchDTO.getLowGrade(), searchDTO.getHighGrade(),
                searchDTO.getIssueOnRecipe(), searchDTO.getMedicineType(), pageable);
        return medicinePage.map(toMedicineDTO::convert);
    }
}