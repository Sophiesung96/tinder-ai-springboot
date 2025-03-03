package com.sophie.api.tinderaibackend.profiles;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.stabilityai.StabilityAiImageClient;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.sophie.api.tinderaibackend.Utils.selfieTypes;
@Service
public class ProfileCreationService {

    private final StabilityAiImageClient stabilityaiImageModel;

    private static final String PROFILES_FILE_PATH = "profiles.json";

    @Value("${startup-actions.initializeProfiles}")
    private Boolean initializeProfiles;

    @Value("${tinderai.lookingForGender}")
    private String lookingForGender;

    @Value("#{${tinderai.character.user}}")
    private Map<String, String> userProfileProperties;

    private ProfileRepository profileRepository;

    public ProfileCreationService(StabilityAiImageClient stabilityaiImageModel,ProfileRepository profileRepository) {
        this.stabilityaiImageModel = stabilityaiImageModel;
        this.profileRepository = profileRepository;
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public Profile generateProfileImage(Profile profile) {
        String uuid = StringUtils.isBlank(profile.id()) ? UUID.randomUUID().toString() : profile.id();
        profile = new Profile(
                uuid, profile.firstName(), profile.lastName(), profile.age(),
                profile.ethnicity(), profile.gender(), profile.bio(),
                uuid + ".jpg", profile.myersBriggsPersonalityType()
        );

        String randomSelfieType = getRandomElement(selfieTypes());


        String prompt = String.format(
                "Selfie of a %d year old %s %s %s, %s, photorealistic skin texture and details, individual hairs and pores visible, highly detailed, photorealistic, hyperrealistic, subsurface scattering, 4k DSLR, ultrarealistic, best quality, masterpiece. Bio- %s",
                profile.age(), profile.myersBriggsPersonalityType(), profile.ethnicity(),
                profile.gender(), randomSelfieType, profile.bio()
        );

        System.out.println("Creating image for " + profile.firstName() + " " + profile.lastName() + " (" + profile.ethnicity() + ")");


        ImageResponse response = stabilityaiImageModel.call(
                new ImagePrompt(prompt,
                        StabilityAiImageOptions.builder()
                                .withStylePreset("cinematic")
                                .withN(1)  // Generate 1 image
                                .withHeight(1024)
                                .withWidth(1024)
                                .build())
        );

        if (response.getResults() != null && !response.getResults().isEmpty()) {
            ImageGeneration imageGen = response.getResults().get(0);
            String base64Image = imageGen.getOutput().getB64Json();  // Ensure `getB64Json()` exists

            if (base64Image != null && !base64Image.isEmpty()) {
                saveImageToFile(base64Image, profile.imageUrl());
            } else {
                System.out.println("No valid image data received.");
            }
        } else {
            System.out.println("No image generated.");
        }

        return profile;
    }

    public void saveImageToFile(String base64Image, String filename) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String directoryPath = "src/main/resources/static/images/";
            Path directory = Path.of(directoryPath);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            String filePath = directoryPath + filename;
            try (FileOutputStream imageOutFile = new FileOutputStream(filePath)) {
                imageOutFile.write(imageBytes);
                System.out.println("Image successfully saved: " + filePath);
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Error saving image file", e);
        }
    }

    public void saveProfilesToDB() {
        Gson gson = new Gson();
        try {
            List<Profile> existingProfiles = gson.fromJson(
                    new FileReader(PROFILES_FILE_PATH),
                    new TypeToken<ArrayList<Profile>>() {}.getType()
            );
            profileRepository.deleteAll();
            profileRepository.saveAll(existingProfiles);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Profile profile = new Profile(
                userProfileProperties.get("id"),
                userProfileProperties.get("firstName"),
                userProfileProperties.get("lastName"),
                Integer.parseInt(userProfileProperties.get("age")),
                userProfileProperties.get("ethnicity"),
                Gender.valueOf(userProfileProperties.get("gender")),
                userProfileProperties.get("bio"),
                userProfileProperties.get("imageUrl"),
                userProfileProperties.get("myersBriggsPersonalityType")
        );
        System.out.println(userProfileProperties);
        profileRepository.save(profile);

    }




}
