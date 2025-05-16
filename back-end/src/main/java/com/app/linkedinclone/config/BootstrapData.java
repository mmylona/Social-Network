package com.app.linkedinclone.config;


import com.app.linkedinclone.model.dao.Role;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.enums.RoleName;
import com.app.linkedinclone.repository.RoleRepository;
import com.app.linkedinclone.repository.UserRepository;
import com.app.linkedinclone.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class BootstrapData {

    @Bean
    CommandLineRunner run(UserService userService, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if(roleRepository.findAll().size() == 0){
                userService.saveRole(new Role(RoleName.USER));
                userService.saveRole(new Role(RoleName.ADMIN));
                User adminUser = new User("admin@gmail.com", passwordEncoder.encode("adminPassword12!"), new ArrayList<>());
                userService.saverUser(adminUser);

                Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN);
                User user = userRepository.findByEmail("admin@gmail.com").orElse(null);
                if (user != null && adminRole != null) {
                    user.getRoles().add(adminRole);
                    userService.saverUser(user);
                }
            }

        };
    }
}