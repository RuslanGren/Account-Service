package com.example.accountservice.security;


import com.example.accountservice.models.user.User;
import com.example.accountservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmailIgnoreCase(email.toLowerCase());

        return user.map(UserInfo::new).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

