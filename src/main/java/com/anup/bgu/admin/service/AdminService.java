package com.anup.bgu.admin.service;

import com.anup.bgu.admin.dto.AuthRequest;
import com.anup.bgu.admin.dto.AuthResponse;
import com.anup.bgu.security.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager manager;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());
        manager.authenticate(authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }

}
