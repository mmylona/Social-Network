package com.app.linkedinclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.app.linkedinclone.repository"})
public class LinkedInCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkedInCloneApplication.class, args);
    }

}
