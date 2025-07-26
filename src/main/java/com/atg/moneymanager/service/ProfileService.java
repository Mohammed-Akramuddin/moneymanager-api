package com.atg.moneymanager.service;

import com.atg.moneymanager.Model.ProfileEntity;
import com.atg.moneymanager.dto.AuthDTO;
import com.atg.moneymanager.dto.ProfileDTO;
import com.atg.moneymanager.repository.ProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${app.activation.url}")
    private String activationURL;
    public ProfileDTO register(ProfileDTO profileDTO) {
        ProfileEntity profileEntity = toEntity(profileDTO);
        profileEntity.setActivationToken(UUID.randomUUID().toString());
        profileEntity = profileRepository.save(profileEntity);
        //sending message of activation
        String activationLink=activationURL+"/activate?token="+profileEntity.getActivationToken();
        String subject="Activate your Money Manager account";
        String body="Click the following link to activate your Money Manager account "+activationLink;
        emailService.sendMail(profileEntity.getEmail(),subject,body);
        return toDTO(profileEntity);
    }

    private ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    private ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }
    public boolean activateProfile(String token) {
        return profileRepository.findByActivationToken(token)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }
    public boolean isAccountActivated(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }
    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

    }
    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity profile=null;
        if(email==null){
            profile=getCurrentProfile();
        }
        else{
            profile=profileRepository.findByEmail(email)
                    .orElseThrow(()-> new UsernameNotFoundException("Invalid username or password"));
        }
        return toDTO(profile);
    }
    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            return Map.of(
                    "token",jwtService.generate(authDTO.getEmail()),
                    "user",getPublicProfile(authDTO.getEmail())
            );
        }
        catch (Exception e){
            throw  new RuntimeException("Invalid email or Password");
        }
    }
}
