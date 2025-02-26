package com.sophie.api.tinderaibackend;

import com.sophie.api.tinderaibackend.profiles.Gender;
import com.sophie.api.tinderaibackend.profiles.Profile;
import com.sophie.api.tinderaibackend.profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TinderAiBackendApplication implements CommandLineRunner {
    @Autowired
    ProfileRepository profileRepository;
    public static void main(String[] args) {


        SpringApplication.run(TinderAiBackendApplication.class, args);

    }

    public void run (String... args) throws Exception {
       Profile profile = new Profile(
               "1",
               "Koushik",
               "KOthgal",
               40,
               "Indian",
               Gender.MALE,
               "Software Developer",
               "foo.jpg",
               "INTP"
       );
       profileRepository.save(profile);
       profileRepository.findAll().forEach(System.out::println);
    }

}
