package com.sophie.api.tinderaibackend.profiles;

import org.springframework.data.annotation.Id;

public record Profile (
        String id,
        String firstName,
        String lastName,
        int age,
        String ethnicity,
        Gender gender,
        String bio,
        String imageUrl,
        String myerBriggsPersonalType
){}