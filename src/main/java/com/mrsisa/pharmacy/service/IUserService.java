package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.User;

public interface IUserService extends IJPAService<User> {
    User findByUsernameWithAuthorities(String username);

    User updatePassword(Long id, String oldPassword, String newPassword);

    User findById(Long id);
}
