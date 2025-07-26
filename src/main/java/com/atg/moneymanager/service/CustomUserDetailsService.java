package com.atg.moneymanager.service;

import com.atg.moneymanager.Model.ProfileEntity;
import com.atg.moneymanager.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private ProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<ProfileEntity> profile=profileRepository.findByEmail(email);
        if(profile.isEmpty()){
            throw new UsernameNotFoundException("Invalid email or password");
        }
        return User.builder()
                .username(profile.get().getEmail())
                .password(profile.get().getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
