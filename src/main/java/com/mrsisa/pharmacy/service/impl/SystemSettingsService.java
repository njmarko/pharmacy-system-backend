package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.valueobjects.SystemSettings;
import com.mrsisa.pharmacy.repository.ISystemSettingsRepository;
import com.mrsisa.pharmacy.service.ISystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class SystemSettingsService extends JPAService<SystemSettings> implements ISystemSettingsService {

    private final ISystemSettingsRepository systemSettingsRepository;

    @Autowired
    public SystemSettingsService(ISystemSettingsRepository systemSettingsRepository) {
        this.systemSettingsRepository = systemSettingsRepository;
    }


    @Override
    public SystemSettings save(SystemSettings entity) {
        return this.systemSettingsRepository.save(entity);
    }

    @Override
    public SystemSettings update(SystemSettings entity) {
        return this.systemSettingsRepository.save(entity);
    }

    @Override
    protected JpaRepository<SystemSettings, Long> getEntityRepository() {
        return this.systemSettingsRepository;
    }

    @Override
    public SystemSettings findById(Long id) {
        Optional<SystemSettings> optionalSystemSettings = this.systemSettingsRepository.findById(id);
        if(optionalSystemSettings.isEmpty())
            return null;
        return optionalSystemSettings.get();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = ResponseStatusException.class)
    public SystemSettings updateSystemSettings(Integer dermatologistPoints, Integer pharmacistPoints) {
        var systemSettings = this.systemSettingsRepository.findByIdForUpdate(1L);
        if(systemSettings == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No setting configuration found.");

        systemSettings.setDermatologistAppointmentPoints(dermatologistPoints);
        systemSettings.setPharmacistAppointmentPoints(pharmacistPoints);
        return systemSettings;
    }
}
