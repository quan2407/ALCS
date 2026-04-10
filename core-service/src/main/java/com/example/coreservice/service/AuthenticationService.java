package com.example.coreservice.service;

import com.example.coreservice.dto.request.AuthenticationRequest;
import com.example.coreservice.dto.request.RegisterRequest;
import com.example.coreservice.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}