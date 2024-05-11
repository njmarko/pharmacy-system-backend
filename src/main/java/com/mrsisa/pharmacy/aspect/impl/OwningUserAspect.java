package com.mrsisa.pharmacy.aspect.impl;

import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.service.IUserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class OwningUserAspect extends OwningAspectBase {
    private final IUserService userService;

    @Autowired
    public OwningUserAspect(IUserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(com.mrsisa.pharmacy.aspect.OwningUser)")
    public void userOwnsResource(JoinPoint joinPoint) {
        Long userId = getIdentityParameter(joinPoint, OwningUser.class);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.findByUsernameWithAuthorities(authentication.getName());
        if (!user.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions to access this data.");
        }
    }

}
