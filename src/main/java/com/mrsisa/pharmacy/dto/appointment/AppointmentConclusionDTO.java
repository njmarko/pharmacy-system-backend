package com.mrsisa.pharmacy.dto.appointment;

import com.mrsisa.pharmacy.dto.stock.MedicineStockConcludeDTO;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class AppointmentConclusionDTO {
    @NotBlank(message = "Report text can not be empty.")
    private String reportText;

    @NotNull(message = "Pharmacy id cant be null.")
    @Min(value = 0, message = "Pharmacy id cant be less than zero.")
    private Long pharmacyId;

    @NotNull(message = "Patient id cant be null.")
    @Min(value = 0, message = "Patient id cant be less than zero.")
    private Long patientId;

    @NotNull(message = "Appointment id cant be null.")
    @Min(value = 0, message = "Appointment id cant be less than zero.")
    private Long appointmentId;

    private Long employeeId;

    private List<MedicineStockConcludeDTO> medicineStocks = new ArrayList<>();
}
