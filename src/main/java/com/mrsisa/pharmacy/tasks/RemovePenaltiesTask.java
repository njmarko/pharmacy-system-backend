package com.mrsisa.pharmacy.tasks;

import com.mrsisa.pharmacy.service.impl.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableAsync
@Component
public class RemovePenaltiesTask {


    private final PatientService patientService;

    @Autowired
    public RemovePenaltiesTask(PatientService patientService) {
        this.patientService = patientService;
    }

    @Async
    @Scheduled(cron = "0 0 0 1 * *")
    public void RemovePenalties() {
        patientService.removeAllPenalties();
    }

}
