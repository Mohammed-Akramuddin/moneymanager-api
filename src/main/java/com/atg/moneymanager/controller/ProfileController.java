package com.atg.moneymanager.controller;

import com.atg.moneymanager.dto.AuthDTO;
import com.atg.moneymanager.dto.CategoryDTO;
import com.atg.moneymanager.dto.ProfileDTO;
import com.atg.moneymanager.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> register(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registerProfile= profileService.register(profileDTO);
        return ResponseEntity.ok(registerProfile);
    }
    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam("token") String activationToken) {
        boolean result = profileService.activateProfile(activationToken);
        if (result) {
            return ResponseEntity.ok("Activated");
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        try {
            if(!profileService.isAccountActivated(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account is not activated"));
            }
            Map<String,Object> map = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(map);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @GetMapping("/test")
    public String test(){
        return "test successful";
    }

}
