package com.mrsisa.pharmacy.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientCategoryDTO {
    private String name;
    private Integer points;
    private Integer discount;
    private String color;

}
