package com.mrsisa.pharmacy.aspect.impl;

import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import com.mrsisa.pharmacy.domain.entities.User;
import com.mrsisa.pharmacy.service.ICRUDService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

@Service
public class OwnerSetHelper {

    @Transactional
    public <T extends BaseEntity> void throwIfNotOwner(ICRUDService<T> service, Long id, Method ownerMethod, User user) throws InvocationTargetException, IllegalAccessException {
        var entity = service.get(id);
        Set<?> owners = (Set<?>) ownerMethod.invoke(entity);
        owners.stream().filter(o -> {
           User owner = (User) o;
           return owner.getActive() && owner.getId().equals(user.getId());
        }).findAny().orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions to access this data."));
    }

}
