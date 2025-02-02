package com.anup.bgu.admin.service;

import com.anup.bgu.admin.dto.AuthRequest;
import com.anup.bgu.admin.dto.AuthResponse;
import com.anup.bgu.exceptions.models.BadCredentialException;
import com.anup.bgu.security.service.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AdminService {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager manager;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        try {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(request.email(), request.password());
            manager.authenticate(authentication);
            log.info("login()-> Login success! Email: {} , Password: {}",request.email(),request.password());
        } catch (AuthenticationException e) {
            log.debug("login()-> Login Failed! Email: {} , Password: {}",request.email(),request.password());
            throw new BadCredentialException("Bad Credential! Please try again.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }
}
