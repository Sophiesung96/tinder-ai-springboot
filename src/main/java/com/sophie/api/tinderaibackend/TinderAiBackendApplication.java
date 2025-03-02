package com.sophie.api.tinderaibackend;

import com.sophie.api.tinderaibackend.profiles.Gender;
import com.sophie.api.tinderaibackend.profiles.Profile;
import com.sophie.api.tinderaibackend.profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TinderAiBackendApplication  {

    public static void main(String[] args) {


        SpringApplication.run(TinderAiBackendApplication.class, args);

    }



}
