package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.valueobjects.SystemSettings;

public interface ISystemSettingsService extends IJPAService<SystemSettings>{


    //id ce uvijek bit 1, jer ce ova tabela imat samo 1 red, nisam znao kako ovo drugacije da cuvam
    SystemSettings findById(Long id);

    SystemSettings updateSystemSettings(Integer dermatologistPoints, Integer pharmacistPoints);
}
