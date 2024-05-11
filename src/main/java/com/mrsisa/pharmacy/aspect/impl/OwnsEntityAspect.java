package com.mrsisa.pharmacy.aspect.impl;

import com.mrsisa.pharmacy.aspect.OwnsEntity;
import com.mrsisa.pharmacy.domain.entities.BaseEntity;
import com.mrsisa.pharmacy.domain.entities.User;
import com.mrsisa.pharmacy.service.ICRUDService;
import com.mrsisa.pharmacy.service.impl.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@Aspect
@Component
public class OwnsEntityAspect extends OwningAspectBase {

    private final UserService userService;
    private final OwnerSetHelper ownerSetHelper;
    private final ApplicationContext applicationContext;

    @Autowired
    public OwnsEntityAspect(UserService userService, OwnerSetHelper ownerSetHelper, ApplicationContext applicationContext) {
        this.userService = userService;
        this.ownerSetHelper = ownerSetHelper;
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    @Before("@annotation(com.mrsisa.pharmacy.aspect.OwnsEntity)")
    public <T extends BaseEntity> void checkEntityOwner(JoinPoint joinPoint) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Long entityId = (Long) getParameter(joinPoint, OwnsEntity.class, "entityId");
        OwnsEntity annotation = getMethodAnnotation(joinPoint, OwnsEntity.class);
        String ownerField = annotation.ownerField();
        Class<T> entityClass = (Class<T>) annotation.entity();
        String[] crudServices = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(ICRUDService.class, entityClass));
        ICRUDService<T> entityService = (ICRUDService<T>) applicationContext.getBean(crudServices[0]);
        var field = entityClass.getDeclaredField(ownerField);
        var ownerGetter = entityClass.getMethod(getFieldGetter(field));
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.findByUsernameWithAuthorities(authentication.getName());
        if (Set.class.isAssignableFrom(field.getType())) {
            ownerSetHelper.throwIfNotOwner(entityService, entityId, ownerGetter, user);
        } else {
            var entity = entityService.get(entityId);
            User owner = (User) ownerGetter.invoke(entity);
            if (!user.getId().equals(owner.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions to access this data.");
            }
        }
    }

    private String getFieldGetter(Field field) {
        return "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }
}
