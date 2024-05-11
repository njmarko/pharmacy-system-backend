package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.domain.entities.Authority;
import com.mrsisa.pharmacy.dto.*;
import com.mrsisa.pharmacy.security.util.JwtUtil;
import com.mrsisa.pharmacy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value= "/api/users")
public class UserController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(IUserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @OwningUser
    @PostMapping("/{id}/password")
    @ResponseStatus(value = HttpStatus.OK,reason = "Password updated successfully!")
    public void updatePassword(@PathVariable Long id, @RequestBody @Valid PasswordUpdateDTO passwordDTO){
        this.userService.updatePassword(id, passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
    }

    @OwningUser
    @PostMapping("/{id}/first-password")
    public void firstPasswordChange(@PathVariable("id") Long id, @RequestBody @Valid PasswordUpdateDTO dto){
        var user = this.userService.findById(id);
        if(!user.getPassword().equals(dto.getOldPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect.");
        user.setPassword(dto.getNewPassword());
        user.setLoggedIn(true);
        this.userService.save(user);
    }



    @PostMapping(value = "/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public AuthTokenDTO createAuthenticationToken(@Valid @RequestBody LoginUserDTO loginUserDTO)  {
        var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword())
            );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(authentication);
        String username = jwtUtil.extractUsernameFromToken(token);
        try {
            var user = userService.findByUsernameWithAuthorities(username);
            List<String> authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList());
            return new AuthTokenDTO(token, username, user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getLoggedIn(),authorities);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }





}