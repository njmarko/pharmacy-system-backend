package com.mrsisa.pharmacy.domain.valueobjects;


import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
public class SystemSettings extends BaseEntity {

    @Column
    private Integer dermatologistAppointmentPoints;

    @Column
    private Integer pharmacistAppointmentPoints;

    public SystemSettings(){
        this.setDermatologistAppointmentPoints(0);
        this.setPharmacistAppointmentPoints(0);
    }

    public SystemSettings(Integer dermatologistAppointmentPoints, Integer pharmacistAppointmentPoints) {
        this.dermatologistAppointmentPoints = dermatologistAppointmentPoints;
        this.pharmacistAppointmentPoints = pharmacistAppointmentPoints;
    }

}
