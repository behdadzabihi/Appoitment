package com.blubank.doctorappointment.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("doctor".equals(username)) {
            return User.withUsername("doctor")
                    .password("{noop}password") // Use a real password encoder in production
                    .roles("ROLE_DOCTOR")
                    .build();
        } else if ("patient".equals(username)) {
            return User.withUsername("patient")
                    .password("{noop}password") // Use a real password encoder in production
                    .roles("ROLE_PATIENT")
                    .build();
        }
        else{
            log.error("User" + username + " not found");
            throw new UsernameNotFoundException("User not found");
        }
    }
}
