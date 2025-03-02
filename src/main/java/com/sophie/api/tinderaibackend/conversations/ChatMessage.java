package com.sophie.api.tinderaibackend.conversations;

import com.sophie.api.tinderaibackend.profiles.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ChatMessage(
        @NotBlank(message = "Message text cannot be blank")
        String messageText,
        @NotBlank(message = "Author Id cannot be blank")
        String authorId,

        LocalDateTime messageTime


) {

}
