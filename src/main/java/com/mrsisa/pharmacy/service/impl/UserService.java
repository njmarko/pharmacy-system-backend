package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.User;
import com.mrsisa.pharmacy.repository.IUserRepository;
import com.mrsisa.pharmacy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService extends JPAService<User> implements IUserService, UserDetailsService {
    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected JpaRepository<User, Long> getEntityRepository() {
        return userRepository;
    }

    @Override
    public User findByUsernameWithAuthorities(String username) {
        return userRepository.findByUsernameFetchAuthorities(username).orElseThrow(() -> new UsernameNotFoundException("Invalid username: " + username));
    }

    public User findById(Long id) {
        Optional<User> optionalUser = this.userRepository.findByIdAndActiveTrue(id);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + " does not exist.");
        return optionalUser.get();
    }

    @Override
    public User updatePassword(Long id, String oldPassword, String newPassword) {
        Optional<User> found = this.userRepository.findById(id);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the id" + id + " does not exist and can't be updated!");
        }
        var oldUser = found.get();

        if (!oldUser.getPassword().equals(oldPassword.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect!");
        }

        oldUser.setPassword(newPassword);

        return this.userRepository.save(oldUser);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = findByUsernameWithAuthorities(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.getAuthorities());
    }
}
