package com.mrsisa.pharmacy.tasks;


import com.mrsisa.pharmacy.service.IPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableAsync
@Component
public class DeleteUnverifiedUsers {

    private final IPatientService patientService;

    @Autowired
    public DeleteUnverifiedUsers(IPatientService patientService) {
        this.patientService = patientService;
    }

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteUnverifiedUsers(){
        this.patientService.deleteUnverifiedUsers();
    }
}
